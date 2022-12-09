package IR.Node.GlobalUnit;

import IR.TypeSystem.IRType;
import IR.Node.Instruction.instruction;

import java.util.ArrayList;

public class declare extends instruction {
    public IRType returnType = null;
    public ArrayList<IRType> parameter = new ArrayList<>();
    public String funcName = null;

    public declare(IRType retType, String name) {
        returnType = retType;
        funcName = name;
    }
}
