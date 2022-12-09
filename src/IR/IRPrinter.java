package IR;

import Backend.Pass;
import IR.Node.*;
import IR.Node.Instruction.*;
import IR.Node.GlobalUnit.*;
import IR.TypeSystem.IRType;
import IR.TypeSystem.register;

import java.io.PrintStream;
import java.util.HashMap;

public class IRPrinter implements Pass {

    private PrintStream output;
    private int regCnt = 0;    //统计输出时使用虚拟寄存器的总数
    private HashMap<register, Integer> regIndex = new HashMap<>();

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

    public String getRegName(register r) {
        if (regIndex.containsKey(r)) return "%" + regIndex.get(r);
        r.regNum = regCnt;
        regIndex.put(r, regCnt);
        return "%" + (regCnt++);
    }

    public String getBlockName(block blk) {
        return blk.name;
    }

    public void print(instruction inst) {
        if (inst instanceof alloca a) {
            output.print(getRegName(a.rd) + " = alloca " + getType(a.allocType) + ", align " + a.alignSpace);
        } else if (inst instanceof br a) {
            if (a.val == null) {
                output.print("br label %" + getBlockName(a.trueBranch));
            } else {
                output.print("br i1 " + getRegName(a.val) + ", label %" + getBlockName(a.trueBranch)
                                + ", label %" + getBlockName(a.falseBranch));
            }
        } else if (inst instanceof call a) {

        } else if (inst instanceof store a) {

        } else if (inst instanceof load a) {

        } else if (inst instanceof icmp a) {
            
        }
    }

    @Override
    public void visitBlock(block blk) {
        output.print(blk.label + ":");
        blk.stmt.forEach(inst -> {
        });
    }

    @Override
    public void visitProgram(Program prog) {

    }

    @Override
    public void visitFuncDef(funcDef func) {
        output.print("define " + getType(func.retType) + " @" + func.funcName + "(");
        int len = func.parameters.size();

        register r;

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
        output.print("@" + decl.name + " = global " + getType(decl.type)
                    + " " + decl.reg.value.toString() + ", align " + decl.align);
    }

    @Override
    public void visitGlobalString(globalStringConst str) {

    }

    @Override
    public void visitDeclaration(declare decl) {

    }

}
