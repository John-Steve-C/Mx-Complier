package Assembly;

import Assembly.Instruction.AsmInst;

import java.util.*;

public class AsmBlock {
    public AsmInst headInst, tailInst;  // 链表
//    public HashSet<AsmBlock> successors = new HashSet<>();  // 后继
    public LinkedHashSet<AsmBlock> successors = new LinkedHashSet<>();

    public int index = -1, funcIndex = -1;
    public boolean isRoot = false;
    public String blockName, comment = null;

    public int loopDepth, id = 0;
    public HashMap<AsmBlock, AsmInst> jumpFrom = new HashMap<>();

    public AsmBlock(int depth) {
        loopDepth = depth;
    }

    public AsmBlock(int depth, String comment) {
        loopDepth = depth;
        this.comment = comment;
    }

    public void push_back(AsmInst inst) {
        if (headInst == null) headInst = tailInst = inst;
        else {
            tailInst.next = inst;
            inst.pre = tailInst;
            tailInst = inst;
        }
    }

    public void push_front(AsmInst inst) {
        if (headInst == null) headInst = tailInst = inst;
        else {
            headInst.pre = inst;
            inst.next = headInst;
            headInst = inst;
        }
    }

    public void insert_before(AsmInst src, AsmInst mark) {
        // insert src before mark
        if (mark.pre == null) headInst = src;
        else mark.pre.next = src;

        src.next = mark;
        src.pre = mark.pre;
        mark.pre = src;
    }

    public void insert_after(AsmInst src, AsmInst mark) {
        // insert src after mark
        if (mark.next == null) tailInst = src;
        else mark.next.pre = src;

        src.pre = mark;
        src.next = mark.next;
        mark.next = src;
    }

    public void delete_inst(AsmInst i) {
        if (i.pre == null) headInst = i.next;
        else i.pre.next = i.next;

        if (i.next == null) tailInst = i.pre;
        else i.next.pre = i.pre;
    }

    @Override
    public String toString() {
        if (isRoot) return blockName;
        else return ".block" + funcIndex + "_" + index;
    }
}
