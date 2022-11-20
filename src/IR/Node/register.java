package IR.Node;

import IR.Node.Instruction.instruction;
import Utility.Type.IRType;

// virtual register
public class register {
    public String name = null;
    // regNum stands for the name of register ?
    public int regNum = 0;
    public IRType saveType = null;
    public literal value = null;
    public instruction def = null;

    public register() {}

    public register(int num, literal val) {
        regNum = num;
        value = val;
    }
}
