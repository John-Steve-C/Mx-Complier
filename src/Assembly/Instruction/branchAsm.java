package Assembly.Instruction;

import Assembly.*;
import Assembly.Operand.*;

import java.util.BitSet;

public class branchAsm extends AsmInst {
    public reg src1, src2;
    public AsmBlock dest;
    public CmpKind op;

    public branchAsm(CmpKind op, reg src1, reg src2, AsmBlock destination) {
        this.op = op;
        this.src1 = src1;
        this.src2 = src2;
        this.dest = destination;
    }


    @Override
    public void fillSet() {
        // set(index) :把第 index 位设置成 1
        use.set(src1.getNumber());
        if (src2 != null) use.set(src2.getNumber());
    }

    @Override
    public void calInst() {
        liveOut = new BitSet(bitSize);
        if (next != null) liveOut.or(next.liveIn);
        if (dest != null) liveOut.or(dest.headInst.liveIn);
        liveIn = (BitSet) use.clone();
        BitSet tmp = (BitSet) liveOut.clone();
        tmp.andNot(def);
        liveIn.or(tmp);
    }

    @Override
    public boolean check() {
        return false;
    }

    @Override
    public String toString() {
        if (src2 == null) {
            // beqz : (src1 == 0)
            return "b" + op + "z " + src1 + ", " + dest;    // auto call toString()
        } else {
            // beq : (src1 == src2)
            return "b" + op + " " + src1 + ", " + src2 + ", " + dest;
        }
    }
}
