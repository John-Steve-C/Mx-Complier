package IR.Node.Instruction;
import IR.Node.block;

public abstract class instruction {
    public instruction pre = null, next = null;
    public boolean removed = false, isActivate = false;
    public block parentBlock = null;
    public String Comments = null;

}