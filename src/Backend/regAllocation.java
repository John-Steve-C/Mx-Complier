package Backend;

import Assembly.*;
import Assembly.Operand.*;
import Assembly.Instruction.*;

public class regAllocation {
    public AsmProgram asmProg;
    private physicalReg sp, t0, t1, t2, s0, ra, zero, t3, t6;
    private AsmBlock tailBlock;

    private int getLow12(int constValue) {
        return (0xfffff800) * ((constValue >> 11) & 1) + (constValue & 0x7ff);
    }

    public regAllocation(AsmProgram asmProgram) {
        asmProg = asmProgram;
        sp = asmProg.physicalRegs.get(2);
        t0 = asmProg.physicalRegs.get(5);
        t1 = asmProg.physicalRegs.get(6);
        t2 = asmProg.physicalRegs.get(7);
        t3 = asmProg.physicalRegs.get(28);
        t6 = asmProg.physicalRegs.get(31);
        ra = asmProg.physicalRegs.get(1);
        s0 = asmProg.physicalRegs.get(8);
    }
}
