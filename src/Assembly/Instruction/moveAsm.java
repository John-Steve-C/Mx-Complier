package Assembly.Instruction;

import Assembly.Operand.*;

import java.util.BitSet;

public class moveAsm extends AsmInst {
    // rd = rs1
    public reg rd, rs1;

    public moveAsm(reg rd, reg rs1) {
        this.rd = rd;
        this.rs1 = rs1;
    }

    @Override
    public void fillSet() {
        def.set(rd.getNumber());
        use.set(rs1.getNumber());
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
        return "mv " + rd + ", " + rs1;
    }
}
