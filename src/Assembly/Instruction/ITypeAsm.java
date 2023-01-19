package Assembly.Instruction;

import Assembly.Operand.*;

import java.util.BitSet;

public class ITypeAsm extends AsmInst {
    public reg rs1, rd;
    public Imm imm;
    public CalKind op;   // add -> addi

    public ITypeAsm(CalKind op, reg rd, reg rs1, Imm imm) {
        this.op = op;
        this.rd = rd;
        this.rs1 = rs1;
        this.imm = imm;
    }

    @Override
    public void fillSet() {
        use.set(rs1.getNumber());
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
        if (op.ordinal() < 12) {
            // need imm, like addi
            return op + "i " + rd +", " + rs1 + ", " + imm;
        } else {
            if (imm.value != 0) throw new RuntimeException();
            return op + "z " + rd +", " + rs1;
            // snez/seqz/sgtz in IType, no need imm
        }
    }
}
