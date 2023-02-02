package Backend;

import Assembly.*;
import Assembly.Operand.*;
import Assembly.Instruction.*;

import java.util.ArrayList;

public class regAllocation {
    public AsmProgram program;
    public ArrayList<physicalReg> phyRegs;

    private physicalReg sp, t0, t1, t2, s0, ra, t3, t6;
    private AsmBlock tailBlock;
    private livenessAnalysis liveAnalysis;


    private int getLow12(int value) {
        return (0xfffff800) * ((value >> 11) & 1) + (value & 0x7ff);
    }

    public regAllocation(AsmProgram asmPg) {
        program = asmPg;
        ra = asmPg.physicalRegs.get(1);
        sp = asmPg.physicalRegs.get(2);
        t0 = asmPg.physicalRegs.get(5);   // t0-t6: temp register
        t1 = asmPg.physicalRegs.get(6);
        t2 = asmPg.physicalRegs.get(7);
        s0 = asmPg.physicalRegs.get(8);
        t3 = asmPg.physicalRegs.get(28);
        t6 = asmPg.physicalRegs.get(31);
        liveAnalysis = new livenessAnalysis(asmPg);
        phyRegs = asmPg.physicalRegs;
    }

    private int loadValue(AsmBlock curBlock, reg rd, int value, AsmInst mark) {
        // insert before mark
        if (value > 2047 || value < -2048) {
            if (((value >> 11) & 1) > 0) value += 1 << 12;
            curBlock.insert_before(new luiAsm(t6, new Imm(value >>> 12)), mark);   // >>> 为无符号右移
            curBlock.insert_before(new RTypeAsm(AsmInst.CalKind.add, t6, sp, t6), mark);
            curBlock.insert_before(new loadAsm(rd, t6, new Imm(getLow12(value)), 4), mark);
        } else {
            curBlock.insert_before(new loadAsm(rd, sp, new Imm(value), 4), mark);
        }
        return value;
    }

    private int storeValue(AsmBlock curBlock, reg rd, int value, AsmInst mark) {
        // insert before mark
        if (value > 2047 || value < -2048) {
            if (((value >> 11) & 1) > 0) value += 1 << 12;
            curBlock.insert_before(new luiAsm(t6, new Imm(value >>> 12)), mark);
            curBlock.insert_before(new RTypeAsm(AsmInst.CalKind.add, t6, sp, t6), mark);
            curBlock.insert_before(new storeAsm(rd, t6, new Imm(getLow12(value)), 4), mark);
        } else {
            curBlock.insert_before(new storeAsm(rd, sp, new Imm(value), 4), mark);
        }
        return value;   // 手动引用传参
    }

    private int addValue(AsmBlock curBlock, reg rd, reg rs1, int value, AsmInst mark) {
        // rd = rs1 + value
        if (value > 2047 || value < -2048) {
            if (((value >> 11) & 1) > 0) value += 1 << 12;
            curBlock.insert_before(new luiAsm(t6, new Imm(value >>> 12)), mark);
            curBlock.insert_before(new ITypeAsm(AsmInst.CalKind.add, t6, t6, new Imm(getLow12(value))), mark);
            curBlock.insert_before(new RTypeAsm(AsmInst.CalKind.add, rd, rs1, t6), mark);
        } else {
            curBlock.insert_before(new ITypeAsm(AsmInst.CalKind.add, rd, rs1, new Imm(value)), mark);
        }
        return value;
    }

    public void work() {
        program.functions.forEach(this::workInFunc);
    }

    public void workInFunc(AsmFunc func) {
        new graphColoring(program, func, liveAnalysis).work();
        func.stackLength = (func.stackReserved + func.calleeSavedCount + func.callSpilledCount) * 4 - 4;

        AsmBlock rootBlock = func.rootBlock;
        tailBlock = func.tailBlock;
        AsmInst headInst = rootBlock.headInst, tailInst = tailBlock.tailInst;

        addValue(rootBlock, sp, sp, -func.stackLength, headInst);

        int value = storeValue(rootBlock, ra, func.stackLength - 4, headInst);

        func.calleeSavedUsed.set(8);
        for (int d = func.calleeSavedUsed.nextSetBit(0); d >= 0; d = func.calleeSavedUsed.nextSetBit(d + 1)) {
            value -= 4;
            value = storeValue(rootBlock, program.physicalRegs.get(d), value, headInst);
        }

        addValue(rootBlock, s0, sp, func.stackLength, headInst);

        value = func.stackLength - 4;
        for (int d = func.calleeSavedUsed.nextSetBit(0); d >= 0; d = func.calleeSavedUsed.nextSetBit(d + 1)) {
            value -= 4;
            value = loadValue(tailBlock, program.physicalRegs.get(d), value, tailInst);
        }

        loadValue(tailBlock, ra, func.stackLength - 4, tailInst);

        addValue(tailBlock, sp, sp, func.stackLength, tailInst);

    }
}