package IR.Node.Instruction;

import IR.TypeSystem.*;

import java.util.LinkedList;

public class alloca extends instruction {
    public register rd;
    public int align;
    public IRType irType;
    public LinkedList<user> users;

    public int allocaNumber;

    public alloca(register rd, IRType irType) {
        this.rd = rd;
        this.align = irType.getAlign();
        this.irType = irType;
        this.users = new LinkedList<>();
    }

    @Override
    public String toString() {
        return allocaNumber + " " + rd;
    }
}