package Assembly.Instruction;

import Assembly.*;
import Assembly.Operand.*;

public class branchAsm extends AsmInst {
    public reg src1, src2;
    public AsmBlock dest;
    public CmpKind op;

    public branchAsm(CmpKind op, reg src1, reg src2, AsmBlock destination) {
        this.op = op;
        this.src1 = src1;
        this.src2 = src2;
        this.dest = destination;
    }

    @Override
    public String toString() {
        if (src2 == null) {
            // beqz : (src1 == 0)
            return "b" + op + "z " + src1 + ", " + dest;    // auto call toString()
        } else {
            // beq : (src1 == src2)
            return "b" + op + src1 + ", " + src2 + ", " + dest;
        }
    }
}
