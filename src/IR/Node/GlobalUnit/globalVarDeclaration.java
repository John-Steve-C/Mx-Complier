package IR.Node.GlobalUnit;

import IR.TypeSystem.register;
import IR.TypeSystem.entity;
import IR.TypeSystem.IRType;

// int c = 50;
// @c = global i32 50, align 4

public class globalVarDeclaration {
    public IRType rsType;
    public String name;
    public int align;
    public register rd;
    public entity rs;

    public globalVarDeclaration(register rd, IRType t, entity rs, String name) {
        this.rd = rd;
        this.rs = rs;
        this.rsType = t;
        this.name = name;
        this.align = t.getAlign();
    }
}
