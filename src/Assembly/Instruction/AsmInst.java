package Assembly.Instruction;

import java.util.BitSet;

public abstract class AsmInst {
    public enum CalKind {
        mul,div,rem,add,sub,xor,or,and,sll,srl,sra, // 0-10, rem = mod
        slt,        // 11, no need imm
        seq,sne,sgt // pseudo instruction, print as seqz/snez/sgtz in IType
    }

    public enum CmpKind {
        eq, ne, lt, le, gt, ge
    }

    public AsmInst pre = null, next = null;

    public int bitSize = 0;
    public BitSet def, use, liveIn, liveOut; // liveness analysis
    // 用 32位二进制表示每个寄存器是否被占用
    // use = gen, def = kill (in wikipedia)
    // def(rd) = (rs) use[1] + use[2]...
    // liveOut = \lor liveIn of successor
    // liveIn = gen \lor (liveOut - kill) = use \lor (liveOut - def)
    // - (集合的差) 用 andNot 实现 a & (~b)

    abstract public void fillSet();
    abstract public void calInst();
    abstract public boolean check();

    @Override abstract public String toString();
}
