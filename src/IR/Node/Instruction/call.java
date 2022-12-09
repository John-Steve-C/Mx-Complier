package IR.Node.Instruction;

import IR.TypeSystem.*;
import IR.Node.GlobalUnit.funcDef;

import java.util.LinkedList;

public class call extends instruction {
    public register rd;
    public IRType rdType;
    public String funcName;
    public funcDef funcAssociated;
    public LinkedList<entityTypePair> parameters = new LinkedList<>();

    public call(register rd, IRType rdType, String funcName, funcDef funcAssociated) {
        this.funcAssociated = funcAssociated;
        this.rd = rd;
        this.rdType = rdType;
        this.funcName = funcName;
    }

    public void push_back(entityTypePair par) {
        parameters.add(par);
    }

}
