package IR;

import AST.*;
import AST.Nodes.*;
import AST.Nodes.Expression.*;
import AST.Nodes.Statement.*;
import IR.Node.block;
import IR.Node.program;
import Utility.GlobalScope;
import Utility.Scope;
import Utility.Type.ClassType;

public class IRBuilder implements ASTVisitor {
    // build IR from AST
    // 部分变量与 symbolCollector 类似
    public program prog;
    public Scope currentScope;
    public GlobalScope globalScope;
    public ClassType currentStruct = null;
    public block currentBlock = null;


    @Override
    public void visit(RootNode it) {
        it.declList.forEach(dec -> dec.accept(this));

    }

    @Override
    public void visit(declarationNode it) {

    }

    @Override
    public void visit(functionDefNode it) {

    }

    @Override
    public void visit(compoundStatementNode it) {

    }

    @Override
    public void visit(statementNode it) {

    }

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

    }

    @Override
    public void visit(declarationStatementNode it) {

    }

    @Override
    public void visit(classSpecifierNode it) {

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

    }

    @Override
    public void visit(expressionNode it) {

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

    }

    @Override
    public void visit(declaratorNode it) {

    }

    @Override
    public void visit(functionParameterDefNode it) {

    }
}
