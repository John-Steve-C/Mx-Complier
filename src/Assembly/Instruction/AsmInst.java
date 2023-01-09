package Assembly.Instruction;

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

    @Override abstract public String toString();
}
