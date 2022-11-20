package IR.Node.Instruction;

import Utility.Type.IRType;
import IR.Node.register;

public class alloca extends instruction {
    public IRType allocType;
    public int alignSpace;
    public register rd;

    public alloca() {}
}
