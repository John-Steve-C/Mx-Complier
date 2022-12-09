package IR.Node.Instruction;

import IR.TypeSystem.*;

public class convertOp extends instruction {
    public enum convertType {
        TRUNC, ZEXT, SEXT
    }

    public register rd;
    public entity rs;
    public convertType kind;
    public IRType rsType, rdType;

    public convertOp(register rd, entity rs, convertType kind, IRType rdType, IRType rsType) {
        this.rd = rd;
        this.rs = rs;
        this.kind = kind;
        this.rdType = rdType;
        this.rsType = rsType;
    }
}
