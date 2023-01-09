package Assembly.Instruction;

import Assembly.Operand.*;

public class ITypeAsm extends AsmInst {
    public reg rs1, rd;
    public Imm imm;
    public CalKind op;   // add -> addi

    public ITypeAsm(CalKind op, reg rd, reg rs1, Imm imm) {
        this.op = op;
        this.rd = rd;
        this.rs1 = rs1;
        this.imm = imm;
    }

    @Override
    public String toString() {
        if (op.ordinal() < 12) {
            // need imm, like addi
            return op + "i " + rd +", " + rs1 + ", " + imm;
        } else {
            if (imm.value != 0) throw new RuntimeException();
            return op + "z " + rd +", " + rs1;
            // snez/seqz/sgtz in IType, no need imm
        }
    }
}
