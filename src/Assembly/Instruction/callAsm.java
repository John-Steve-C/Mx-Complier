package Assembly.Instruction;

import java.util.BitSet;
import java.util.LinkedList;
import Assembly.Operand.*;
public class callAsm extends AsmInst {
    public String funcName;
    public LinkedList<reg> parameters = new LinkedList<>();

    public callAsm(String name) {
        this.funcName = name;
    }

    @Override
    public void fillSet() {
        def.set(0, 8);      // [0,8) = 1
        def.set(10, 18);    // parameter & return value
        def.set(28, 32);    // tmpReg
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
        return false;
    }

    @Override
    public String toString() {
        return "call " + funcName;
    }
}
