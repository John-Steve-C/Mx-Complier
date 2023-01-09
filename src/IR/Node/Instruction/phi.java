package IR.Node.Instruction;

import IR.TypeSystem.*;
import Assembly.AsmBlock;
import java.util.LinkedList;

public class phi extends instruction {
    // used to implement the phi node in the SSA graph representing the function.
    // 表示不同 block 之间跳转同名变量的区分
    // (block1, entity1) -> (b2, e2)
    public IRType rdType;
    public register rd;
    public LinkedList<entityBlockPair> entityBlockPairs = new LinkedList<>();
    public alloca creator = null;
    public AsmBlock asmParentBlock = null;

    public phi(register rd, IRType irType) {
        this.rd = rd;
        this.rdType = irType;
    }

    public void push_back(entityBlockPair t) {
        entityBlockPairs.add(t);
    }
}
