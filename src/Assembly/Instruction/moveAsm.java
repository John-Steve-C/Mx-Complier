package Assembly.Instruction;

import Assembly.Operand.*;
public class moveAsm extends AsmInst {
    // rd = rs1
    public reg rd, rs1;

    public moveAsm(reg rd, reg rs1) {
        this.rd = rd;
        this.rs1 = rs1;
    }

    @Override
    public String toString() {
        return "mv " + rd + ", " + rs1;
    }
}
