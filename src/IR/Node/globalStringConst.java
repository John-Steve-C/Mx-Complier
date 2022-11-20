package IR.Node;

import Utility.Type.IRType;

public class globalStringConst {
    public String content, rawStr;
    public int counter = 0;
    public register reg;
    public IRType type;

    public globalStringConst(String content, int counter, register reg, IRType type) {
        this.content = content;
        this.counter = counter;
        this.reg = reg;
        this.type = type;
    }
}
