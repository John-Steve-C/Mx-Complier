package Assembly.Instruction;

import Assembly.Operand.*;

import java.util.BitSet;

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
        return "lui " + rd + ", " + imm;
    }
}
