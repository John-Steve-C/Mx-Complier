package Backend;

import Assembly.*;
import Assembly.Instruction.*;

import java.util.BitSet;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Queue;

public class livenessAnalysis {
    public AsmProgram program;
    public livenessAnalysis(AsmProgram prog) {
        program = prog;
    }

    private void addBitset(AsmInst inst, int bitSize) {
        inst.bitSize = bitSize;
        inst.def = new BitSet(bitSize);
        inst.use = new BitSet(bitSize);
        inst.liveIn = new BitSet(bitSize);
        inst.liveOut = new BitSet(bitSize);
        inst.fillSet();
    }

    private boolean calInst(AsmInst inst) {
        BitSet preIn = inst.liveIn;
        BitSet preOut = inst.liveOut;
        inst.calInst();
        return !preIn.equals(inst.liveIn) || !preOut.equals(inst.liveOut);
    }

    public void work() {
        program.functions.forEach(this::workFunc);
    }

    public void workFunc(AsmFunc func) {
        int bitSize = func.regCnt + 32, blockListSize = func.blocks.size();
        // liveness analysis 需要倒序执行
        ListIterator<AsmBlock> it = func.blocks.listIterator(blockListSize);
        while (it.hasPrevious()) {
            AsmBlock curBlock = it.previous();  // it = it.pre
            AsmInst curInst = curBlock.tailInst;
            while (curInst.pre != null) {
                addBitset(curInst, bitSize);
                curInst = curInst.pre;
            }
            addBitset(curInst, bitSize);
        }

        boolean quit = false;
        while (!quit) {
            quit = true;
            it = func.blocks.listIterator(blockListSize);
            while (it.hasPrevious()) {
                AsmBlock curBlock = it.previous();
                AsmInst curInst = curBlock.tailInst;
                while (curInst.pre != null) {
                    if (calInst(curInst)) quit = false;
                    curInst = curInst.pre;
                }
                if (calInst(curInst)) quit = false;
            }
        }
        // dead code eliminate
        it = func.blocks.listIterator(blockListSize);
        while (it.hasPrevious()) {
            AsmBlock curBlock = it.previous();
            AsmInst curInst = curBlock.headInst;
            while (curInst != null) {
                AsmInst next = curInst.next;
                if (curInst.check()) curBlock.delete_inst(curInst);
                curInst = next;
            }
        }
    }
}
