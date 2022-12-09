package IR.TypeSystem;

import

public class regTypePair {
    public register reg;
    public IRType irType;

    public regTypePair(register reg,IRType irType){
        this.reg = reg;
        this.irType = irType;
    }
}
