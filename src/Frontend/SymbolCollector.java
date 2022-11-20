package Frontend;

import AST.*;
import AST.Node.*;
import AST.Node.Expression.*;
import AST.Node.Statement.*;
import Utility.GlobalScope;
import Utility.Type.*;
import Utility.Error.SemanticError;

import java.util.ArrayList;

public class SymbolCollector implements ASTVisitor {
    private GlobalScope globalScope;
    private ClassType currentStruct = null;
    private Type intType = new Type(Type.Types.INT_TYPE),
            boolType = new Type(Type.Types.BOOL_TYPE),
            voidType = new Type(Type.Types.VOID_TYPE);
    private ClassType stringType = new ClassType(Type.Types.CLASS_TYPE);
    private FuncType printFunc = new FuncType(Type.Types.FUNC_TYPE),
            printlnFunc = new FuncType(Type.Types.FUNC_TYPE),
            printIntFunc = new FuncType(Type.Types.FUNC_TYPE),
            getStringFunc = new FuncType(Type.Types.FUNC_TYPE),
            getIntFunc = new FuncType(Type.Types.FUNC_TYPE),
            toStringFunc = new FuncType(Type.Types.FUNC_TYPE),
            printlnIntFunc = new FuncType(Type.Types.FUNC_TYPE);

    public SymbolCollector(GlobalScope gScope) {
        globalScope = gScope;
    }

    @Override
    public void visit(RootNode it) {

        //build in methods of string
        FuncType lengthFunc = new FuncType(Type.Types.FUNC_TYPE),
                 substringFunc = new FuncType(Type.Types.FUNC_TYPE),
                 parseIntFunc = new FuncType(Type.Types.FUNC_TYPE),
                 ordFunc = new FuncType(Type.Types.FUNC_TYPE);
        lengthFunc.name = "length";
        lengthFunc.returnType = intType;

        substringFunc.name = "substring";
        substringFunc.returnType = stringType;
        substringFunc.parameter = new ArrayList<>();
        substringFunc.parameter.add(intType);
        substringFunc.parameter.add(intType);

        parseIntFunc.name = "parseInt";
        parseIntFunc.returnType = intType;

        ordFunc.name = "ord";
        ordFunc.returnType = intType;
        ordFunc.parameter = new ArrayList<>();
        ordFunc.parameter.add(intType);

        //add build in methods into string
        stringType.method.put("length", lengthFunc);
        stringType.method.put("substring", substringFunc);
        stringType.method.put("parseInt", parseIntFunc);
        stringType.method.put("ord", ordFunc);
        stringType.name = "string";

        //add build in types into globalScope
        globalScope.addVarType(intType, "int", it.pos);
        globalScope.addVarType(boolType, "bool", it.pos);
        globalScope.addVarType(stringType, "string", it.pos);
        globalScope.addVarType(voidType, "void", it.pos);
        //build in functions
        printFunc.name = "print";
        printFunc.returnType = voidType;
        printFunc.parameter = new ArrayList<>();
        printFunc.parameter.add(stringType);

        printlnFunc.name = "println";
        printlnFunc.returnType = voidType;
        printlnFunc.parameter = new ArrayList<>();
        printlnFunc.parameter.add(stringType);

        printIntFunc.name = "printInt";
        printIntFunc.returnType = voidType;
        printIntFunc.parameter = new ArrayList<>();
        printIntFunc.parameter.add(intType);

        printlnIntFunc.name = "printlnInt";
        printlnIntFunc.returnType = voidType;
        printlnIntFunc.parameter = new ArrayList<>();
        printlnIntFunc.parameter.add(intType);

        getStringFunc.name = "getString";
        getStringFunc.returnType = stringType;

        getIntFunc.name = "getInt";
        getIntFunc.returnType = intType;

        toStringFunc.name = "toString";
        toStringFunc.returnType = stringType;
        toStringFunc.parameter = new ArrayList<>();
        toStringFunc.parameter.add(intType);

        //add build in functions into globalScope
        globalScope.addFuncType(printFunc, "print", it.pos);
        globalScope.addFuncType(printlnFunc, "println", it.pos);
        globalScope.addFuncType(printIntFunc, "printInt", it.pos);
        globalScope.addFuncType(printlnIntFunc, "printlnInt", it.pos);
        globalScope.addFuncType(getStringFunc, "getString", it.pos);
        globalScope.addFuncType(getIntFunc, "getInt", it.pos);
        globalScope.addFuncType(toStringFunc, "toString", it.pos);
        it.declList.forEach(decl -> {
            if (decl.isDeclareStmt) {
                declarationStatementNode declStmt = (declarationStatementNode) decl.declStmt;
                if (declStmt.isClassDef) {
                    // the real use of symbol collector
                    // collect the class definition all over the program,
                    // and add them to global scope
                    classSpecifierNode c = (classSpecifierNode) declStmt.struct;
                    ClassType struct = new ClassType(Type.Types.CLASS_TYPE);
                    struct.name = c.name;   // remember to check the name!
                    globalScope.addVarType(struct, c.name, it.pos);
                }
            }
        });
        it.declList.forEach(decl -> decl.accept(this));
    }

    @Override
    public void visit(declarationNode it) {
        if (it.isFuncDef) it.funcDef.accept(this);
        else it.declStmt.accept(this);
    }

    @Override
    public void visit(declarationStatementNode it) {
        if (it.isClassDef) it.struct.accept(this);
        else {
            if (it.fail) throw new SemanticError(it.pos, "declarator statement error");
            Type t = new Type(globalScope.queryType(it.arraySpec.type, it.arraySpec.pos));
            t.dimension = it.arraySpec.emptyBracketPair;
            it.initList.forEach(declarator -> {
                globalScope.checkNameConflict(declarator.id, declarator.pos);
                // add to class as member
                if (currentStruct != null) {
                    if (currentStruct.member.containsKey(declarator.id))
                        throw new SemanticError(declarator.pos, "redefinition of member " + declarator.id);
                    currentStruct.member.put(declarator.id, t);
                }
            });
        }
    }

    @Override
    public void visit(classSpecifierNode it) {
        currentStruct = (ClassType) globalScope.queryType(it.name, it.pos);
        it.declList.forEach(decl -> decl.accept(this));
        if (it.constructFunc != null) it.constructFunc.accept(this);
        currentStruct = null;
    }

    @Override
    public void visit(functionDefNode it) {
        FuncType func = new FuncType(Type.Types.FUNC_TYPE);
        func.name = it.funcName;
        //do not check function overload, which is undefined behavior in Mx
        if (it.isConstructFunc) {
            if (currentStruct == null) throw new SemanticError(it.pos, "construct function outside the class");
            func.returnType = new Type(Type.Types.NULL);
        } else {
            func.returnType = new Type(globalScope.queryType(it.retType.type, it.retType.pos));
            func.returnType.dimension = it.retType.emptyBracketPair;
        }
        if (it.funcPar != null) {
            func.parameter = new ArrayList<>();
            it.funcPar.typeList.forEach(var -> {
                Type t = new Type(globalScope.queryType(var.type, var.pos));
                t.dimension = var.emptyBracketPair;
                func.parameter.add(t);
            });
        }
        if (currentStruct != null) {
            if (currentStruct.method.containsKey(it.funcName)) throw new SemanticError(it.pos, "redefinition of method " + it.funcName);
            currentStruct.method.put(it.funcName, func);
        } else globalScope.addFuncType(func, it.funcName, it.pos);
    }

    @Override
    public void visit(arraySpecifierNode it) {}

    @Override
    public void visit(additiveExpressionNode it) {}

    @Override
    public void visit(andExpressionNode it) {}

    @Override
    public void visit(assignExpressionNode it) {}

    @Override
    public void visit(compoundStatementNode it) {}

    @Override
    public void visit(literalNode it) {}

    @Override
    public void visit(declaratorNode it) {}

    @Override
    public void visit(functionParameterDefNode it) {}

    @Override
    public void visit(equalityExpressionNode it) {}

    @Override
    public void visit(exclusiveOrExpressionNode it) {}

    @Override
    public void visit(expressionNode it) {}

    @Override
    public void visit(expressionStatementNode it) {}

    @Override
    public void visit(idExpressionNode it) {}

    @Override
    public void visit(inclusiveOrExpressionNode it) {}

    @Override
    public void visit(iterationStatementNode it) {}

    @Override
    public void visit(jumpStatementNode it) {}

    @Override
    public void visit(lambdaExpressionNode it) {}

    @Override
    public void visit(logicAndExpressionNode it) {}

    @Override
    public void visit(logicOrExpressionNode it) {}

    @Override
    public void visit(multiplicativeExpressionNode it) {}

    @Override
    public void visit(newExpressionNode it) {}

    @Override
    public void visit(postfixExpressionNode it) {}

    @Override
    public void visit(primaryExpressionNode it) {}

    @Override
    public void visit(relationalExpressionNode it) {}

    @Override
    public void visit(selectionStatementNode it) {}

    @Override
    public void visit(shiftExpressionNode it) {}

    @Override
    public void visit(statementNode it) {}

    @Override
    public void visit(unaryExpressionNode it) {}

    @Override
    public void visit(newArrayTypeNode it) {}
}
