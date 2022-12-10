package Backend;

import IR.Node.*;
import IR.Node.GlobalUnit.declare;
import IR.Node.GlobalUnit.classDef;
import IR.Node.GlobalUnit.funcDef;
import IR.Node.GlobalUnit.globalStringConst;
import IR.Node.GlobalUnit.globalVarDeclaration;
import IR.Node.Program;

public interface Pass {
    void visitProgram(Program prog);
    void visitBlock(block blk);
    void visitFuncDef(funcDef func);
    void visitClassDef(classDef cls);
    void visitGlobalVar(globalVarDeclaration decl);
    void visitGlobalString(globalStringConst str);
    void visitDeclare(declare decl);
}