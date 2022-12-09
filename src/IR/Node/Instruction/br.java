package IR.Node.Instruction;

import IR.Node.*;
import IR.TypeSystem.register;

public class br extends instruction {
    public register val;
    public block trueBranch, falseBranch;

    public br(register val, block trueBranch, block falseBranch) {
        this.val = val;
        this.trueBranch = trueBranch;
        this.falseBranch = falseBranch;
    }
}
