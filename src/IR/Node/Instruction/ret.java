package IR.Node.Instruction;

import IR.TypeSystem.*;

public class ret extends terminator {
    public entity value;
    public IRType irType;

    public ret(entity value, IRType irType) {
        this.value = value;
        this.irType = irType;
    }
}
