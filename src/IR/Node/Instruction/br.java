package IR.Node.Instruction;

import IR.Node.*;

public class br extends instruction {
    public register val;
    public block trueBranch, falseBranch;
}
