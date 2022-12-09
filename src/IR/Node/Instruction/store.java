package IR.Node.Instruction;

import IR.TypeSystem.*;

public class store extends instruction {
    public register target;
    public entity rs;
    public int align;
    public IRType rsType;

    public store(entity rs, register target, IRType rsType) {
        this.rs = rs;
        this.target = target;
        this.align = rsType.getAlign();
        this.rsType = rsType;
    }
}
