package Assembly.Instruction;

import Assembly.Operand.*;

import java.util.BitSet;

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
    public void fillSet() {
        use.set(addr.getNumber());
        def.set(rd.getNumber());
    }

    @Override
    public void calInst() {
        liveOut = new BitSet(bitSize);
        if (next != null) liveOut.or(next.liveIn);
        liveIn = (BitSet) use.clone();
        BitSet tmp = (BitSet) liveOut.clone();
        tmp.andNot(def);
        liveIn.or(tmp);
    }

    @Override
    public boolean check() {
        return !liveOut.get(rd.getNumber()) && rd.getNumber() >= 32;
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
