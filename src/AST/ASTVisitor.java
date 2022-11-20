package AST;

import AST.Node.*;
import AST.Node.Expression.*;
import AST.Node.Statement.*;

// 接口类型，实现多重继承
// 类似 rust 的 trait
// 为遍历 AST 提供接口
public interface ASTVisitor {
    void visit(RootNode it);

    void visit(declarationNode it);
    // branch 1
    void visit(functionDefNode it);
    void visit(compoundStatementNode it);
    void visit(statementNode it);
    void visit(selectionStatementNode it);
    void visit(iterationStatementNode it);
    void visit(jumpStatementNode it);
    void visit(expressionStatementNode it);
    // branch 2
    void visit(declarationStatementNode it);
    void visit(classSpecifierNode it);
    void visit(assignExpressionNode it);
    void visit(logicOrExpressionNode it);
    void visit(logicAndExpressionNode it);
    void visit(inclusiveOrExpressionNode it);
    void visit(exclusiveOrExpressionNode it);
    void visit(andExpressionNode it);
    void visit(equalityExpressionNode it);
    void visit(relationalExpressionNode it);
    void visit(shiftExpressionNode it);
    void visit(additiveExpressionNode it);
    void visit(multiplicativeExpressionNode it);
    void visit(unaryExpressionNode it);
    void visit(newExpressionNode it);
    void visit(postfixExpressionNode it);
    void visit(primaryExpressionNode it);
    void visit(expressionNode it);
    void visit(idExpressionNode it);
    void visit(lambdaExpressionNode it);
    void visit(newArrayTypeNode it);
    void visit(arraySpecifierNode it);
    void visit(literalNode it);
    void visit(declaratorNode it);
    void visit(functionParameterDefNode it);
}
