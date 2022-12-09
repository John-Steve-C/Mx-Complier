package IR.Node.Instruction;

import IR.TypeSystem.*;

public class bitcast extends instruction{
    public register rd, rs;
    public IRType rdType, rsType;

    public bitcast(register rd, register rs, IRType rdType, IRType rsType) {
        this.rd = rd;
        this.rs = rs;
        this.rdType = rdType;
        this.rsType = rsType;
    }
}
