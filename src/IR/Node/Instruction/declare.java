package IR.Node.Instruction;

import Utility.Type.IRType;

import java.util.ArrayList;

public class declare extends instruction{
    public IRType returnType = null;
    public ArrayList<IRType> parameter = null;
    public String funcName = null;

    public declare(IRType retType, String name) {
        returnType = retType;
        funcName = name;
    }
}
