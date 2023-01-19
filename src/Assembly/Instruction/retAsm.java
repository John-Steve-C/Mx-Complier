package Assembly.Instruction;

import java.util.BitSet;

public class retAsm extends AsmInst {
    @Override
    public void fillSet() {}

    @Override
    public void calInst() {
        liveOut = new BitSet(bitSize);
        liveOut.set(0, 5);
        liveOut.set(8);
        liveIn =  new BitSet(bitSize);
    }

    @Override
    public boolean check() {
        return false;
    }

    @Override
    public String toString() {
        return "ret";
    }
}
