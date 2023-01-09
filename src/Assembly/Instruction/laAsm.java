package Assembly.Instruction;

import Assembly.Operand.*;
public class laAsm extends AsmInst {
    // load address
    public reg rd;
    public String addr; // 0x..

    public laAsm(reg rd, String addr) {
        this.rd = rd;
        this.addr = addr;
    }

    @Override
    public String toString() {
        return "la " + rd + ", " + addr;
    }
}
