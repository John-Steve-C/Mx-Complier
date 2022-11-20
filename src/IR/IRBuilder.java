package IR;

import AST.*;
import AST.Node.*;
import AST.Node.Expression.*;
import AST.Node.Statement.*;
import IR.Node.*;
import Utility.GlobalScope;
import Utility.Scope;
import Utility.Type.*;

import java.util.HashMap;
import java.util.Objects;

public class IRBuilder implements ASTVisitor {
    // build IR from AST
    // 部分变量与 symbolCollector 类似
    public program prog;
    public Scope currentScope;
    public GlobalScope globalScope;
    public ClassType currentStruct = null;
    public block currentBlock = null;

    public HashMap<String, classDef> idToClsDef;
    public HashMap<String, funcDef> idToFuncDef;

    public int initFuncNum = 0;
    public funcDef mainFunc = null, currentFunc = null;

    public IRBuilder(program p, GlobalScope gscope) {
        prog = p;
        currentScope = globalScope = gscope;

    }

    public int stringToInt(String str) {
        int ret = 0;
        for (int i = 0; i < str.length(); i++) {
            ret = ret * 10 + str.charAt(i) - 'a';
        }
        return ret;
    }

    @Override
    public void visit(RootNode it) {
        it.declList.forEach(dec -> dec.accept(this));
        if (initFuncNum > 0) {
            
        }
        prog.mainFunc = mainFunc;
    }

    @Override
    public void visit(declarationNode it) {
        if (it.isDeclareStmt) it.declStmt.accept(this);
        else it.funcDef.accept(this);
    }

    @Override
    public void visit(functionDefNode it) {

    }

    @Override
    public void visit(compoundStatementNode it) {

    }

    @Override
    public void visit(statementNode it) {}

    @Override
    public void visit(selectionStatementNode it) {

    }

    @Override
    public void visit(iterationStatementNode it) {

    }

    @Override
    public void visit(jumpStatementNode it) {

    }

    @Override
    public void visit(expressionStatementNode it) {
        it.expr.accept(this);
    }

    @Override
    public void visit(declarationStatementNode it) {
        if (it.isClassDef) it.struct.accept(this);
        else {
            Type t = new Type(globalScope.queryType(it.arraySpec.type, it.arraySpec.pos));
            t.dimension = it.arraySpec.emptyBracketPair;

            IRType irType;
            if (t.kind == Type.Types.CLASS_TYPE) {
                irType = new IRType(t.dimension + 1, 0);
                irType.clsDef = idToClsDef.get(t.name);
                if (Objects.equals(t.name, "string")) irType.isString = true;
            } else {
                irType = new IRType(t);
            }

            for (declaratorNode node : it.initList) {

                if (currentFunc != null || currentStruct == null) {
                    register rd = new register();
                    if (currentFunc != null) {

                    } else {

                    }
                } else {
                    globalVarDeclaration d = new globalVarDeclaration(irType, );
                    prog.add(d);
                }
            }

        }
    }

    @Override
    public void visit(classSpecifierNode it) {
        currentStruct = (ClassType) globalScope.queryType(it.name, it.pos);
        currentScope = new Scope(currentScope);

        prog.add(idToClsDef.get(it.name));
        it.declList.forEach(decl -> decl.accept(this));
        if (it.constructFunc != null) it.constructFunc.accept(this);

        currentStruct = null;
        currentScope = currentScope.parentScope;
    }

    @Override
    public void visit(assignExpressionNode it) {

    }

    @Override
    public void visit(logicOrExpressionNode it) {

    }

    @Override
    public void visit(logicAndExpressionNode it) {

    }

    @Override
    public void visit(inclusiveOrExpressionNode it) {

    }

    @Override
    public void visit(exclusiveOrExpressionNode it) {

    }

    @Override
    public void visit(andExpressionNode it) {

    }

    @Override
    public void visit(equalityExpressionNode it) {

    }

    @Override
    public void visit(relationalExpressionNode it) {

    }

    @Override
    public void visit(shiftExpressionNode it) {

    }

    @Override
    public void visit(additiveExpressionNode it) {

    }

    @Override
    public void visit(multiplicativeExpressionNode it) {

    }

    @Override
    public void visit(unaryExpressionNode it) {

    }

    @Override
    public void visit(newExpressionNode it) {

    }

    @Override
    public void visit(postfixExpressionNode it) {

    }

    @Override
    public void visit(primaryExpressionNode it) {
        if (it.isThis) {
            register reg = currentScope.getEntity("this", true);

            it.expr.accept(this);
        } else {
            it.expr.accept(this);
            it.reg = it.expr.reg;
        }
    }

    @Override
    public void visit(expressionNode it) {
        it.exprList.forEach(expr -> expr.accept(this));
        expressionNode first = it.exprList.get(0);
        it.type = first.type;
        it.reg = first.reg;
    }

    @Override
    public void visit(idExpressionNode it) {

    }

    @Override
    public void visit(lambdaExpressionNode it) {

    }

    @Override
    public void visit(newArrayTypeNode it) {

    }

    @Override
    public void visit(arraySpecifierNode it) {

    }

    @Override
    public void visit(literalNode it) {
//        it.accept(this);
        it.reg = new register();
        if (it.isBool) {
            it.reg.saveType = new IRType(8);
            it.reg.value = new literal(it.content.equals("true"));
        }
        else if (it.isInt) {
            it.reg.saveType = new IRType(32);
            it.reg.value = new literal(stringToInt(it.content));
        }
        else if (it.isString) {
            it.reg.value = new literal(it.content);
            IRType irType = new IRType(0, it.content.length());
            irType.intLen = 8;
            irType.isString = true;
            it.reg.saveType = irType.getPtr();
            // todo: 需要对转义字符的长度进行特判吗？
            // todo 2:如何实现字符串的标识符？
            // it.reg.name = ?
            prog.add(new globalStringConst(it.content, it.content.length(), it.reg, it.reg.saveType));
        }
        else {
            it.reg.saveType = new IRType(0);
            it.reg.value = new literal();
        }
    }

    @Override
    public void visit(declaratorNode it) {

    }

    @Override
    public void visit(functionParameterDefNode it) {

    }
}
