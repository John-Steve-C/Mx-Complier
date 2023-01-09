package Assembly.Instruction;

import Assembly.Operand.*;

public class loadAsm extends AsmInst {
    public reg rd, addr;
    public Imm offset;
    public int byteLen;

    public loadAsm(reg rd, reg addr, Imm offset, int byteLen) {
        this.rd = rd;
        this.addr = addr;
        this.offset = offset;
        this.byteLen = byteLen;
    }

    @Override
    public String toString() {
        String op = null;
        switch (byteLen) {
            case 1 -> op = "lb";
            case 2 -> op = "lh";
            case 4 -> op = "lw";
        }
        return op + " " + rd + ", " + offset + "(" + addr + ")";    // order
    }
}
