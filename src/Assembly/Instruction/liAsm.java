package Assembly.Instruction;

import Assembly.Operand.*;
public class liAsm extends AsmInst {
    // load imm
    public reg rd;
    public Imm imm;

    public liAsm(reg rd, Imm imm) {
        this.rd = rd;
        this.imm = imm;
    }

    @Override
    public String toString() {
        return "li " + rd + ", " + imm;
    }
}
