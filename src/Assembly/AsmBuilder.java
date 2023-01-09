package Assembly;

import java.util.*;

import Assembly.Instruction.*;
import IR.Node.Instruction.call;
import IR.TypeSystem.*;
import IR.Node.*;
import IR.Node.GlobalUnit.*;
import IR.Node.Instruction.*;

import Assembly.Operand.*;

public class AsmBuilder {
    public AsmProgram asmProg;
    public HashMap<register, reg> regMap = new HashMap<>();   // IR -> Asm
    public HashMap<block, AsmBlock> blockMap = new HashMap<>();
//    public HashMap<String, reg> globalVarCache = new HashMap<>();

    private final physicalReg zero, sp, s0, a0;
    private int regCnt = 0, blockCnt = 0, funcCnt = 0;
    private HashSet<block> blockVisited;
    private AsmFunc curFunc = null;
    private AsmBlock tailBlock = null;
    private LinkedList<phi> Phis = new LinkedList<>();


    public AsmBuilder(AsmProgram program) {
        this.asmProg = program;
        zero = asmProg.physicalRegs.get(0);
        sp = asmProg.physicalRegs.get(2);
        s0 = asmProg.physicalRegs.get(8);
        a0 = asmProg.physicalRegs.get(10);
    }

    public AsmBlock getBlock(block b) {
        if (blockMap.containsKey(b)) return blockMap.get(b);
        else {
            AsmBlock newBlock = new AsmBlock(b.loopDepth, b.comment);
            blockMap.put(b, newBlock);
            return newBlock;
        }
    }

    public reg getReg(register r) {
        if (regMap.containsKey(r)) return regMap.get(r);
        else {
            virtualReg newReg = new virtualReg(regCnt++);
            regMap.put(r, newReg);
            return newReg;
        }
    }

    public Imm getImm(constant c) {
        return new Imm(c.getValue());
    }

    private boolean powerOf2(int x) {
        // 判断 x 是否为 2 的幂
        int lowbit = x & (-x);// 二进制最低位的 1. 1001 0110 -> 0000 0010
        return x > 0 && ((x ^ lowbit) == 0);
    }

    private int getLow12(int constValue) {
        // 取出 constValue 的低 12 位
        // (constValue >> 11) & 1 取出第 12 位。高位补1
        // 2^11 - 1 = 0x7ff (后 11 位)
        return (0xfffff800) * ((constValue >> 11) & 1) + (constValue & 0x7ff);
    }

    private void loadValue(AsmBlock curBlock, reg rd, int value) {
        if (value > 2047 || value < -2048) {
            if (((value >> 11) & 1) > 0) value += 1 << 12;
            curBlock.push_back(new luiAsm(rd, new Imm(value >>> 12)));   // >>> 为无符号右移
            curBlock.push_back(new ITypeAsm(AsmInst.CalKind.add, rd, rd, new Imm(getLow12(value))));
        } else {
            curBlock.push_back(new ITypeAsm(AsmInst.CalKind.add, rd, zero, new Imm(value)));
        }
    }

    //-----------------------------------------------------------------------
    public void visitProgram(Program prog) {
        prog.func.forEach(this::visitFuncDef);
        prog.varDecl.forEach(this::visitGlobalVarDecl);
        prog.strConst.forEach(this::visitGlobalString);

        // collect function
        asmProg.functions.forEach(this::collectFunc);
    }

    public void visitFuncDef(funcDef f) {
        blockVisited = new HashSet<>();
//        globalVarCache = new HashMap<>();
        regCnt = 0;

        curFunc = new AsmFunc(f.funcName);
        curFunc.rootBlock = getBlock(f.rootBlock);
        asmProg.functions.add(curFunc);

        // add parameters
        int parameterCnt = 0;
        for (register parameterReg : f.parameterRegs) {
            if (parameterCnt < 8) {
                // x10-x17 保存函数参数
                curFunc.rootBlock.push_back(new moveAsm(getReg(parameterReg), asmProg.physicalRegs.get(10 + parameterCnt)));
            } else {
                // 多余的参数保存在虚拟寄存器中
                virtualReg vr = (virtualReg) getReg(parameterReg);
                vr.overflow = (parameterCnt - 8) * 4;   // calculate the location
            }
            parameterCnt++;
        }
        // cope with alloca inst
        for (alloca alloc : f.allocas) {
            virtualReg vr = new virtualReg(regCnt++);
            vr.isAlloca = true;
            regMap.put(alloc.rd, vr);
        }

        // Phi's var
        Phis.clear();
        tailBlock = null;
        visitBlock(f.rootBlock);
        for (phi p : Phis) {
            reg rd = new virtualReg(regCnt++);
            for (entityBlockPair pair : p.entityBlockPairs) {
                entity en = pair.en;
                AsmBlock fromBlk = getBlock(pair.blk);
                AsmInst fromInst = p.asmParentBlock.jumpFrom.get(fromBlk);
                p.asmParentBlock.push_front(new moveAsm(getReg(p.rd), rd));
                if (fromInst != null) {
                    if (en instanceof constant c) {
                        int constValue = c.getValue();
                        if (constValue > 2047 || constValue < -2048) {
                            if (((constValue >> 11) & 1) > 0) constValue += 1 << 12;
                            fromBlk.insert_before(new luiAsm(rd, new Imm(constValue >>> 12)), fromInst);   // >>> 为无符号右移
                            fromBlk.insert_before(new ITypeAsm(AsmInst.CalKind.add, rd, rd, new Imm(getLow12(constValue))), fromInst);
                        } else {
                            fromBlk.insert_before(new ITypeAsm(AsmInst.CalKind.add, rd, zero, new Imm(constValue)), fromInst);
                        }
                    } else {
                        // register
                        fromBlk.insert_before(new RTypeAsm(AsmInst.CalKind.add, rd, zero, getReg((register) en)), fromInst);
                    }
                }
            }
        }
        if (tailBlock == null) {
            visitBlock(f.returnBlock);
            tailBlock = getBlock(f.returnBlock);
        }
        curFunc.tailBlock = tailBlock;
        curFunc.stackLength = 4 * (regCnt + 2);     // +2 to prevent exception
        curFunc.regCnt = regCnt;
    }

    public void visitBlock(block b) {
        blockVisited.add(b);
        AsmBlock curBlock = getBlock(b);

        for (instruction i = b.headStmt; i != null; i = i.next) {
            if (i.removed) continue;
            if (i instanceof binary binaryInst) {
                AsmInst.CalKind op = null;
                switch (binaryInst.op) {
                    case ADD -> op = AsmInst.CalKind.add;
                    case SUB -> op = AsmInst.CalKind.sub;
                    case MUL -> op = AsmInst.CalKind.mul;
                    case SDIV -> op = AsmInst.CalKind.div;
                    case MOD -> op = AsmInst.CalKind.rem;
                    case AND -> op = AsmInst.CalKind.and;
                    case OR -> op = AsmInst.CalKind.or;
                    case XOR -> op = AsmInst.CalKind.xor;
                    case SHL -> op = AsmInst.CalKind.sll;
                    case ASHR -> op = AsmInst.CalKind.sra;
                    case LSHR -> op = AsmInst.CalKind.srl;
                }
                reg rd = getReg(binaryInst.rd);
                if (binaryInst.rs1 instanceof constant const1) {
                    int value1 = const1.getValue();
                    loadValue(curBlock, rd, value1);    // load rs1 to rd directly
                    reg rs2;
                    if (binaryInst.rs2 instanceof constant const2) {
                        // const expression
                        rs2 = new virtualReg(regCnt++);
                        int value2 = const2.getValue();
                        loadValue(curBlock, rs2, value2);
                    } else {
                        rs2 = getReg((register) binaryInst.rs2);
                    }
                    curBlock.push_back(new RTypeAsm(op, rd, rd, rs2));
                } else {
                    reg rs1 = getReg((register) binaryInst.rs1);
                    if (binaryInst.rs2 instanceof constant const2) {
                        int value2 = const2.getValue();
                        if (op == AsmInst.CalKind.mul || op == AsmInst.CalKind.div || op == AsmInst.CalKind.rem) {
                            if (powerOf2(value2)) {     // 判断 value2 = 2^k，可以优化乘除计算
                                // replace mul/div/rem with sll/sra
                                int shiftCnt = -1;
                                while (value2 > 0) {
                                    ++shiftCnt;
                                    value2 >>= 1;
                                }
                                if (op == AsmInst.CalKind.mul)
                                    curBlock.push_back(new ITypeAsm(AsmInst.CalKind.sll, rd, rs1, new Imm(shiftCnt)));
                                else if (op == AsmInst.CalKind.div)
                                    curBlock.push_back(new ITypeAsm(AsmInst.CalKind.sra, rd, rs1, new Imm(shiftCnt)));
                                else
                                    curBlock.push_back(new ITypeAsm(AsmInst.CalKind.and, rd, rs1, new Imm((1 << shiftCnt) - 1)));
                            } else {
                                loadValue(curBlock, rd, value2);
                                curBlock.push_back(new RTypeAsm(op, rd, rs1, rd));
                            }
                        } else {
                            if (op == AsmInst.CalKind.sub) {
                                op = AsmInst.CalKind.add;
                                value2 = -value2;
                            }
                            loadValue(curBlock, rd, value2);
                            curBlock.push_back(new RTypeAsm(op, rd, rs1, rd));
                        }
                    }
                }

            } else if (i instanceof br branchInst) {
                if (branchInst.val == null) {
                    AsmBlock target = getBlock(branchInst.trueBranch);
                    jumpAsm j = new jumpAsm(target);
                    target.jumpFrom.put(curBlock, j);
                    curBlock.push_back(j);
                } else {
                    AsmBlock trueBlock = getBlock(branchInst.trueBranch), falseBlock = getBlock(branchInst.falseBranch);
                    jumpAsm jumpTrue = new jumpAsm(trueBlock);    // no condition jump
                    trueBlock.jumpFrom.put(curBlock, jumpTrue);

                    AsmInst tailInst = curBlock.tailInst;
                    branchAsm falseInst;
                    // check whether tailInst is special cmpInst in I/RType
                    // if so, then change it into real branch Inst
                    if (tailInst instanceof RTypeAsm in && in.op.ordinal() > 10) {
                        // cmp in RType
                        AsmInst.CmpKind op = null;
                        // CalKind -> 相反的 CmpKind
                        switch (in.op) {
                            case slt -> op = AsmInst.CmpKind.ge;    // < --- >=
                            case sgt -> op = AsmInst.CmpKind.le;
                            case seq -> op = AsmInst.CmpKind.ne;
                            case sne -> op = AsmInst.CmpKind.eq;
                        }
                        falseInst = new branchAsm(op, in.rs1, in.rs2, falseBlock);
                        falseBlock.jumpFrom.put(curBlock, falseInst);
                        curBlock.delete_inst(tailInst);
                        curBlock.push_back(falseInst);
                    } else if (tailInst instanceof ITypeAsm in && (in.op == AsmInst.CalKind.sne || in.op == AsmInst.CalKind.seq)) {
                        // cmp in IType
                        AsmInst.CmpKind op = null;
                        if (in.op == AsmInst.CalKind.sne) op = AsmInst.CmpKind.eq;
                        else op = AsmInst.CmpKind.ne;
                        falseInst = new branchAsm(op, in.rs1, null, falseBlock);
                        falseBlock.jumpFrom.put(curBlock, falseInst);
                        curBlock.delete_inst(tailInst);
                        curBlock.push_back(falseInst);
                    } else {
                        // normal situation
                        falseInst = new branchAsm(AsmInst.CmpKind.eq, getReg(branchInst.val), null, falseBlock);
                        falseBlock.jumpFrom.put(curBlock, falseInst);
                        curBlock.push_back(falseInst);
                    }
                    curBlock.push_back(jumpTrue);
                }

            } else if (i instanceof call callInst) {
                int parameterCnt = 0;
                callAsm funcCall = new callAsm(callInst.funcName);
                for (entityTypePair parameter : callInst.parameters) {
                    reg rs;
                    entity en = parameter.en;
                    if (en instanceof constant c) {
                        rs = new virtualReg(regCnt++);
                        int constValue = c.getValue();
                        loadValue(curBlock, rs, constValue);
                    } else {
                        rs = getReg((register) en);
                    }
                    if (parameterCnt < 8)
                        curBlock.push_back(new moveAsm(asmProg.physicalRegs.get(parameterCnt + 10), rs));
                    else curBlock.push_back(new storeAsm(rs, sp, new Imm((parameterCnt - 8) * 4), 4));
                    funcCall.parameters.add(rs);
                    parameterCnt++;
                }
                curBlock.push_back(funcCall);
                // store return value
                if (callInst.rd != null) curBlock.push_back(new moveAsm(getReg(callInst.rd), a0));

            } else if (i instanceof convertOp convertInst) {
                if (convertInst.rs instanceof constant) {
                    reg rs = new virtualReg(regCnt++);
                    int constValue = ((constant) convertInst.rs).getValue();
                    loadValue(curBlock, rs, constValue);
                    virtualReg vr = (virtualReg) getReg(convertInst.rd);
                    curBlock.push_back(new moveAsm(vr, rs));
                } else {
                    regMap.put(convertInst.rd, getReg((register) convertInst.rs));
                }

            } else if (i instanceof getelementptr eleInst) {
                IRType irType = eleInst.rsType;
                reg rd = getReg(eleInst.rd), rs = getReg(eleInst.rs);
                if (irType.cDef == null) {
                    // array
                    entity en = eleInst.locator1;
                    int atomSize = irType.reducePtr().getSize();
                    if (en instanceof constant c) {
                        int constValue = c.getValue() * atomSize;
                        loadValue(curBlock, rd, constValue);
                        curBlock.push_back(new RTypeAsm(AsmInst.CalKind.add, rd, rs, rd));
                    } else {
                        reg tmp = getReg((register) en);
                        if (atomSize > 1) {
                            int constValue = atomSize;
                            // 优化乘法
                            if (powerOf2(constValue)) {
                                int shiftCnt = -1;
                                while (constValue > 0) {
                                    shiftCnt++;
                                    constValue >>= 1;
                                }
                                curBlock.push_back(new ITypeAsm(AsmInst.CalKind.sll, tmp, tmp, new Imm(shiftCnt)));
                            } else {
                                virtualReg container = new virtualReg(regCnt++);
                                loadValue(curBlock, container, constValue);
                                curBlock.push_back(new RTypeAsm(AsmInst.CalKind.mul, tmp, tmp, container));
                            }
                        }
                        curBlock.push_back(new RTypeAsm(AsmInst.CalKind.add, rd, rs, tmp));
                    }
                } else {
                    // class
                    classDef cDef = irType.cDef;
                    int index = ((constant) eleInst.locator2).getValue(), offset;
                    IRTypeWithCounter memberType = cDef.memberForAsm.get(index);
                    if (cDef.align == 1) offset = memberType.offset1;
                    else offset = memberType.offset4;
                    curBlock.push_back(new ITypeAsm(AsmInst.CalKind.add, rd, rs, new Imm(offset)));
                }

            } else if (i instanceof icmp icmpInst) {
                reg rd = getReg(icmpInst.rd), rs1, rs2;
                if (icmpInst.rs1 instanceof constant c1) {
                    rs1 = new virtualReg(regCnt++);
                    loadValue(curBlock, rs1, c1.getValue());
                } else {
                    rs1 = getReg((register) icmpInst.rs1);
                }
                if (icmpInst.rs2 instanceof constant c2) {
                    rs2 = new virtualReg(regCnt++);
                    loadValue(curBlock, rs2, c2.getValue());
                } else {
                    rs2 = getReg((register) icmpInst.rs2);
                }
                reg tmpReg = new virtualReg(regCnt++);
                switch (icmpInst.cmpOp) {
                    // we only have slt in RV32I
                    case SGT -> curBlock.push_back(new RTypeAsm(AsmInst.CalKind.slt, rd, rs2, rs1));
                    case SLT -> curBlock.push_back(new RTypeAsm(AsmInst.CalKind.slt, rd, rs1, rs2));
                    case SGE -> {
                        // implement SGE by 2 combined insts
                        curBlock.push_back(new RTypeAsm(AsmInst.CalKind.slt, tmpReg, rs1, rs2));
                        curBlock.push_back(new ITypeAsm(AsmInst.CalKind.seq, rd, tmpReg, new Imm(0)));
                    }
                    case SLE -> {
                        curBlock.push_back(new RTypeAsm(AsmInst.CalKind.slt, tmpReg, rs2, rs1));
                        curBlock.push_back(new ITypeAsm(AsmInst.CalKind.seq, rd, tmpReg, new Imm(0)));
                    }
                    case EQ -> {
                        curBlock.push_back(new RTypeAsm(AsmInst.CalKind.xor, tmpReg, rs1, rs2));
                        curBlock.push_back(new ITypeAsm(AsmInst.CalKind.seq, rd, tmpReg, new Imm(0)));
                    }
                    case NEQ -> {
                        curBlock.push_back(new RTypeAsm(AsmInst.CalKind.xor, tmpReg, rs1, rs2));
                        curBlock.push_back(new ITypeAsm(AsmInst.CalKind.sne, rd, tmpReg, new Imm(0)));
                    }
                }

            } else if (i instanceof load loadInst) {
                String label = loadInst.ptr.label;
                reg rd = getReg(loadInst.rd);
                int align = loadInst.align;
                if (label != null) {
                    reg tmpReg = new virtualReg(regCnt++);
                    curBlock.push_back(new laAsm(tmpReg, label));
                    curBlock.push_back(new loadAsm(rd, tmpReg, new Imm(0), align));
                } else {
                    virtualReg vr = (virtualReg) getReg(loadInst.ptr);
                    if (vr.overflow >= 0) curBlock.push_back(new loadAsm(rd, s0, new Imm(vr.overflow), align));
                    else if (vr.isAlloca) curBlock.push_back(new moveAsm(rd, vr));
                    else if (vr.index < 0) curBlock.push_back(new loadAsm(rd, s0, new Imm(vr.index * 4), align));
                    else curBlock.push_back(new loadAsm(rd, vr, new Imm(0), align));
                }

            } else if (i instanceof store storeInst) {
                reg rs;
                if (storeInst.rs instanceof constant c) {
                    rs = new virtualReg(regCnt++);
                    int constValue = c.getValue();
                    loadValue(curBlock, rs, constValue);
                } else {
                    rs = getReg((register) storeInst.rs);
                }
                String label = storeInst.target.label;
                if (label != null) {
                    reg tmpReg = new virtualReg(regCnt++);
                    curBlock.push_back(new laAsm(tmpReg, label));
                    curBlock.push_back(new storeAsm(rs, tmpReg, new Imm(0), storeInst.align));
                } else {
                    virtualReg vr = (virtualReg) getReg(storeInst.target);
                    if (rs instanceof virtualReg && ((virtualReg) rs).overflow >= 0) {
                        if (!vr.isAlloca) curBlock.push_back(new storeAsm(vr, s0, new Imm(((virtualReg) rs).overflow), 4));
                    } else if (vr.isAlloca) {
                        curBlock.push_back(new moveAsm(vr, rs));
                    } else if (vr.index < 0) {
                        curBlock.push_back(new storeAsm(rs, s0, new Imm(vr.index * 4), 4));
                    } else {
                        // todo: modify?
                        curBlock.push_back(new storeAsm(rs, vr, new Imm(0), storeInst.align));
                    }
                }

            } else if (i instanceof phi phiInst) {
                phiInst.asmParentBlock = curBlock;
                Phis.add(phiInst);

            } else if (i instanceof ret retInst) {
                tailBlock = curBlock;
                if (retInst.value != null) {
                    if (retInst.value instanceof register) curBlock.push_back(new moveAsm(asmProg.physicalRegs.get(10), getReg((register) retInst.value)));
                    else curBlock.push_back(new liAsm(asmProg.physicalRegs.get(10), getImm((constant) retInst.value)));
                }

            } else if (i instanceof bitcast bitInst) {
                if (bitInst.rs.label != null) curBlock.push_back(new laAsm(getReg(bitInst.rd), bitInst.rs.label));
                else regMap.put(bitInst.rd, getReg(bitInst.rs));
            }
        }

        for (block successor : b.successors) {
            if (!blockVisited.contains(successor)) {
                curBlock.successors.add(getBlock(successor));
                visitBlock(successor);
            }
        }
    }

    public void visitGlobalVarDecl(globalVarDeclaration v) {
        asmProg.globals.add(new AsmGlobal(v.name, v.align, getImm((constant) v.rs).toString(), false));
    }

    public void visitGlobalString(globalStringConst str) {
        asmProg.globals.add(new AsmGlobal(".str." + str.content, str.type.arrayLen, str.rawStr, true));
    }

    public void collectFunc(AsmFunc func) {
        // add blocks to functions
        // offer = push; poll = get head & pop
        blockCnt = 0;
        Queue<AsmBlock> queue = new LinkedList<>();
        queue.offer(func.rootBlock);
        func.rootBlock.index = blockCnt++;
        func.rootBlock.isRoot = true;
        func.rootBlock.blockName = func.name;
        while (!queue.isEmpty()) {
            AsmBlock b = queue.poll();
            b.funcIndex = funcCnt;
            b.successors.forEach(s -> {
                if (s.index == -1) {    // has not been added to queue
                    s.index = blockCnt++;
                    queue.offer(s);
                }
            });
            func.blocks.add(b);
        }
        funcCnt++;
    }
}
