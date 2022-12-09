package IR.Node.Instruction;

import IR.TypeSystem.*;

public class load extends instruction {
    public register rd, ptr;
    public IRType rsType;
    public int align;
    public entity recorder = null;

    public load(register rd, register ptr, IRType rsType) {
        this.rd = rd;
        this.ptr = ptr;
        this.rsType = rsType;
        this.align = rsType.reducePtr().getAlign();
    }
}
