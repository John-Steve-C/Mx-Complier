package Test;

import AST.*;
import Utility.Error.SemanticError;
import Utility.GlobalScope;
import Utility.Position;
import Utility.Scope;
import Utility.Type.ClassType;
import Utility.Type.FuncType;
import Utility.Type.Type;

import java.util.Objects;

public class SemanticChecker implements ASTVisitor {
    public Scope currentScope;
    public ClassType currentStruct = null;  // 用来表示 this 指代的对象
    public GlobalScope globalScope;
    public boolean hasMain = false, hasLambda = false, funcSuite = false;
    public Type returnType = null, lambdaReturnType = null;
    public Position mainPos = null;
    public int inLoop = 0, inLambda = 0;

    public SemanticChecker(GlobalScope gScope) {
        currentScope = globalScope = gScope;
    }

    @Override
    public void visit(RootNode it) {
        it.decList.forEach(decl -> decl.accept(this));
        if (!hasMain) throw new SemanticError(it.pos, "no main function");
    }

    @Override
    public void visit(declarationNode it) {
        if (it.isFuncDef) it.funcDef.accept(this);
        else it.declStmt.accept(this);
    }

    //-------------------------------------- branch 1
    @Override
    public void visit(functionDefNode it) {
        // 进入新的 currentScope (类比链表的赋值)
        currentScope = new Scope(currentScope);

        //check main function
        if (currentStruct == null && Objects.equals(it.funcName, "main")) {
            if (hasMain) throw new SemanticError(it.pos, "main has been defined in " + mainPos.toString());
            if (!Objects.equals(it.retType.type, "int"))
                throw new SemanticError(it.pos, "the return type of main function is " + it.retType.type);
            if (it.funcPar != null) throw new SemanticError(it.pos, "main function has parameters");

            hasMain = true;
            mainPos = it.pos;
        }

        FuncType func;
        if (currentStruct != null) {
            if (currentStruct.method.containsKey(it.funcName)) {
                func = currentStruct.method.get(it.funcName);
                // illegal construct function
                if (func.returnType.kind != Type.Types.NULL && Objects.equals(it.funcName, currentStruct.name))
                    throw new SemanticError(it.pos, "illegal construct function");
            } else
                throw new SemanticError(it.pos, "can't find function " + it.funcName + " in class " + currentStruct.name);
        } else {
            func = globalScope.queryFuncType(it.funcName, it.pos);
        }

        if (it.funcPar != null) {
            for (int i = 0; i < func.parameter.size(); i++) {
                String id = it.funcPar.idList.get(i);
                globalScope.checkNameConflict(id, it.funcPar.pos);
                currentScope.defineVar(id, func.parameter.get(i), it.funcPar.pos);
            }
        }

        // check function suite(嵌套), 未退出上层函数
        if (returnType != null) throw new SemanticError(it.pos, "define function inside function");
        returnType = func.returnType;
        funcSuite = true;
        it.compoundStmt.accept(this);
        // 访问结束，回退到 parent
        returnType = null;
        currentScope = currentScope.parentScope;
    }

    @Override
    public void visit(compoundStatementNode it) {
        if (it.stmtList != null) {
            boolean temp = false;
            if (!funcSuite) currentScope = new Scope(currentScope);
            else {
                funcSuite = false;
                temp = true;
            }

            it.stmtList.forEach(stmt -> stmt.accept(this));
            // If it's a suite, you should step out in the end.
            if (!temp) currentScope = currentScope.parentScope;
        }
    }

    @Override
    public void visit(statementNode it) {}

    @Override
    public void visit(selectionStatementNode it) {
        it.cond.accept(this);
        if (it.cond.isEmpty) throw new SemanticError(it.cond.pos, "condition can't be empty");
        if (it.cond.type.kind != Type.Types.BOOL_TYPE)
            throw new SemanticError(it.cond.pos, "condition's result isn't a bool");

        if (it.trueStmt != null) {
            currentScope = new Scope(currentScope);
            it.trueStmt.accept(this);
            currentScope = currentScope.parentScope;
        }
        if (it.falseStmt != null) {
            currentScope = new Scope(currentScope);
            it.falseStmt.accept(this);
            currentScope = currentScope.parentScope;
        }
    }

    @Override
    public void visit(iterationStatementNode it) {
        currentScope = new Scope(currentScope);
        if (it.isWhile) {
            if (it.cond == null) throw new SemanticError(it.pos, "condition can't be empty");
            it.cond.accept(this);
            if (it.cond.type.kind != Type.Types.BOOL_TYPE)
                throw new SemanticError(it.cond.pos, "condition's result isn't a bool");
        } else {
            if (it.init != null) it.init.accept(this);
            if (it.cond != null) {
                it.cond.accept(this);
                if (it.cond.type.kind != Type.Types.BOOL_TYPE)
                    throw new SemanticError(it.cond.pos, "condition's result isn't a bool");
            }
            if (it.stepExpr != null) it.stepExpr.accept(this);
        }

        inLoop++;
        it.todoStmt.accept(this);
        inLoop--;
        currentScope = currentScope.parentScope;
    }

    @Override
    public void visit(jumpStatementNode it) {
        if (it.isReturn) {
            if (inLambda > 0) {
                it.retExpr.accept(this);
                it;
            }
        } else {
            if (inLoop == 0) throw new SemanticError(it.pos, "jump outside the loop");
        }
    }

    @Override
    public void visit(expressionStatementNode it) {
        it.expr.accept(this);
    }


    //-------------------------------------- branch 2
    @Override
    public void visit(declarationStatementNode it) {
        if (it.isClassDef) it.struct.accept(this);
        else {
            if (it.fail) throw new SemanticError(it.pos, "illegal class definition");
            else {
                Type t = new Type(globalScope.queryType(it.arraySpec.type, it.arraySpec.pos));
                t.dimension = it.arraySpec.emptyBracketPair;
                it.initList.forEach(declarator -> {
                    if (declarator.expr != null) {
                        declarator.expr.accept(this); // 通过 visit expr，已经对变量是否重名进行了判断
                        // 下面主要判断表达式赋值的合法性
                        if (declarator.expr.type.kind == Type.Types.CONST_NULL) {
                            if (t.dimension == 0 && t.kind != Type.Types.CLASS_TYPE)
                                throw new SemanticError(it.pos, "can't assign null to !(class or array)");
                        } else {
                            if (!Objects.equals(declarator.expr.type.name, t.name) ||
                                    declarator.expr.type.dimension != t.dimension )
                                throw new SemanticError(declarator.pos, "expr type doesn't match var type");
                        }
                    }

                    currentScope.defineVar(declarator.id, t, declarator.pos);
                });
            }
        }
    }

    @Override
    public void visit(classSpecifierNode it) {
        currentStruct = (ClassType) globalScope.queryType(it.name, it.pos);
        currentScope = new Scope(currentScope);
        it.declareList.forEach(dec -> dec.accept(this));

        if (it.constructFunc != null) {
            if (!Objects.equals(it.constructFunc.funcName, it.name))
                throw new SemanticError(it.constructFunc.pos, "mismatched construction function");
            it.constructFunc.accept(this);
        }

        // remember to exit this class definition
        currentStruct = null;
        currentScope = currentScope.parentScope;
    }

    @Override
    public void visit(assignExpressionNode it) {
        it.logicExpr.accept(this);
        it.type = it.logicExpr.type;

        if (it.exprList != null) {
            if (!it.type.assignable)
                throw new SemanticError(it.pos, "not assignable");
            expressionNode expr = it.exprList.get(it.exprList.size() - 1);
            // 从右往左赋值
            expr.accept(this);
            if (expr.type.kind != Type.Types.CONST_NULL) {
                if (!Objects.equals(expr.type.name, it.logicExpr.type.name) ||
                        expr.type.dimension != it.type.dimension)
                    throw new SemanticError(expr.pos, "type doesn't match in assignment");
            } else if (it.logicExpr.type.kind != Type.Types.CLASS_TYPE && it.logicExpr.type.dimension == 0)
                throw new SemanticError(it.pos, "can't assign null to !(class or array)");
            // null can only be assigned to class or array
        }
    }

    @Override
    public void visit(logicOrExpressionNode it) {
        boolean first = false;

        for (expressionNode expr : it.exprList) {
            expr.accept(this);
            if (first && (!Objects.equals(expr.type.name, "bool")) || expr.type.dimension > 0)
                throw new SemanticError(expr.pos, "expr type isn't bool in logicOrExpression");
            first = true;
        }

        it.type = it.exprList.get(0).type;
        if (it.exprList.size() > 1) {
            if (it.type.dimension > 0)
                throw new SemanticError(it.pos, "expr type isn't bool in logicOrExpression");
            it.type.assignable = false;
        }
    }

    @Override
    public void visit(logicAndExpressionNode it) {
        boolean first = false;

        for (expressionNode expr : it.exprList) {
            expr.accept(this);
            if (first && (!Objects.equals(expr.type.name, "bool")) || expr.type.dimension > 0)
                throw new SemanticError(expr.pos, "expr type isn't bool in logicAndExpression");
            first = true;
        }

        it.type = it.exprList.get(0).type;
        if (it.exprList.size() > 1) {
            if (it.type.dimension > 0)
                throw new SemanticError(it.pos, "expr type isn't bool in logicAndExpression");
            it.type.assignable = false;
        }
    }

    @Override
    public void visit(inclusiveOrExpressionNode it) {
        expressionNode first = null;
        for (expressionNode expr : it.exprList) {
            expr.accept(this);
            if (first != null && !Objects.equals(first.type.name, expr.type.name) || expr.type.dimension > 0)
                throw new SemanticError(it.pos, "expr type isn't bool in inclusiveOrExpression");
            first = expr;
        }
        
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
    public void visit(postfixExpressionNode it) {

    }

    @Override
    public void visit(primaryExpressionNode it) {
        if (it.isLiteral) {
            it.expr.accept(this);
            it.type = it.expr.type;
        } else if (it.isExpr) {
            it.expr.accept(this);
            it.type = it.expr.type;
            if (it.type.kind == Type.Types.FUNC_TYPE) it.func = (FuncType) it.type;
        } else if (it.isThis) {
            if (currentStruct == null) throw new SemanticError(it.pos, "use THIS outside the class");
            it.type = new Type(currentStruct);
            it.type.assignable = false;
        } else if (it.isIdExpr) {
            String s = ((idExpressionNode) it.expr).content;

            // check the var's id
            if (currentScope.containVar(s, false)) {
                it.type = new Type(currentScope.getType(s, true));
                it.type.assignable = true;
            } else if (currentStruct != null && currentStruct.member.containsKey(s)) {
                it.type = new Type(currentStruct.member.get(s));
                it.type.assignable = true;
            } else if (currentScope.containVar(s, true)) {
                it.type = new Type(currentScope.getType(s, true));
                it.type.assignable = true;
            }

            // check the func's id
            if (currentStruct != null && currentStruct.method.containsKey(s)) {
                it.func = currentStruct.method.get(s);
            } else if (globalScope.funcTypes.containsKey(s)) {
                it.func = globalScope.funcTypes.get(s);
            }

            if (it.func == null && it.type == null)
                throw new SemanticError(it.expr.pos, "can't find the definition of " + s);
        } else {
            // is lambda expr
            it.expr.accept(this);
            it.type = it.expr.type;
            if (it.type.kind == Type.Types.FUNC_TYPE) it.func = (FuncType) it.type;
        }
    }

    @Override
    public void visit(expressionNode it) {
        it.exprList.forEach(expr -> expr.accept(this));
        it.type = it.exprList.get(0).type;
    }

    @Override
    public void visit(idExpressionNode it) {

    }

    @Override
    public void visit(lambdaExpressionNode it) {
        hasLambda = true;
        FuncType lambdaFunc = new FuncType();
    }

    @Override
    public void visit(newExpressionNode it) {

    }

    @Override
    public void visit(newArrayTypeNode it) {}

    @Override
    public void visit(arraySpecifierNode it) {}

    @Override
    public void visit(literalNode it) {}

    @Override
    public void visit(declaratorNode it) {}

    @Override
    public void visit(functionParameterDefNode it) {}

}
