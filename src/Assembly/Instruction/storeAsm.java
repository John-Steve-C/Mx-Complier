package Assembly.Instruction;

import Assembly.Operand.*;
public class storeAsm extends AsmInst {
    public reg rs, addr;
    public Imm offset;
    public int byteLen;

    public storeAsm(reg rs, reg addr, Imm offset, int byteLen) {
        this.rs = rs;
        this.addr = addr;
        this.offset = offset;
        this.byteLen = byteLen;
    }

    @Override
    public String toString() {
        String op = null;
        switch (byteLen) {
            case 1 -> op = "sb";
            case 2 -> op = "sh";
            case 4 -> op = "sw";
        }
        return op + " " + rs + ", " + offset + "(" + addr + ")";
    }
}
