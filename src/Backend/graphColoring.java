package Backend;

import Assembly.*;
import Assembly.Instruction.*;
import Assembly.Operand.*;

import java.util.*;

public class graphColoring {
    private enum InWhichNodeSet {
        INITIAL, PRECOLORED, SIMPLIFYWORKLIST, FREEZEWORKLIST, SPILLEDNODES, COALESCEDNODES, COLOREDNODES, SELECTSTACK
    }

    private final int K = 32;   // 可分配的寄存器数
    private livenessAnalysis liveAnalysis;
    private AsmProgram program;
    private AsmFunc curFunc;
    private HashSet<Integer> preColored, initial, simplifyWorklist, freezeWorklist, spilledNodes, coalescedNodes, coloredNodes, spillWorklist;
    private LinkedList<Integer> selectStack;
    private HashSet<AsmInst> coalescedMoves, constrainedMoves, frozenMoves, worklistMoves, activeMove;
    private HashMap<Integer, reg> IntToReg;
    private HashSet<Integer> edgeSet;    // Edge (u -> v) : hash to (u * MaxNodeNumber + v)
    private ArrayList<Integer> degree, alias, color;
    private ArrayList<Double> Priority;
    private ArrayList<LinkedList<AsmInst>> moveList;    // 邻接链表, reg -> 'move' AsmInst
    private ArrayList<LinkedList<Integer>> edgeList;    // 邻接链表, 边集
    private ArrayList<InWhichNodeSet> inWhichNodeSets;  // 每个点的状态
    private int finalRegCount;
    private final double[] myExp = {0, 10, 1e2, 1e3, 1e4, 1e5, 1e6, 1e7, 1e8, 1e9, 1e10, 1e11, 1e12, 1e13, 1e14, 1e15, 1e16, 1e17, 1e18};
    private final physicalReg s0;
    private final ArrayList<physicalReg> phyRegs;
    private BitSet colorSet = new BitSet(K), callerSavedSet = new BitSet(K), calleeSavedUsed = new BitSet(32);
    // 每个物理寄存器就是一种颜色

    public graphColoring(AsmProgram program, AsmFunc curFunc, livenessAnalysis liveAnalysis) {
        this.program = program;
        this.curFunc = curFunc;
        this.liveAnalysis = liveAnalysis;
        preColored = new HashSet<>();
        for (int i = 0; i < K; ++i) preColored.add(i);
        initial = new HashSet<>();
        simplifyWorklist = new HashSet<>();
        freezeWorklist = new HashSet<>();
        spillWorklist = new HashSet<>();
        spilledNodes = new HashSet<>();
        coalescedNodes = new HashSet<>();
        coloredNodes = new HashSet<>();
        selectStack = new LinkedList<>();

        // 不同状态的 move 指令
        coalescedMoves = new HashSet<>();
        constrainedMoves = new HashSet<>();
        frozenMoves = new HashSet<>();
        worklistMoves = new HashSet<>();
        activeMove = new HashSet<>();

        s0 = program.physicalRegs.get(8);
        phyRegs = program.physicalRegs;
        colorSet.set(0, 5);
        colorSet.set(8);
        callerSavedSet.set(0, 8);
        callerSavedSet.set(10, 18);
        callerSavedSet.set(28, 32);
        curFunc.calleeSavedUsed = calleeSavedUsed;
    }

    public void work() {
        liveAnalysis.workFunc(curFunc);
        init();
        Build();
        MakeWorklist();
        do {
            if (!simplifyWorklist.isEmpty()) Simplify();
            else if (!worklistMoves.isEmpty()) Coalesce();
            else if (!freezeWorklist.isEmpty()) Freeze();
            else if (!spillWorklist.isEmpty()) SelectSpill();
            else break;
        } while (true);
        AssignColors();
        if (spilledNodes.isEmpty()) {   // legal
            Replace();
        } else {    // actual spill exists, redo
            RewriteProgram();
            work();
        }
    }

    private void init() {
        edgeSet = new HashSet<>();
        finalRegCount = curFunc.regCnt + 32;
        degree = new ArrayList<>(finalRegCount);
        alias = new ArrayList<>(finalRegCount);
        color = new ArrayList<>(finalRegCount);
        inWhichNodeSets = new ArrayList<>(finalRegCount);
        moveList = new ArrayList<>(finalRegCount);
        edgeList = new ArrayList<>(finalRegCount);
        Priority = new ArrayList<>(finalRegCount);
        for (int i = 0; i < 32; ++i) {
            inWhichNodeSets.add(InWhichNodeSet.PRECOLORED);     // 提前上好色
            color.add(i);
            degree.add(100000000);
            alias.add(i);
            Priority.add(0.0);
            moveList.add(new LinkedList<>());
            edgeList.add(new LinkedList<>());
        }
        for (int i = 32; i < finalRegCount; ++i) {
            inWhichNodeSets.add(i, InWhichNodeSet.INITIAL);
            color.add(-1);  // 没有颜色
            degree.add(0);
            alias.add(i);
            Priority.add(0.0);
            moveList.add(new LinkedList<>());
            edgeList.add(new LinkedList<>());
        }
        passTheFunc();
        for (int i = 32; i < finalRegCount; ++i) {
            if (IntToReg.containsKey(i)) initial.add(i);
        }
        for (int i = 0; i < 32; ++i) {  // 物理寄存器之间建边
            for (int j = 0; j < 32; ++j) {
                edgeSet.add(i * finalRegCount + j);
                edgeSet.add(j * finalRegCount + i);
                edgeList.get(i).add(j);
            }
        }
    }

    private void passTheFunc() {    // update Priority & IntToReg
        IntToReg = new HashMap<>();
        for (AsmBlock asmBlock : curFunc.blocks) {
            double loopWeight = (asmBlock.loopDepth > 18) ? 4e18 : myExp[asmBlock.loopDepth];
            int tmp;
            for (AsmInst i = asmBlock.headInst; i != null; i = i.next) {
                if (i instanceof branchAsm branch) {
                    tmp = branch.src1.getNumber();
                    IntToReg.put(tmp, branch.src1);
                    Priority.set(tmp, Priority.get(tmp) + loopWeight);
                    if (branch.src2 != null) {
                        tmp = branch.src2.getNumber();
                        IntToReg.put(tmp, branch.src2);
                        Priority.set(tmp, Priority.get(tmp) + loopWeight);
                    }
                } else if (i instanceof ITypeAsm it) {
                    tmp = it.rd.getNumber();
                    IntToReg.put(tmp, it.rd);
                    Priority.set(tmp, Priority.get(tmp) + loopWeight);
                    tmp = it.rs1.getNumber();
                    IntToReg.put(tmp, it.rs1);
                    Priority.set(tmp, Priority.get(tmp) + loopWeight);
                } else if (i instanceof laAsm la) {
                    tmp = la.rd.getNumber();
                    IntToReg.put(tmp, la.rd);
                    Priority.set(tmp, Priority.get(tmp) + loopWeight);
                } else if (i instanceof loadAsm ld) {
                    tmp = ld.rd.getNumber();
                    IntToReg.put(tmp, ld.rd);
                    Priority.set(tmp, Priority.get(tmp) + loopWeight);
                    tmp = ld.addr.getNumber();
                    IntToReg.put(tmp, ld.addr);
                    Priority.set(tmp, Priority.get(tmp) + loopWeight);
                } else if (i instanceof liAsm li) {
                    tmp = li.rd.getNumber();
                    IntToReg.put(tmp, li.rd);
                    Priority.set(tmp, Priority.get(tmp) + loopWeight);
                } else if (i instanceof luiAsm lui) {
                    tmp = lui.rd.getNumber();
                    IntToReg.put(tmp, lui.rd);
                    Priority.set(tmp, Priority.get(tmp) + loopWeight);
                } else if (i instanceof moveAsm mv) {
                    tmp = mv.rd.getNumber();
                    IntToReg.put(tmp, mv.rd);
                    Priority.set(tmp, Priority.get(tmp) + loopWeight);
                    tmp = mv.rs1.getNumber();
                    IntToReg.put(tmp, mv.rs1);
                    Priority.set(tmp, Priority.get(tmp) + loopWeight);
                } else if (i instanceof RTypeAsm r) {
                    tmp = r.rd.getNumber();
                    IntToReg.put(tmp, r.rd);
                    Priority.set(tmp, Priority.get(tmp) + loopWeight);
                    tmp = r.rs1.getNumber();
                    IntToReg.put(tmp, r.rs1);
                    Priority.set(tmp, Priority.get(tmp) + loopWeight);
                    tmp = r.rs2.getNumber();
                    IntToReg.put(tmp, r.rs2);
                    Priority.set(tmp, Priority.get(tmp) + loopWeight);
                } else if (i instanceof storeAsm st) {
                    tmp = st.rs.getNumber();
                    IntToReg.put(tmp, st.rs);
                    Priority.set(tmp, Priority.get(tmp) + loopWeight);
                    tmp = st.addr.getNumber();
                    IntToReg.put(tmp, st.addr);
                    Priority.set(tmp, Priority.get(tmp) + loopWeight);
                }
            }
        }
    }

    private void Build() {
        curFunc.blocks.forEach(asmBlock -> {
            BitSet live = (BitSet) asmBlock.tailInst.liveOut.clone();   // 表示当前 寄存器 的占用情况
            live.set(0,5);
            live.set(8);

            for (AsmInst i = asmBlock.tailInst; i != null; i = i.pre) {
                if (i instanceof branchAsm || i instanceof retAsm) {
                    live.or(i.liveOut);
                }
                if (i instanceof moveAsm mv) {
                    live.andNot(i.use);
                    moveList.get(mv.rd.getNumber()).add(i);
                    if (mv.rd != mv.rs1) {
                        moveList.get(mv.rs1.getNumber()).add(i);
                    }
                    worklistMoves.add(i);
                }
                live.or(i.def);
                // nextSetBit(i) : 返回从第 i 位往后的第一个 '1' 的 index, 不存在则为 -1
                // 此循环相当于遍历整个 Bitset
                for (int d = i.def.nextSetBit(0); d >= 0; d = i.def.nextSetBit(d + 1)) {
                    for (int l = live.nextSetBit(0); l >= 0; l = live.nextSetBit(l + 1)) {
                        AddEdge(l, d);
                    }
                }
                live.andNot(i.def);
                live.or(i.use);
            }
        });
    }

    private void AddEdge(int u, int v) {
        if (!edgeSet.contains(u * finalRegCount + v) && u != v) {   // 去重
            edgeSet.add(u * finalRegCount + v);
            edgeSet.add(v * finalRegCount + u);
            if (u >= 32) {            // u not in PRECOLORED
                edgeList.get(u).add(v);
                degree.set(u, degree.get(u) + 1);
            }
            if (v >= 32) {            //v not in PRECOLORED
                edgeList.get(v).add(u);
                degree.set(v, degree.get(v) + 1);
            }
        }
    }

    private void MakeWorklist() {
        initial.forEach(n -> {
            if (degree.get(n) >= K) spillWorklist.add(n);   // '可能' 的 spill
            else if (MoveRelated(n)) freezeWorklist.add(n); // 可能的 freeze
            else simplifyWorklist.add(n);
        });
        initial.clear();
    }

    private boolean MoveRelated(int n) {    // 存在 move 操作，实际上是对于 '一对点' 而言的关系
        return !moveList.get(n).isEmpty();
    }

    private void Simplify() {   // 从图上删除结点，并入栈
        Iterator<Integer> iter = simplifyWorklist.iterator();
        int n = iter.next();
        simplifyWorklist.remove(n);
        selectStack.push(n);
        inWhichNodeSets.set(n, InWhichNodeSet.SELECTSTACK);
        edgeList.get(n).forEach(m -> {
            InWhichNodeSet checkSet = inWhichNodeSets.get(m);
            if (checkSet != InWhichNodeSet.SELECTSTACK && !coalescedNodes.contains(m)) {
                DecrementDegree(m);
            }
        });
    }

    private void DecrementDegree(int m) {
        int d = degree.get(m);
        degree.set(m, d - 1);
        if (d == K) {
            //EnableMoves
            edgeList.get(m).forEach(n -> {
                InWhichNodeSet checkSet = inWhichNodeSets.get(n);
                if (checkSet != InWhichNodeSet.SELECTSTACK && !coalescedNodes.contains(m)) {
                    EnableMoves(n);
                }
            });
            EnableMoves(m);
            spillWorklist.remove(m);
            if (MoveRelated(m)) freezeWorklist.add(m);
            else simplifyWorklist.add(m);
        }
    }

    private void EnableMoves(int n) {
        moveList.get(n).forEach(m -> {
            if (activeMove.contains(m)) {
                activeMove.remove(m);
                worklistMoves.add(m);   // 真正可以做的 move
            }
        });
    }

    private void Coalesce() {   // 合并 moveRelated 的结点，用 conservative 作为判据
        Iterator<AsmInst> iter = worklistMoves.iterator();
        moveAsm m = (moveAsm) iter.next();
        worklistMoves.remove(m);
        int y = GetAlias(m.rd.getNumber()), x = GetAlias(m.rs1.getNumber()), u, v;
        if (preColored.contains(y)) {
            u = y;
            v = x;
        } else {
            u = x;
            v = y;
        }   // 把 v 合并到 u
        if (u == v) {   // 颜色相同
            coalescedMoves.add(m);
            AddWorkList(u);
        } else if (v < 32 || edgeSet.contains(u * finalRegCount + v)) {     // 不能同色
            constrainedMoves.add(m);    // moveRelated & 存在冲突
            AddWorkList(u);
            AddWorkList(v);
        } else if ((u < 32 && checkOK(u, v)) || (u >= 32 && Conservative(u, v))) {
            coalescedMoves.add(m);
            Combine(u, v);
            AddWorkList(u);
        } else activeMove.add(m);
    }

    private int GetAlias(int n) {
        if (coalescedNodes.contains(n)) return GetAlias(alias.get(n));
        return n;
    }

    private boolean checkOK(int u, int v) {
        for (Integer t : edgeList.get(v)) {
            InWhichNodeSet checkSet = inWhichNodeSets.get(t);
            if (checkSet != InWhichNodeSet.SELECTSTACK && !coalescedNodes.contains(t)) {
                if (!OK(t, u)) return false;
            }
        }
        return true;
    }

    private boolean OK(int t, int r) {
        return degree.get(t) < K || t < 32 || edgeSet.contains(t * finalRegCount + r);
    }

    private boolean Conservative(int u, int v) {    // 度数大于 K 的邻居数少于 K, 则可安全合并
        int k = 0;
        BitSet bitSet = new BitSet(finalRegCount);
        for (Integer n : edgeList.get(u)) {
            InWhichNodeSet checkSet = inWhichNodeSets.get(n);
            bitSet.set(n);
            if (checkSet != InWhichNodeSet.SELECTSTACK && !coalescedNodes.contains(n)) {
                if (degree.get(n) >= K) ++k;
            }
        }
        for (Integer n : edgeList.get(v))
            if (!bitSet.get(n)) {
                InWhichNodeSet checkSet = inWhichNodeSets.get(n);
                if (checkSet != InWhichNodeSet.SELECTSTACK && !coalescedNodes.contains(n)) {
                    if (degree.get(n) >= K) ++k;
                }
            }
        return k < K;
    }

    private void Combine(int u, int v) {    // 执行真正的 合并点 操作
        if (freezeWorklist.contains(v)) freezeWorklist.remove(v);
        else spillWorklist.remove(v);
        coalescedNodes.add(v);
        alias.set(v, u);    // 类似并查集的 并
        moveList.get(u).addAll(moveList.get(v));
        EnableMoves(v);
        for (Integer t : edgeList.get(v)) {
            InWhichNodeSet checkSet = inWhichNodeSets.get(t);
            if (checkSet != InWhichNodeSet.SELECTSTACK && !coalescedNodes.contains(t)) {
                AddEdge(t, u);
                DecrementDegree(t);
            }
        }
        if (degree.get(u) >= K && freezeWorklist.contains(u)) { // 发生冲突，无法分配
            freezeWorklist.remove(u);
            spillWorklist.add(u);
        }
    }

    private void AddWorkList(int u) {
        if (u >= 32 && !MoveRelated(u) && degree.get(u) < K) {
            freezeWorklist.remove(u);
            simplifyWorklist.add(u);
        }
    }

    private void Freeze() { // 条件 : 有 move-related 的邻居，但是不能进行合并，在进入之前先行判断
        Iterator<Integer> iter = freezeWorklist.iterator();
        int u = iter.next();
        freezeWorklist.remove(u);
        simplifyWorklist.add(u);
        FreezeMoves(u);
    }

    private void FreezeMoves(int u) {   // 删除相连结点 v 的 moveRelated 关系
        moveList.get(u).forEach(m -> {
            if (activeMove.contains(m) || worklistMoves.contains(m)) {
                moveAsm M = (moveAsm) m;
                int y = GetAlias(M.rd.getNumber()), x = GetAlias(M.rs1.getNumber()), v;
                if (GetAlias(y) == GetAlias(u)) v = GetAlias(x);
                else v = GetAlias(y);
                activeMove.remove(m);
                frozenMoves.add(m);
                boolean hasNodeMoves = false;
                for (AsmInst inst : moveList.get(v)) {
                    if (activeMove.contains(inst) || worklistMoves.contains(inst)) {
                        hasNodeMoves = true;
                        break;
                    }
                }
                if (!hasNodeMoves && degree.get(v) < K) {
                    freezeWorklist.remove(v);
                    simplifyWorklist.add(v);
                }
            }
        });
    }

    private void SelectSpill() { // 虚拟寄存器中的值保留在栈中而不是保留在物理寄存器中，只有在使用的时候才取出来
        int m = spillWorklist.iterator().next();
        double chosenThreshold = 1e50;
        for (Integer i : spillWorklist) {
            if (i < 32) continue;
            int dd = degree.get(i);
            double currentPriority = Priority.get(i) / (dd*dd);
            if (((virtualReg)IntToReg.get(i)).isAlloca) currentPriority = 0;
            if (i > curFunc.originRegCnt + 32) currentPriority += 1e50;
            if (currentPriority < chosenThreshold) {
                chosenThreshold = currentPriority;
                m = i;
            }
        }
        spillWorklist.remove(m);
        simplifyWorklist.add(m);
        FreezeMoves(m);
    }

    private void AssignColors() {  // 出栈进行颜色分配时，如果邻居用完了所有颜色，溢出才真正发生
        calleeSavedUsed.clear();
        while (!selectStack.isEmpty()) {
            int n = selectStack.pop();
            inWhichNodeSets.set(n, InWhichNodeSet.COLOREDNODES);
            BitSet forbidBits = (BitSet) colorSet.clone();
            for (Integer w : edgeList.get(n)) {
                int aliasW = GetAlias(w);
                if (coloredNodes.contains(aliasW) || aliasW < 32) forbidBits.set(color.get(aliasW));
            }
            int nextClearBit = forbidBits.nextClearBit(0);
            if (nextClearBit < 0 || nextClearBit >= K)
                spilledNodes.add(n);
            else {
                coloredNodes.add(n);
                int colorChosen = forbidBits.nextClearBit(0);
                if (!callerSavedSet.get(colorChosen)) calleeSavedUsed.set(colorChosen);
                color.set(n, colorChosen);
            }
        }
        for (Integer n : coalescedNodes) {
            color.set(n, color.get(GetAlias(n)));
        }
        int calleeSavedCount = 0;
        for (int d = calleeSavedUsed.nextSetBit(0); d >= 0; d = calleeSavedUsed.nextSetBit(d + 1)) {
            ++calleeSavedCount;
        }
        curFunc.calleeSavedCount = calleeSavedCount;
    }

    private void RewriteProgram() { // 真正的 spill 需要额外寄存器，因此要在 Asm 中加入 load 等指令
        LinkedList<Integer> newTemps = new LinkedList<>();
        HashMap<Integer, Integer> getStackPos = new HashMap<>();
        for (Integer spilledNode : spilledNodes) {
            getStackPos.put(spilledNode, curFunc.stackReserved++);
        }
        curFunc.blocks.forEach(asmBlock -> {
            int tmp;
            for (AsmInst i = asmBlock.headInst; i != null; i = i.next) {
                if (i instanceof branchAsm branch) {
                    tmp = branch.src1.getNumber();
                    if (spilledNodes.contains(tmp)) {
                        int stackPos = getStackPos.get(tmp);
                        branch.src1 = new virtualReg(curFunc.regCnt++);
                        newTemps.add(branch.src1.getNumber());
                        asmBlock.insert_before(i, new loadAsm(branch.src1, s0, new Imm(stackPos * -4), 4));
                    }
                    if (branch.src2 != null) {
                        tmp = branch.src2.getNumber();
                        if (spilledNodes.contains(tmp)) {
                            int stackPos = getStackPos.get(tmp);
                            branch.src2 = new virtualReg(curFunc.regCnt++);
                            newTemps.add(branch.src2.getNumber());
                            asmBlock.insert_before(i, new loadAsm(branch.src2, s0, new Imm(stackPos * -4), 4));
                        }
                    }
                } else if (i instanceof ITypeAsm it) {
                    tmp = it.rd.getNumber();
                    if (spilledNodes.contains(tmp)) {
                        int stackPos = getStackPos.get(tmp);
                        it.rd = new virtualReg(curFunc.regCnt++);
                        newTemps.add(it.rd.getNumber());
                        asmBlock.insert_after(i, new storeAsm(it.rd, s0, new Imm(stackPos * -4), 4));
                    }
                    tmp = it.rs1.getNumber();
                    if (spilledNodes.contains(tmp)) {
                        int stackPos = getStackPos.get(tmp);
                        it.rs1 = new virtualReg(curFunc.regCnt++);
                        newTemps.add(it.rs1.getNumber());
                        asmBlock.insert_before(i, new loadAsm(it.rs1, s0, new Imm(stackPos * -4), 4));
                    }
                } else if (i instanceof laAsm la) {
                    tmp = la.rd.getNumber();
                    if (spilledNodes.contains(tmp)) {
                        int stackPos = getStackPos.get(tmp);
                        la.rd = new virtualReg(curFunc.regCnt++);
                        newTemps.add(la.rd.getNumber());
                        asmBlock.insert_after(i, new storeAsm(la.rd, s0, new Imm(stackPos * -4), 4));
                    }
                } else if (i instanceof loadAsm ld) {
                    tmp = ld.rd.getNumber();
                    if (spilledNodes.contains(tmp)) {
                        int stackPos = getStackPos.get(tmp);
                        ld.rd = new virtualReg(curFunc.regCnt++);
                        newTemps.add(ld.rd.getNumber());
                        asmBlock.insert_after(i, new storeAsm(ld.rd, s0, new Imm(stackPos * -4), 4));
                    }
                    tmp = ld.addr.getNumber();
                    if (spilledNodes.contains(tmp)) {
                        int stackPos = getStackPos.get(tmp);
                        ld.addr = new virtualReg(curFunc.regCnt++);
                        newTemps.add(ld.addr.getNumber());
                        asmBlock.insert_before(i, new loadAsm(ld.addr, s0, new Imm(stackPos * -4), 4));
                    }
                } else if (i instanceof liAsm li) {
                    tmp = li.rd.getNumber();
                    if (spilledNodes.contains(tmp)) {
                        int stackPos = getStackPos.get(tmp);
                        li.rd = new virtualReg(curFunc.regCnt++);
                        newTemps.add(li.rd.getNumber());
                        asmBlock.insert_after(i, new storeAsm(li.rd, s0, new Imm(stackPos * -4), 4));
                    }
                } else if (i instanceof luiAsm lui) {
                    tmp = lui.rd.getNumber();
                    if (spilledNodes.contains(tmp)) {
                        int stackPos = getStackPos.get(tmp);
                        lui.rd = new virtualReg(curFunc.regCnt++);
                        newTemps.add(lui.rd.getNumber());
                        asmBlock.insert_after(i, new storeAsm(lui.rd, s0, new Imm(stackPos * -4), 4));
                    }
                } else if (i instanceof moveAsm mv) {
                    tmp = mv.rd.getNumber();
                    if (spilledNodes.contains(tmp)) {
                        int stackPos = getStackPos.get(tmp);
                        mv.rd = new virtualReg(curFunc.regCnt++);
                        newTemps.add(mv.rd.getNumber());
                        asmBlock.insert_after(i, new storeAsm(mv.rd, s0, new Imm(stackPos * -4), 4));
                    }
                    tmp = mv.rs1.getNumber();
                    if (spilledNodes.contains(tmp)) {
                        int stackPos = getStackPos.get(tmp);
                        mv.rs1 = new virtualReg(curFunc.regCnt++);
                        newTemps.add(mv.rs1.getNumber());
                        asmBlock.insert_before(i, new loadAsm(mv.rs1, s0, new Imm(stackPos * -4), 4));
                    }
                } else if (i instanceof RTypeAsm r) {
                    tmp = r.rd.getNumber();
                    if (spilledNodes.contains(tmp)) {
                        int stackPos = getStackPos.get(tmp);
                        r.rd = new virtualReg(curFunc.regCnt++);
                        newTemps.add(r.rd.getNumber());
                        asmBlock.insert_after(i, new storeAsm(r.rd, s0, new Imm(stackPos * -4), 4));
                    }
                    tmp = r.rs1.getNumber();
                    if (spilledNodes.contains(tmp)) {
                        int stackPos = getStackPos.get(tmp);
                        r.rs1 = new virtualReg(curFunc.regCnt++);
                        newTemps.add(r.rs1.getNumber());
                        asmBlock.insert_before(i, new loadAsm(r.rs1, s0, new Imm(stackPos * -4), 4));
                    }
                    tmp = r.rs2.getNumber();
                    if (spilledNodes.contains(tmp)) {
                        int stackPos = getStackPos.get(tmp);
                        r.rs2 = new virtualReg(curFunc.regCnt++);
                        newTemps.add(r.rs2.getNumber());
                        asmBlock.insert_before(i, new loadAsm(r.rs2, s0, new Imm(stackPos * -4), 4));
                    }
                } else if (i instanceof storeAsm st) {
                    tmp = st.rs.getNumber();
                    if (spilledNodes.contains(tmp)) {
                        int stackPos = getStackPos.get(tmp);
                        st.rs = new virtualReg(curFunc.regCnt++);
                        newTemps.add(st.rs.getNumber());
                        asmBlock.insert_before(i, new loadAsm(st.rs, s0, new Imm(stackPos * -4), 4));
                    }
                    tmp = st.addr.getNumber();
                    if (spilledNodes.contains(tmp)) {
                        int stackPos = getStackPos.get(tmp);
                        st.addr = new virtualReg(curFunc.regCnt++);
                        newTemps.add(st.addr.getNumber());
                        asmBlock.insert_before(i, new loadAsm(st.addr, s0, new Imm(stackPos * -4), 4));
                    }
                }
            }
        });
        spilledNodes = new HashSet<>();
        initial = coloredNodes;
        initial.addAll(coalescedNodes);
        initial.addAll(newTemps);
        coalescedNodes = new HashSet<>();
        coloredNodes = new HashSet<>();
    }

    private void Replace() {    // virtualReg -> physicalReg
        int stackChange = -4 * curFunc.calleeSavedCount;
        for (AsmBlock asmBlock : curFunc.blocks) {
            int tmp;
            for (AsmInst i = asmBlock.headInst; i != null; i = i.next) {
                if (i instanceof branchAsm branch) {
                    tmp = branch.src1.getNumber();
                    if (tmp >= 32) branch.src1 = phyRegs.get(color.get(tmp));
                    if (branch.src2 != null) {
                        tmp = branch.src2.getNumber();
                        if (tmp >= 32) branch.src2 = phyRegs.get(color.get(tmp));
                    }
                } else if (i instanceof ITypeAsm it) {
                    tmp = it.rd.getNumber();
                    if (tmp >= 32) it.rd = phyRegs.get(color.get(tmp));
                    tmp = it.rs1.getNumber();
                    if (tmp >= 32) it.rs1 = phyRegs.get(color.get(tmp));
                } else if (i instanceof laAsm la) {
                    tmp = la.rd.getNumber();
                    if (tmp >= 32) la.rd = phyRegs.get(color.get(tmp));
                } else if (i instanceof loadAsm ld) {
                    tmp = ld.rd.getNumber();
                    if (tmp >= 32) ld.rd = phyRegs.get(color.get(tmp));
                    tmp = ld.addr.getNumber();
                    if (tmp >= 32) ld.addr = phyRegs.get(color.get(tmp));
                    int value = ld.offset.value;
                    if (ld.addr.getNumber() == 8 && value < 0) ld.offset = new Imm(value + stackChange);
                } else if (i instanceof liAsm li) {
                    tmp = li.rd.getNumber();
                    if (tmp >= 32) li.rd = phyRegs.get(color.get(tmp));
                } else if (i instanceof luiAsm lui) {
                    tmp = lui.rd.getNumber();
                    if (tmp >= 32) lui.rd = phyRegs.get(color.get(tmp));
                } else if (i instanceof moveAsm mv) {
                    tmp = mv.rd.getNumber();
                    if (tmp >= 32) mv.rd = phyRegs.get(color.get(tmp));
                    tmp = mv.rs1.getNumber();
                    if (tmp >= 32) mv.rs1 = phyRegs.get(color.get(tmp));
                } else if (i instanceof RTypeAsm r) {
                    tmp = r.rd.getNumber();
                    if (tmp >= 32) r.rd = phyRegs.get(color.get(tmp));
                    tmp = r.rs1.getNumber();
                    if (tmp >= 32) r.rs1 = phyRegs.get(color.get(tmp));
                    tmp = r.rs2.getNumber();
                    if (tmp >= 32) r.rs2 = phyRegs.get(color.get(tmp));
                } else if (i instanceof storeAsm st) {
                    tmp = st.rs.getNumber();
                    if (tmp >= 32) st.rs = phyRegs.get(color.get(tmp));
                    tmp = st.addr.getNumber();
                    if (tmp >= 32) st.addr = phyRegs.get(color.get(tmp));
                    int value = st.offset.value;
                    if (st.addr.getNumber() == 8 && value < 0) st.offset = new Imm(value + stackChange);
                }
            }
        }
    }
}
