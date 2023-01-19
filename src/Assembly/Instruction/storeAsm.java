package Assembly.Instruction;

import Assembly.Operand.*;

import java.util.BitSet;

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
    public void fillSet() {
        use.set(rs.getNumber());
        use.set(addr.getNumber());
    }

    @Override
    public void calInst() {
        liveOut = new BitSet(bitSize);
        if (next != null) liveOut.or(next.liveIn);
        liveIn = (BitSet) use.clone();
        BitSet tmp = (BitSet) liveOut.clone();
        tmp.andNot(def);        // def is empty
        liveIn.or(tmp);
    }

    @Override
    public boolean check() {
        return false;
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
