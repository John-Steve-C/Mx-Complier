package Test;

import AST.*;
import Utility.Error.SemanticError;
import Utility.GlobalScope;
import Utility.Position;
import Utility.Scope;
import Utility.Type.*;

import java.util.ArrayList;
import java.util.Objects;

public class SemanticChecker implements ASTVisitor {
    public Scope currentScope;
    public ClassType currentStruct = null;  // 用来表示 this 指代的对象
    public GlobalScope globalScope;
    public boolean hasMain = false, hasLambda = false, funcSuite = false;
    public Type returnType = null, lambdaReturnType = null;
    public Position mainPos = null;
    public int inLoop = 0, inLambda = 0;
    public FuncType arraySizeFunc = new FuncType("size", new Type(Type.Types.INT_TYPE));

    public SemanticChecker(GlobalScope gScope) {
        currentScope = globalScope = gScope;
    }

    @Override
    public void visit(RootNode it) {
        it.declList.forEach(decl -> decl.accept(this));
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
        // remember if there's return 0; in main(), it's OK
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
                    // todo: change to (|| not equal)
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
            // 说明是由lambda表达式进入的
            if (!funcSuite) currentScope = new Scope(currentScope);
            else {  // 由函数进入的，已经有新的scope
                funcSuite = false;
                temp = true;
            }

            it.stmtList.forEach(stmt -> stmt.accept(this));
            // 如果不是函数嵌套, 需要手动退出当前空间
            if (!temp) currentScope = currentScope.parentScope;
        }
    }

    @Override
    public void visit(statementNode it) {}

    @Override
    public void visit(selectionStatementNode it) {
        it.cond.accept(this);
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
                if (it.retExpr != null) {
                    it.retExpr.accept(this);
                    if (lambdaReturnType == null) lambdaReturnType = it.retExpr.type;
                    else if (!Objects.equals(lambdaReturnType.name, it.retExpr.type.name))
                        throw new SemanticError(it.pos, "return type in lambdaExpr is different from that outside");
                } else if (lambdaReturnType == null) {
                    lambdaReturnType = new Type(Type.Types.VOID_TYPE);
                } else if (lambdaReturnType.kind != Type.Types.VOID_TYPE) {
                    throw new SemanticError(it.pos, "return type in lambdaExpr is different from that outside");
                }
            }
            else {
                // 普通函数判断
                if (returnType == null) throw new SemanticError(it.pos, "return happened outside the function");
                if (it.retExpr != null) {
                    if (Objects.equals(returnType.name, "void"))
                        throw new SemanticError(it.pos, "can't return value in void");
                    if (Objects.equals(returnType.name, "null"))
                        throw new SemanticError(it.pos, "can't return value in construct function");
                    it.retExpr.accept(this);
                    if (it.retExpr.type.kind == Type.Types.CONST_NULL) {
                        if ((Objects.equals(returnType.name, "int") || Objects.equals(returnType.name, "bool") && returnType.dimension == 0))
                            throw new SemanticError(it.pos, "can't return null to int/bool type");
                    } else if (!Objects.equals(returnType.name, it.retExpr.type.name) || it.retExpr.type.dimension != returnType.dimension)
                        throw new SemanticError(it.pos, "return type mismatch");
                } else {
                    if (!Objects.equals(returnType.name, "void") && !Objects.equals(returnType.name, "null"))
                        throw new SemanticError(it.pos, "return empty in non-void function");
                }
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
        it.declList.forEach(dec -> dec.accept(this));

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
                throw new SemanticError(it.logicExpr.pos, "not assignable");
            expressionNode expr = it.exprList.get(it.exprList.size() - 1);
            // 从右往左赋值
            expr.accept(this);
            if (expr.type.kind != Type.Types.CONST_NULL) {
                if (!Objects.equals(expr.type.name, it.logicExpr.type.name) ||
                        expr.type.dimension != it.type.dimension)
                    throw new SemanticError(expr.pos, "type doesn't match in assignment");
            } else if (it.logicExpr.type.kind != Type.Types.CLASS_TYPE && it.logicExpr.type.dimension == 0
                    || Objects.equals(it.logicExpr.type.name, "string") && it.logicExpr.type.dimension == 0)
                throw new SemanticError(it.pos, "can't assign null to !(class or array)");
            // null can only be assigned to class or array
        }
    }

    @Override
    public void visit(logicOrExpressionNode it) {
        expressionNode before = null;
        // 需要判断合法的运算数类型，|| -> bool
        // 先判断后面的 expr 类型是否与前一个的一致
         for (expressionNode expr : it.exprList) {
            expr.accept(this);
            if (before != null && (!Objects.equals(before.type.name, expr.type.name) || expr.type.dimension > 0))
                throw new SemanticError(it.pos, "expr type mismatch in logicOrExpression");
            before = expr;
        }
        // 再判断第一个 expr 即 Node本身的类型，是否合法
        it.type = it.exprList.get(0).type;
        // 如果 size=1，说明该表达式不存在 || 操作
        if (it.exprList.size() > 1) {
            if (it.type.dimension > 0)
                throw new SemanticError(it.pos, "expr type mismatch in logicOrExpression");
            if (!Objects.equals(it.type.name, "bool"))
                throw new SemanticError(it.pos, "expr type mismatch in logicOrExpression");
            it.type.assignable = false;// 右值不能被赋值
        }
    }

    @Override
    public void visit(logicAndExpressionNode it) {
        expressionNode before = null;
        // && -> bool
        for (expressionNode expr : it.exprList) {
            expr.accept(this);
            if (before != null && (!Objects.equals(before.type.name, expr.type.name) || expr.type.dimension > 0))
                throw new SemanticError(it.pos, "expr type mismatch in logicAndExpression");
            before = expr;
        }
        it.type = it.exprList.get(0).type;
        if (it.exprList.size() > 1) {
            if (it.type.dimension > 0)
                throw new SemanticError(it.pos, "expr type mismatch in logicAndExpression");
            if (!Objects.equals(it.type.name, "bool"))
                throw new SemanticError(it.pos, "expr type mismatch in logicAndExpression");
            it.type.assignable = false;
        }
    }

    @Override
    public void visit(inclusiveOrExpressionNode it) {
        expressionNode before = null;
        // | -> bool/int
        for (expressionNode expr : it.exprList) {
            expr.accept(this);
            if (before != null && (!Objects.equals(before.type.name, expr.type.name) || expr.type.dimension > 0))
                throw new SemanticError(it.pos, "expr type mismatch in inclusiveOrExpression");
            before = expr;
        }
        it.type = it.exprList.get(0).type;
        if (it.exprList.size() > 1) {
            if (it.type.dimension > 0)
                throw new SemanticError(it.pos, "expr type mismatch in inclusiveOrExpression");
            if (!Objects.equals(it.type.name, "int") && !Objects.equals(it.type.name, "bool"))
                throw new SemanticError(it.pos, "expr type mismatch in inclusiveOrExpression");
            it.type.assignable = false;
        }
    }

    @Override
    public void visit(exclusiveOrExpressionNode it) {
        expressionNode before = null;
        // ^ -> bool/int
        for (expressionNode expr : it.exprList) {
            expr.accept(this);
            if (before != null && (!Objects.equals(before.type.name, expr.type.name) || expr.type.dimension > 0))
                throw new SemanticError(it.pos, "expr type mismatch in exclusiveOrExpression");
            before = expr;
        }
        it.type = it.exprList.get(0).type;
        if (it.exprList.size() > 1) {
            if (it.type.dimension > 0)
                throw new SemanticError(it.pos, "expr type mismatch in exclusiveOrExpression");
            if (!Objects.equals(it.type.name, "int") && !Objects.equals(it.type.name, "bool"))
                throw new SemanticError(it.pos, "expr type mismatch in exclusiveOrExpression");
            it.type.assignable = false;
        }
    }

    @Override
    public void visit(andExpressionNode it) {
        expressionNode before = null;
        // & -> bool/int
        for (expressionNode expr : it.exprList) {
            expr.accept(this);
            if (before != null && (!Objects.equals(before.type.name, expr.type.name) || expr.type.dimension > 0))
                throw new SemanticError(it.pos, "expr type mismatch in andExpression");
            before = expr;
        }
        it.type = it.exprList.get(0).type;
        if (it.exprList.size() > 1) {
            if (it.type.dimension > 0)
                throw new SemanticError(it.pos, "expr type mismatch in andExpression");
            if (!Objects.equals(it.type.name, "int") && !Objects.equals(it.type.name, "bool"))
                throw new SemanticError(it.pos, "expr type mismatch in andExpression");
            it.type.assignable = false;
        }
    }

    @Override
    public void visit(equalityExpressionNode it) {
        //==: bool/int/string/class/array
        if (it.exprList.size() > 1) {
            expressionNode lhs = it.exprList.get(0);
            expressionNode rhs = it.exprList.get(1);
            lhs.accept(this);
            rhs.accept(this);
            if (((Objects.equals(lhs.type.name, "int") || Objects.equals(lhs.type.name, "bool")) && lhs.type.dimension == 0)
                || (!Objects.equals(lhs.type.name, "const_null") && !Objects.equals(rhs.type.name, "const_null"))) {
                // 不考虑 null,直接认为是true
                if (!Objects.equals(lhs.type.name, rhs.type.name))
                    throw new SemanticError(lhs.pos, "type mismatch in equalityExpression");
            }
            for (int i = 2; i < it.exprList.size(); i++) {
                expressionNode tmp = it.exprList.get(i);
                tmp.accept(this);
                if (!Objects.equals(tmp.type.name, "bool")) // 只能是多个bool比较
                    throw new SemanticError(tmp.pos, "type mismatch in equalityExpression");
            }
            it.type = new Type(Type.Types.BOOL_TYPE);
        } else {
            it.exprList.get(0).accept(this);
            it.type = it.exprList.get(0).type;
        }
    }

    @Override
    public void visit(relationalExpressionNode it) {
        // > < : int
        if (it.exprList.size() > 2) throw new SemanticError(it.pos, "can't compare more than 2 value");
        else if (it.exprList.size() == 2) {
            expressionNode lhs = it.exprList.get(0);
            expressionNode rhs = it.exprList.get(1);
            lhs.accept(this);
            rhs.accept(this);
            if (Objects.equals(lhs.type.name, "bool") || Objects.equals(rhs.type.name, "bool"))
                throw new SemanticError(lhs.pos, "can't compare 2 bool value");
            it.type = new Type(Type.Types.BOOL_TYPE);
        } else {
            it.exprList.get(0).accept(this);
            it.type = it.exprList.get(0).type;
        }
    }

    @Override
    public void visit(shiftExpressionNode it) {
        expressionNode before = null;
        // >> -> int
        for (expressionNode expr : it.exprList) {
            expr.accept(this);
            if (before != null && (!Objects.equals(before.type.name, expr.type.name) || expr.type.dimension > 0))
                throw new SemanticError(it.pos, "expr type mismatch in shiftExpression");
            before = expr;
        }
        it.type = it.exprList.get(0).type;
        if (it.exprList.size() > 1) {
            if (it.type.dimension > 0)
                throw new SemanticError(it.pos, "expr type mismatch in shiftExpression");
            if (!Objects.equals(it.type.name, "int"))
                throw new SemanticError(it.pos, "expr type mismatch in shiftExpression");
            it.type.assignable = false;
        }
    }

    @Override
    public void visit(additiveExpressionNode it) {
        expressionNode before = null;
        // + -> int/string, - -> int
        for (expressionNode expr : it.exprList) {
            expr.accept(this);
            if (before != null && (!Objects.equals(before.type.name, expr.type.name) || expr.type.dimension > 0))
                throw new SemanticError(it.pos, "expr type mismatch in additiveExpression");
            before = expr;
        }
        it.type = it.exprList.get(0).type;
        if (it.exprList.size() > 1) {
            if (it.type.dimension > 0)
                throw new SemanticError(it.pos, "expr type mismatch in additiveExpression");
            if (Objects.equals(it.type.name, "string")) {
                for (String op : it.opList) {
                    if (!Objects.equals(op, "+")) throw new SemanticError(it.pos, "can't '-' a string");
                }
            } else if (!Objects.equals(it.type.name, "int"))
                throw new SemanticError(it.pos, "expr type mismatch in additiveExpression");
            it.type.assignable = false;
        }
    }

    @Override
    public void visit(multiplicativeExpressionNode it) {
        expressionNode before = null;
        // * or / -> int
        for (expressionNode expr : it.exprList) {
            expr.accept(this);
            if (before != null && (!Objects.equals(before.type.name, expr.type.name) || expr.type.dimension > 0))
                throw new SemanticError(it.pos, "expr type mismatch in multiplicativeExpression");
            before = expr;
        }
        it.type = it.exprList.get(0).type;
        if (it.exprList.size() > 1) {
            if (it.type.dimension > 0)
                throw new SemanticError(it.pos, "expr type mismatch in multiplicativeExpression");
            if (!Objects.equals(it.type.name, "int"))
                throw new SemanticError(it.pos, "expr type mismatch in multiplicativeExpression");
            it.type.assignable = false;
        }
    }

    @Override
    public void visit(unaryExpressionNode it) {
        //g4 中的 ~ 和 * 不会出现
        if (it.newExpr != null) {
            it.newExpr.accept(this);
            it.type = it.newExpr.type;
        } else if (it.postfixExpr != null) {
            it.postfixExpr.accept(this);
            it.type = it.postfixExpr.type;
        } else {
            it.unaryExpr.accept(this);
            if (Objects.equals(it.op, "!")) {
                if (!Objects.equals(it.unaryExpr.type.name, "bool"))
                    throw new SemanticError(it.pos, "can't apply ! to no-bool value");
            } else if (!Objects.equals(it.unaryExpr.type.name, "int")) {
                throw new SemanticError(it.pos, "can't apply ++/-- to no-int value");
            } else if ((Objects.equals(it.op, "++") || Objects.equals(it.op, "--")) && !it.unaryExpr.type.assignable) {
                throw new SemanticError(it.pos, "can't apply ++/-- to rvalue");
            }
            it.type = it.unaryExpr.type;
        }
    }

    @Override
    public void visit(newExpressionNode it) {
        if (it.newArray != null) {
            it.type = new Type(globalScope.queryType(it.newArray.type, it.pos));
            it.type.assignable = true;
            it.type.dimension = it.newArray.emptyBracketPair;
            for (expressionNode expr : it.newArray.lengths) {
                expr.accept(this);
                if (!Objects.equals(expr.type.name, "int"))
                    throw new SemanticError(expr.pos, "subscript is not int");
            }
        } else {
            it.type = new Type(globalScope.queryType(it.typeName, it.pos));
            it.type.assignable = true;
            if (it.type.kind != Type.Types.CLASS_TYPE)
                throw new SemanticError(it.pos, "can't new basic type");
        }
    }

    @Override
    public void visit(postfixExpressionNode it) {
        if (it.isParen) {
            // f(x)
            it.postfixExpr.accept(this);
            if (it.postfixExpr.func == null)
                throw new SemanticError(it.pos, "it's not a function");
            FuncType func = it.postfixExpr.func;
            if (it.expr != null) {
                it.expr.accept(this);
                if (it.expr.exprList.size() != func.parameter.size())
                    throw new SemanticError(it.expr.pos, "function parameter number mismatch");
                for (int i = 0; i < it.expr.exprList.size(); i++) {
                    expressionNode expr = it.expr.exprList.get(i);
                    Type t = func.parameter.get(i);
                    if (t.kind == Type.Types.FUNC_TYPE)
                        throw new SemanticError(expr.pos, "parameter can't be a function");
                    else if (t.kind == Type.Types.CLASS_TYPE || t.dimension > 0) {
                        if (expr.type.kind != Type.Types.CONST_NULL)
                            // 如果参数是class，可以传入null ?
                            if (!Objects.equals(t.name, expr.type.name) || t.dimension != expr.type.dimension)
                                throw new SemanticError(expr.pos, "parameter type class mismatch");
                    } else if (!Objects.equals(t.name, expr.type.name) || t.dimension != expr.type.dimension)
                        throw new SemanticError(expr.pos, "parameter type mismatch");
                }
            } else {
                if (func.parameter != null)
                    throw new SemanticError(it.pos, "the function has no paramater");
            }
            // remember to update the type of it!!
            it.type = new Type(func.returnType);
            it.type.assignable = false;
        } else if (it.isBracket) {
            it.postfixExpr.accept(this);
            it.type = it.postfixExpr.type;
            // 每次进入一层[]，变量的维数-1
            // 注意，应该是先判断维数，再做 -- 操作
            if (it.type.dimension-- == 0)
                throw new SemanticError(it.expr.pos, "subscript can only use in array var");
            it.expr.accept(this);
            if (!Objects.equals(it.expr.type.name, "int") || it.expr.type.dimension != 0)
                throw new SemanticError(it.expr.pos, "subscript is not int");
        } else if (it.isDot) {
            it.postfixExpr.accept(this);
            idExpressionNode id = (idExpressionNode) it.expr;
            Type t = it.postfixExpr.type;
            if (t == null)
                throw new SemanticError(it.pos, "can't apply dot to null");
            if (t.dimension > 0) {
                if (!Objects.equals(id.content, "size"))
                    throw new SemanticError(it.pos, "array don't have this method");
                it.func = arraySizeFunc;
                it.type = null;
                //new Type(Type.Types.INT_TYPE)
            } else if (t.kind == Type.Types.CLASS_TYPE) {
                Type memType = globalScope.queryMemberType(t.name, id.content, id.pos);
                if (memType != null) {
                    it.type = new Type(memType);
                    it.type.assignable = true;
                }
                it.func = globalScope.queryMemberFuncType(t.name, id.content, id.pos);
            } else
                throw new SemanticError(it.pos, "this class don't have this member/method");
        } else if (it.isPlusPlus || it.isMinusMinus) {
            it.postfixExpr.accept(this);
            it.type = it.postfixExpr.type;
            if (it.type == null)
                throw new SemanticError(it.pos, "can't apply ++/-- to null");
            if (!Objects.equals(it.type.name, "int"))
                throw new SemanticError(it.pos, "can't apply ++/-- to non-int");
            if (!it.type.assignable)
                throw new SemanticError(it.pos, "can't modify rvalue");
            it.type.assignable = false;
        } else {
            it.primaryExpr.accept(this);
            it.type = it.primaryExpr.type;
            it.func = it.primaryExpr.func;
        }
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
        if (!currentScope.containVar(it.content, true))
            throw new SemanticError(it.pos, "var isn't defined");
        it.type = currentScope.getType(it.content, true);
    }

    @Override
    public void visit(lambdaExpressionNode it) {
        hasLambda = true;
        FuncType lambdaFunc = new FuncType();
        ++inLambda;
        // 与函数的判断类似

        Scope tempScope = currentScope;
        if (it.is_global) currentScope = new Scope(currentScope);
        else currentScope = new Scope(null);
        // 保证 lambda 函数的 scope 不能访问外部变量

        if (it.funcPar != null) {
            lambdaFunc.parameter = new ArrayList<>();
            it.funcPar.typeList.forEach(var -> {
                Type t = new Type(globalScope.queryType(var.type, var.pos));
                t.dimension = var.emptyBracketPair;
                lambdaFunc.parameter.add(t);
            });
            for (int i = 0; i < it.funcPar.idList.size(); i++) {
                String id = it.funcPar.idList.get(i);
                globalScope.checkNameConflict(id, it.funcPar.pos);
                currentScope.defineVar(id, lambdaFunc.parameter.get(i), it.funcPar.pos);
            }
        }

        Type parentLambdaRet = lambdaReturnType;
        lambdaReturnType = null;
        it.compoundStmt.accept(this);
        lambdaFunc.returnType = Objects.requireNonNullElseGet(lambdaReturnType, () -> new Type(Type.Types.VOID_TYPE));
        // 上式等价于一个三目运算符
        lambdaReturnType = parentLambdaRet;
        if (it.is_global) currentScope = currentScope.parentScope;
        else currentScope = tempScope;
        --inLambda;
        it.type = lambdaFunc;
    }

    @Override
    public void visit(newArrayTypeNode it) {}

    @Override
    public void visit(arraySpecifierNode it) {}

    @Override
    public void visit(literalNode it) {
        if (it.isBool) it.type = new Type(globalScope.queryType("bool", it.pos));
        else if (it.isInt) it.type = new Type(globalScope.queryType("int", it.pos));
        else if (it.isString) it.type = new Type(globalScope.queryType("string", it.pos));
        else if (it.isNull) it.type = new Type(Type.Types.CONST_NULL);
    }

    @Override
    public void visit(declaratorNode it) {}

    @Override
    public void visit(functionParameterDefNode it) {}

}
