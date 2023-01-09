package Assembly.Instruction;

import Assembly.Operand.*;
public class RTypeAsm extends AsmInst {
    public reg rd, rs1, rs2;
    public CalKind op;

    public RTypeAsm(CalKind op, reg rd, reg rs1, reg rs2) {
        this.rd = rd;
        this.rs1 = rs1;
        this.rs2 = rs2;
        this.op = op;
    }


    @Override
    public String toString() {
        if (op.ordinal() < 12) {
            return op + " " + rd + ", " + rs1 + ", " + rs2;
        } else {
            // no seq/sne/sgt in RType
            throw new RuntimeException("wrong R-Type inst");
        }
    }
}
