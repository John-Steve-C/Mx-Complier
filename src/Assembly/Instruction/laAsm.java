package Assembly.Instruction;

import Assembly.Operand.*;

import java.util.BitSet;

public class laAsm extends AsmInst {
    // load address
    public reg rd;
    public String addr; // 0x..

    public laAsm(reg rd, String addr) {
        this.rd = rd;
        this.addr = addr;
    }

    @Override
    public void fillSet() {
        def.set(rd.getNumber());
    }

    @Override
    public void calInst() {
        liveOut = new BitSet(bitSize);
        if (next != null) liveOut.or(next.liveIn);
        liveIn = (BitSet) use.clone();      // use is empty
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
        return "la " + rd + ", " + addr;
    }
}
