package IR;

import Backend.Pass;
import IR.Node.*;
import IR.Node.IRType;
import IR.Node.classDef;
import IR.Node.funcDef;

import java.io.PrintStream;

public class IRPrinter implements Pass {

    private PrintStream output;

    public IRPrinter(PrintStream out){
        output = out;
    }

    public String getType(IRType t) {
        if (t.isVoid) return "void";

        StringBuilder ret;
        if (t.arrayLen > 0) {
            ret = new StringBuilder("[" + t.arrayLen + " x "+ getType(t.subArray) + "]");
        } else if (t.clsDef == null) {
            ret = new StringBuilder("i" + t.intLen);
        } else {
            if (t.clsDef.name != null) ret = new StringBuilder("%struct." + t.clsDef.name);
            else ret = new StringBuilder(getUnnamedClassType(t.clsDef));
        }

        ret.append("*".repeat(Math.max(0, t.ptrNum)));
        return ret.toString();
    }

    public String getUnnamedClassType(classDef cls) {
        StringBuilder ret = new StringBuilder("{ ");
        int len = cls.members.size();
        for (int i = 0;i < len - 1; ++i) {
            ret.append(getType(cls.members.get(i).reducePtr())).append(", ");
        }
        if (len > 0) ret.append(getType(cls.members.get(len - 1).reducePtr())).append(" }");
        else ret.append("}");

        return ret.toString();
    }

    @Override
    public void visitBlock(block blk) {

    }

    @Override
    public void visitProgram(program prog) {

    }

    @Override
    public void visitFuncDef(funcDef func) {
        output.print("define " + func.retType + " @" + func.funcName + "(");
        int len = func.parameters.size();



        for (int i = 0;i < len - 1; ++i) {
            output.print(getType(func.parameters.get(i)) + " %" + i + ", ");
        }
        if (len > 0) {
            output.print(getType(func.parameters.get(len - 1)) + " %" + (len - 1) + ")");
        } else {
            output.print(")");
        }



        output.println();
    }

    @Override
    public void visitClassDef(classDef cls) {
        // classDef != classType
        output.print("%struct." + cls.name + " = type { ");
        int len = cls.members.size();
        for (int i = 0;i < len - 1; ++i) {
            output.print(getType(cls.members.get(i).reducePtr()) + ", ");
        }
        if (len > 0) output.print(getType(cls.members.get(len - 1).reducePtr()) + " }");
        else output.print("}");

        output.println();
    }

    @Override
    public void visitGlobalVar(globalVarDeclaration decl) {

    }

    @Override
    public void visitGlobalString(globalStringConst str) {

    }

    @Override
    public void visitDeclaration(declaration decl) {

    }

}
