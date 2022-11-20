package IR.Node;

import Utility.Type.IRType;

// int c = 50;
// @c = global i32 50, align 4

public class globalVarDeclaration {
    public IRType type;
    public String name;
    public int align;
    public register reg;

    public globalVarDeclaration(register r, IRType t, String name, String value) {
        reg = r;
        type = t;
        this.name = name;
        this.align = t.getAlign();
    }
}
