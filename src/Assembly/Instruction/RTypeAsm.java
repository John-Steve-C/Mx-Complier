package Assembly.Instruction;

import Assembly.Operand.*;

import java.util.BitSet;

public class RTypeAsm extends AsmInst {
    public reg rd, rs1, rs2;
    public CalKind op;

    public RTypeAsm(CalKind op, reg rd, reg rs1, reg rs2) {
        this.rd = rd;
        this.rs1 = rs1;
        this.rs2 = rs2;
        this.op = op;
    }


    @Override
    public void fillSet() {
        def.set(rd.getNumber());
        use.set(rs1.getNumber());
        use.set(rs2.getNumber());
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
        if (op.ordinal() < 12) {
            return op + " " + rd + ", " + rs1 + ", " + rs2;
        } else {
            // no seq/sne/sgt in RType
            throw new RuntimeException("wrong R-Type inst");
        }
    }
}
