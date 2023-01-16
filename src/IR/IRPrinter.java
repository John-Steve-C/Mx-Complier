package IR;

import IR.Node.*;
import IR.Node.Instruction.*;
import IR.Node.GlobalUnit.*;
import IR.TypeSystem.*;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

public class IRPrinter {

    private PrintStream output;
    private int cnt = 0;    //统计输出时使用 虚拟寄存器 的总数
    private HashMap<register, Integer> regIndex = new HashMap<>();  // virtual register for local var
    private HashMap<register, String> regGlobal = new HashMap<>();  // for global var
    private HashMap<block, Integer> blockIndex = new HashMap<>();
    private HashSet<block> blockVisited =null;
    private LinkedList<block> blockQueue = null;

    public IRPrinter(PrintStream out){
        output = out;
    }


    public void visitProgram(Program prog) {
        prog.cls.forEach(this::visitClassDef);
        if (prog.cls.size() != 0) output.println();

        prog.varDecl.forEach(this::visitGlobalVar);
        if (prog.varDecl.size() != 0) output.println();

        prog.strConst.forEach(this::visitGlobalString);
        if (prog.strConst.size() != 0) output.println();

        prog.func.forEach(this::visitFuncDef);
        if (prog.func.size() != 0) output.println();

        prog.decl.forEach(this::visitDeclare);
        if (prog.decl.size() != 0) output.println();
    }

    public void visitBlock(block blk) {
        output.println("\n"+ ((blk.comment != null) ? ";" + blk.comment + "\n" : "") + getBlockName(blk) + ":" );
//        output.println(";" + getBlockName(blk) + " " +getBlockName(blk.IDom));
        for (instruction stmt = blk.headStmt; stmt!= null; stmt = stmt.next) {
            print(stmt);
            if (stmt instanceof terminator) break;
        }
    }

    public void visitFuncDef(funcDef f) {
        cnt = 0;
        blockIndex = new HashMap<>();
        blockVisited = new HashSet<>();
        regIndex = new HashMap<>();
        blockQueue = new LinkedList<>();

        collectBlocks(f.rootBlock);
        //run naming
        int len = f.parameterRegs.size();
        for (int i = 0; i < len; ++i) {
            getRegName(f.parameterRegs.get(i));
        }
        getBlockName(f.rootBlock);
//        for (alloca alloca : f.allocas) {
//            runNaming(alloca);
//        }
        // todo: specially to add allocas into rootblock
        // no need to runNaming ahead of block
        for (int i = f.allocas.size() - 1; i >= 0; --i) {
            f.rootBlock.push_front(f.allocas.get(i));
        }

        blockQueue.forEach(this::runNaming);
        //print
        output.print("define " + getType(f.returnType) + " @" + f.funcName + "(");
        for (int i = 0; i < len; ++i) {
            output.print(getType(f.parameters.get(i)) + " " + getRegName(f.parameterRegs.get(i)));
            if (i < len - 1) output.print(",");
        }
        output.println("){");

//        for (alloca alloca : f.allocas) {
//            print(alloca);
//        }
        blockQueue.forEach(this::visitBlock);
        output.println("}\n");
    }

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

    public void visitGlobalVar(globalVarDeclaration g) {
        regGlobal.put(g.rd, g.name);
        output.println(getRegName(g.rd) + " = global " + getType(g.rsType) + " " + getEntityString(g.rs) + ", align " + g.align);
    }

    public void visitGlobalString(globalStringConst str) {
        regGlobal.put(str.rd, ".str" + ((str.counter == 0) ? "" : "." + str.counter));
        output.println(getRegName(str.rd) + " = constant " + getType(str.type) + " c" + str.content + ", align 1");
    }

    public void visitDeclare(declare dec) {
        output.print("declare " + getType(dec.returnType) + " @" + dec.funcName + "(");
        int len = dec.parameter.size();
        for (int i = 0; i < len; ++i) {
            output.print(getType(dec.parameter.get(i)));
            if (i != len - 1) output.print(", ");
        }
        output.println(")");
    }

    //----------------------------------------------------------------------------------------------------------
    public void collectBlocks(block b){
        blockVisited.add(b);
        blockQueue.add(b);
        if (b.tailStmt instanceof br Br) {
            if (!blockVisited.contains(Br.trueBranch)) {
                collectBlocks(Br.trueBranch);
            }
            if (Br.falseBranch != null && !blockVisited.contains(Br.falseBranch)) {
                collectBlocks(Br.falseBranch);
            }
        }
    }

    private void runNaming(block b) {
        getBlockName(b);
        for (instruction stmt = b.headStmt; stmt != null;stmt = stmt.next) {
            runNaming(stmt);
            if (stmt instanceof terminator) break;
        }
    }

    private void runNaming(instruction s) {
        if (s.removed) return;
        if (s instanceof alloca a) {
            getRegName(a.rd);
        } else if (s instanceof binary b) {
            getEntityString(b.rd);
        } else if (s instanceof br) {
        } else if (s instanceof call c) {
            if (c.rd != null) getRegName(c.rd);
        } else if (s instanceof convertOp con) {
            getRegName(con.rd);
        } else if (s instanceof getelementptr g) {
            getRegName(g.rd);
        } else if (s instanceof icmp ic) {
            getRegName(ic.rd);
        } else if (s instanceof load l) {
            getRegName(l.rd);
        } else if (s instanceof phi p) {
            getRegName(p.rd);
        } else if (s instanceof ret) {
        } else if (s instanceof store) {
        } else if (s instanceof bitcast b) {
            getRegName(b.rd);
        }
    }

    //----------------------------------------------------------------------------------------------------------

    private String getType(IRType t) {
        if (t.isVoid) return "void";

        StringBuilder ret;
        if (t.arrayLen > 0) {
            ret = new StringBuilder("[" + t.arrayLen + " x " + getType(t.arraySubIR) + "]");
        } else if (t.cDef == null) {
            ret = new StringBuilder("i" + t.intLen);
        } else {
            if (t.cDef.name != null) ret = new StringBuilder("%struct." + t.cDef.name);
            else ret = new StringBuilder(getUnnamedClassType(t.cDef));
        }

        ret.append("*".repeat(Math.max(0, t.ptrNum)));
        return ret.toString();
    }

    private String getUnnamedClassType(classDef cls) {
        StringBuilder ret = new StringBuilder("{ ");
        int len = cls.members.size();
        for (int i = 0;i < len - 1; ++i) {
            ret.append(getType(cls.members.get(i).reducePtr())).append(", ");
        }
        if (len > 0) ret.append(getType(cls.members.get(len - 1).reducePtr())).append(" }");
        else ret.append("}");

        return ret.toString();
    }

    private String getTypeDecPointer(IRType t) {
        if (t.isVoid) return "void";

        StringBuilder ret;
        if (t.cDef == null) {
            ret = new StringBuilder("i" + t.intLen);
        } else {
            ret = new StringBuilder("%struct." + t.cDef.name);
        }
        ret.append("*".repeat(Math.max(0, t.ptrNum - 1)));

        if (t.arrayLen > 0) {
            ret = new StringBuilder("[" + t.arrayLen + " x " + ret.toString() + "]");
        }
        return ret.toString();
    }

    private String getEntityString(entity e) {
        if (e == null) return "";
        if (e instanceof register) return getRegName((register) e);
        else {
            constant constE = (constant) e;
            if (constE.kind == constant.constType.INT) return constE.getIntValue() + "";
            else if (constE.kind == constant.constType.STRING) return constE.getStringValue();
            else if (constE.kind == constant.constType.BOOL) return (constE.getBoolValue() ? "1" : "0");
            else return "null";
        }
    }

    private String getRegName(register r) {
        if (regIndex.containsKey(r)) return "%" + regIndex.get(r);
        if (regGlobal.containsKey(r)) return "@" + regGlobal.get(r);
        r.registerCount = cnt;
        regIndex.put(r, cnt);
        return "%" + (cnt++);
    }

    private String getBlockName(block blk) {
        if (blockIndex.containsKey(blk)) return blockIndex.get(blk) + "";
        else {
            if (blk != null) blk.blockIndex = cnt;
            blockIndex.put(blk, cnt++);
            return (cnt - 1) + "";
        }
    }

    private String getCalOp(binary.opType op) {
        return switch (op) {
            case ADD -> "add";
            case SUB -> "sub";
            case OR -> "or";
            case XOR -> "xor";
            case MUL -> "mul";
            case SDIV -> "sdiv";
            case MOD -> "srem";
            case ASHR -> "ashr";
            case SHL -> "shl";
            case AND -> "and";
            case LSHR -> "lshr";
        };
    }

    private String getCmpOp(icmp.cmpOpType op) {
        return switch (op) {
            case SLE -> "sle";
            case SLT -> "slt";
            case SGE -> "sge";
            case SGT -> "sgt";
            case EQ -> "eq";
            case NEQ -> "ne";
        };
    }

    private String getConvertOp(convertOp.convertType op) {
        return switch (op) {
            case TRUNC -> "trunc";
            case SEXT -> "sext";
            case ZEXT -> "zext";
        };
    }

    public void print(instruction inst) {
        output.print("\t");

        if (inst instanceof alloca Inst) {
            output.print(getRegName(Inst.rd) + " = alloca " + getType(Inst.irType) + ", align " + Inst.align);
        } else if (inst instanceof binary Inst) {
            String op = getCalOp(Inst.op);
            output.print(getEntityString(Inst.rd) + " = " + op + " " +  getType(Inst.irType) + " " + getEntityString(Inst.rs1) + ", " + getEntityString(Inst.rs2));
        } else if (inst instanceof bitcast Inst) {
            output.print(getRegName(Inst.rd) + " = bitcast " + getType(Inst.rsType) + " " + getRegName(Inst.rs) + " to " + getType(Inst.rdType));
        } else if (inst instanceof br Inst) {
            if (Inst.val == null) {
                output.print("br label %" + getBlockName(Inst.trueBranch));
            } else {
                output.print("br i1 " + getRegName(Inst.val) + ", label %" + getBlockName(Inst.trueBranch) + ", label %" + getBlockName(Inst.falseBranch));
            }
        } else if (inst instanceof call Inst) {
            if (Inst.rd != null) output.print(getRegName(Inst.rd) + " = call " + getType(Inst.rdType) + " @" + Inst.funcName + "(");
            else output.print("call " + getType(Inst.rdType) + " @" + Inst.funcName + "(");
            int len = Inst.parameters.size();
            for (int i = 0; i < len - 1; ++i) {
                entityTypePair para = Inst.parameters.get(i);
                output.print(getType(para.type) + " " + getEntityString(para.en) + ", ");
            }
            if (len > 0) {
                entityTypePair para = Inst.parameters.get(len - 1);
                output.print(getType(para.type) + " " + getEntityString(para.en) + ")");
            } else output.print(")");
        } else if (inst instanceof convertOp Inst) {
            output.print(getRegName(Inst.rd) + " = " + getConvertOp(Inst.kind) + " " + getType(Inst.rsType) + " " + getEntityString(Inst.rs) + " to " + getType(Inst.rdType));
        } else if (inst instanceof getelementptr Inst) {
            if (Inst.rsType.cDef != null && Inst.rsType.ptrNum == 1)
                output.print(getRegName(Inst.rd) + " = getelementptr " + getTypeDecPointer(Inst.rsType) + ", " + getType(Inst.rsType) + " " + getRegName(Inst.rs) +
                        ", i32 " + getEntityString(Inst.locator1) + ", i32 " + getEntityString(Inst.locator2));
            else
                output.print(getRegName(Inst.rd) + " = getelementptr " + getTypeDecPointer(Inst.rsType) + ", " + getType(Inst.rsType) + " " + getRegName(Inst.rs) +
                        ", i32 " + getEntityString(Inst.locator1));
        } else if (inst instanceof icmp Inst) {
            output.print(getRegName(Inst.rd) + " = icmp " + getCmpOp(Inst.cmpOp) + " " + getType(Inst.rsType) + " " + getEntityString(Inst.rs1) + ", " + getEntityString(Inst.rs2));
        } else if (inst instanceof load Inst) {
            output.print(getRegName(Inst.rd) + " = load " + getType(Inst.rsType.reducePtr()) + ", " + getType(Inst.rsType) + " " + getRegName(Inst.ptr) + ", align " + Inst.align);
        } else if (inst instanceof phi Inst) {
            output.print(getRegName(Inst.rd) + " = phi " + getType(Inst.rdType) + " ");
            int len = Inst.entityBlockPairs.size();
            for (int i = 0; i < len - 1; ++i) {
                entityBlockPair enBl = Inst.entityBlockPairs.get(i);
                output.print("[ " + getEntityString(enBl.en) + ", %" + getBlockName(enBl.blk) + " ], ");
            }
            entityBlockPair enBl = Inst.entityBlockPairs.get(len - 1);
            output.print("[ " + getEntityString(enBl.en) + ", %" + getBlockName(enBl.blk) + " ]");
        } else if (inst instanceof ret Inst) {
            if (Inst.irType.isVoid) output.print("ret void");
            else output.print("ret " + getType(Inst.irType) + " " + getEntityString(Inst.value));
        } else if (inst instanceof store Inst) {
            output.print("store " + getType(Inst.rsType) + " " + getEntityString(Inst.rs) + ", " + getType(Inst.rsType.getPtr()) + " " + getRegName(Inst.target) + ", align " + Inst.align);
        }

//        if (inst.comments != null) {
//            output.println("; " + inst.comments);
//        } else {
            output.println();
//        }
    }

}
