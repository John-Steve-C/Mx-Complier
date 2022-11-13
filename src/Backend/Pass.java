package Backend;

import IR.Node.*;
import IR.Node.classDef;
import IR.Node.funcDef;

public interface Pass {
    void visitBlock(block blk);
    void visitProgram(program prog);
    void visitFuncDef(funcDef func);
    void visitClassDef(classDef cls);
    void visitGlobalVar(globalVarDeclaration decl);
    void visitGlobalString(globalStringConst str);
    void visitDeclaration(declaration decl);
}