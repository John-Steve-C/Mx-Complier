package Assembly.Instruction;

import Assembly.AsmBlock;

import java.util.BitSet;

public class jumpAsm extends AsmInst {
    public AsmBlock dest;

    public jumpAsm(AsmBlock block) {
        dest = block;
    }

    @Override
    public void fillSet() {}

    @Override
    public void calInst() {
        liveOut = new BitSet(bitSize);
        // jump has nothing to do with next!
        if (dest != null) liveOut.or(dest.headInst.liveIn);
        liveIn = (BitSet) liveOut.clone();
    }

    @Override
    public boolean check() {
        return false;
    }

    @Override
    public String toString() {
        return "j " + dest;
    }
}
