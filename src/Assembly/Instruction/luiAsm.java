package Assembly.Instruction;

import Assembly.Operand.*;
public class luiAsm extends AsmInst {
    // Load Upper Imm
    // rd = imm << 12
    // 低 12 位用 0 填充
    public reg rd;
    public Imm imm;

    public luiAsm(reg rd, Imm imm) {
        this.rd = rd;
        this.imm = imm;
    }

    @Override
    public String toString() {
        return "lui " + rd + ", " + imm;
    }
}
