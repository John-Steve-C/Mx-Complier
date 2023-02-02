package Backend;

import Assembly.*;
import Assembly.Operand.*;
import Assembly.Instruction.*;

public class regAllocationBasic {
    public AsmProgram program;
    private physicalReg sp, t0, t1, t2, s0, ra, t3, t6;
    private AsmBlock tailBlock;

    private int getLow12(int constValue) {
        return (0xfffff800) * ((constValue >> 11) & 1) + (constValue & 0x7ff);
    }

    public regAllocationBasic(AsmProgram asmPg) {
        program = asmPg;
        ra = asmPg.physicalRegs.get(1);
        sp = asmPg.physicalRegs.get(2);
        t0 = asmPg.physicalRegs.get(5);   // t0-t6: temp register
        t1 = asmPg.physicalRegs.get(6);
        t2 = asmPg.physicalRegs.get(7);
        s0 = asmPg.physicalRegs.get(8);
        t3 = asmPg.physicalRegs.get(28);
        t6 = asmPg.physicalRegs.get(31);
    }

    private void loadVirtualReg(AsmBlock block, AsmInst inst, virtualReg vReg, physicalReg rd) {
        int value = vReg.index * 4;
        if (value > 2047 || value < -2048) {
            block.insert_before(new liAsm(t6, new Imm(value)), inst);
            block.insert_before(new RTypeAsm(AsmInst.CalKind.add, t6, t6, sp), inst);
            block.insert_before(new loadAsm(rd, t6, new Imm(0), 4), inst);
        } else {
            block.insert_before(new loadAsm(rd, sp, new Imm(value), 4), inst);
        }
    }

    private void storeVirtualReg(AsmBlock block, AsmInst inst, virtualReg vReg) {
        int value = vReg.index * 4;
        if (value > 2047 || value < -2048) {
            // 注意，此处指令插入的顺序和实际顺序相反
            // 即 li -> add -> store
            block.insert_after(new storeAsm(t2, t6, new Imm(0), 4), inst);
            block.insert_after(new RTypeAsm(AsmInst.CalKind.add, t6, t6, sp), inst);
            block.insert_after(new liAsm(t6, new Imm(value)), inst);
        } else {
            block.insert_after(new storeAsm(t2, sp, new Imm(value), 4), inst);
        }
    }

    private void loadValue(AsmBlock curBlock, reg rd, int value, AsmInst mark) {
        // insert before mark
        if (value > 2047 || value < -2048) {
            if (((value >> 11) & 1) > 0) value += 1 << 12;
            curBlock.insert_before(new luiAsm(t6, new Imm(value >>> 12)), mark);   // >>> 为无符号右移
            curBlock.insert_before(new RTypeAsm(AsmInst.CalKind.add, t6, sp, t6), mark);
            curBlock.insert_before(new loadAsm(rd, t6, new Imm(getLow12(value)), 4), mark);
        } else {
            curBlock.insert_before(new loadAsm(rd, sp, new Imm(value), 4), mark);
        }
    }

    private void storeValue(AsmBlock curBlock, reg rd, int value, AsmInst mark) {
        // insert before mark
        if (value > 2047 || value < -2048) {
            if (((value >> 11) & 1) > 0) value += 1 << 12;
            curBlock.insert_before(new luiAsm(t6, new Imm(value >>> 12)), mark);
            curBlock.insert_before(new RTypeAsm(AsmInst.CalKind.add, t6, sp, t6), mark);
            curBlock.insert_before(new storeAsm(rd, t6, new Imm(getLow12(value)), 4), mark);
        } else {
            curBlock.insert_before(new storeAsm(rd, sp, new Imm(value), 4), mark);
        }
    }

    private void addValue(AsmBlock curBlock, reg rd, reg rs1, int value, AsmInst mark) {
        // rd = rs1 + value
        if (value > 2047 || value < -2048) {
            if (((value >> 11) & 1) > 0) value += 1 << 12;
            curBlock.insert_before(new luiAsm(t6, new Imm(value >>> 12)), mark);
            curBlock.insert_before(new ITypeAsm(AsmInst.CalKind.add, t6, t6, new Imm(getLow12(value))), mark);
            curBlock.insert_before(new RTypeAsm(AsmInst.CalKind.add, rd, rs1, t6), mark);
        } else {
            curBlock.insert_before(new ITypeAsm(AsmInst.CalKind.add, rd, rs1, new Imm(value)), mark);
        }
    }

    public void work() {
        program.functions.forEach(this::workInFunc);
    }

    public void workInFunc(AsmFunc func) {
        tailBlock = null;
        AsmBlock rootBlock = func.rootBlock;
        workInBlock(rootBlock);
        AsmInst headInst = rootBlock.headInst, tailInst = tailBlock.tailInst;
        // add value to sp
        addValue(rootBlock, sp, sp, -func.stackLength, headInst);

        storeValue(rootBlock, s0, func.stackLength - 8, headInst);
        storeValue(rootBlock, ra, func.stackLength - 4, headInst);
        addValue(rootBlock, s0, sp, func.stackLength, headInst);

        loadValue(tailBlock, s0, func.stackLength - 8, tailInst);
        loadValue(tailBlock, ra, func.stackLength - 4, tailInst);
        addValue(tailBlock, sp, sp, func.stackLength, tailInst);
    }

    public void workInBlock(AsmBlock block) {
        // t0,t1 --- load
        // t2 --- store
        for (AsmInst i = block.headInst; i != null; i = i.next) {
            if (i instanceof branchAsm br) {
                if (br.src1 instanceof virtualReg) {
                    loadVirtualReg(block, i, (virtualReg) br.src1, t0);
                    br.src1 = t0;
                }
                if (br.src2 != null) {
                    loadVirtualReg(block, i, (virtualReg) br.src2, t1);
                    br.src2 = t1;
                }

            } else if (i instanceof ITypeAsm iType) {
                if (iType.rs1 instanceof virtualReg) {
                    loadVirtualReg(block, i, (virtualReg) iType.rs1, t0);
                    iType.rs1 = t0;
                }
                if (iType.rd instanceof virtualReg) {
                    storeVirtualReg(block, i, (virtualReg) iType.rd);
                    iType.rd = t2;
                }

            } else if (i instanceof liAsm li) {
                if (li.rd instanceof virtualReg) {
                    storeVirtualReg(block, i, (virtualReg) li.rd);
                    li.rd = t2;
                }

            } else if (i instanceof moveAsm mv) {
                if (mv.rs1 instanceof virtualReg) {
                    loadVirtualReg(block, i, (virtualReg) mv.rs1, t0);
                    mv.rs1 = t0;
                }
                if (mv.rd instanceof virtualReg) {
                    storeVirtualReg(block, i, (virtualReg) mv.rd);
                    mv.rd = t2;
                }

            } else if (i instanceof RTypeAsm rType) {
                if (rType.rs1 instanceof virtualReg) {
                    loadVirtualReg(block, i, (virtualReg) rType.rs1, t0);
                    rType.rs1 = t0;
                }
                if (rType.rs2 instanceof virtualReg) {
                    loadVirtualReg(block, i, (virtualReg) rType.rs2, t1);
                    rType.rs2 = t1;
                }
                if (rType.rd instanceof virtualReg) {
                    storeVirtualReg(block, i, (virtualReg) rType.rd);
                    rType.rd = t2;
                }

            } else if (i instanceof luiAsm lui) {
                if (lui.rd instanceof virtualReg) {
                    storeVirtualReg(block, i, (virtualReg) lui.rd);
                    lui.rd = t2;
                }

            } else if (i instanceof retAsm) {
                tailBlock = block;

            } else if (i instanceof loadAsm ld) {
                if (ld.addr instanceof virtualReg) {
                    loadVirtualReg(block, i, (virtualReg) ld.addr, t0);
                    ld.addr = t0;
                }
                if (ld.rd instanceof virtualReg) {
                    storeVirtualReg(block, i, (virtualReg) ld.rd);
                    ld.rd = t2;
                }

            } else if (i instanceof storeAsm st) {
                if (st.addr instanceof virtualReg) {
                    loadVirtualReg(block, i, (virtualReg) st.addr, t0);
                    st.addr = t0;
                }
                if (st.rs instanceof virtualReg) {
                    loadVirtualReg(block, i, (virtualReg) st.rs, t1);
                    st.rs = t1;
                }

            } else if (i instanceof laAsm la) {
                if (la.rd instanceof virtualReg) {
                    storeVirtualReg(block, i, (virtualReg) la.rd);
                    la.rd = t2;
                }
            }
        }

        block.successors.forEach(this::workInBlock);
    }
}
