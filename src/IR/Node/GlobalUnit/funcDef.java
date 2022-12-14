package IR.Node.GlobalUnit;

import IR.TypeSystem.*;
import IR.Node.block;
import IR.Node.Instruction.alloca;

import java.util.ArrayList;
import java.util.HashSet;

public class funcDef extends globalUnit {
    public String funcName = null;
    public IRType returnType = null;
    public ArrayList<IRType> parameters = new ArrayList<>();
    public ArrayList<register> parameterRegs = new ArrayList<>();
    public ArrayList<alloca> allocas = new ArrayList<>();     // 为函数 返回值 & 参数 提前分配寄存器, 也应该位于 block 中
    public HashSet<register> globalVariableUsed = new HashSet<>();
    public block rootBlock = null, returnBlock = null, entryBlock = null;
    public register retReg = null;
    public HashSet<funcDef> directCall = new HashSet<>();   // 当前函数直接调用了哪些函数


    public funcDef() {}

    public funcDef(String funcName, IRType returnType, ArrayList<IRType> parameters) {
        this.funcName = funcName;
        this.returnType = returnType;
        this.parameters = parameters;
    }

    public void push_back(alloca stmt) {
        allocas.add(stmt);
    }
}
