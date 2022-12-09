package IR.TypeSystem;

import IR.Node.Instruction.instruction;

import java.util.LinkedList;

// virtual register
// 只'保存'地址
public class register extends entity {
    public String label = null;
    public int loopDepth = 0;
    public LinkedList<instruction> uses;
    public instruction def;

    public boolean isLoopInvariant;

    public register() {
        super();
        registerCount = --curCount;
    }

    public register(int t) {
        registerCount = t;
    }

    // for debug
    static public int curCount = 0;
    public int registerCount = 0;

    @Override
    public String toString() {
        return "" + registerCount;
    }

    @Override
    public boolean entityEquals(entity en) {
        if (en instanceof constant) return false;
        return this == en;
    }

    @Override
    public entity clone() {
        return new register();
    }
}
