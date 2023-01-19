package Assembly.Instruction;

import Assembly.Operand.*;

import java.util.BitSet;

public class liAsm extends AsmInst {
    // load imm
    public reg rd;
    public Imm imm;

    public liAsm(reg rd, Imm imm) {
        this.rd = rd;
        this.imm = imm;
    }

    @Override
    public void fillSet() {
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
        return "li " + rd + ", " + imm;
    }
}
