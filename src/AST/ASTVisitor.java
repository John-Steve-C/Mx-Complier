package AST;

// 接口类型，实现多重继承
// 类似 rust 的 trait
// 用于 semantic check 的遍历
public interface ASTVisitor {
    void visit(RootNode it);

    void visit(additiveExpressionNode it);
    void visit(andExpressionNode it);
    void visit(arraySpecifierNode it);
    void visit(assignExpressionNode it);
    void visit(classSpecifierNode it);
    void visit(compoundStatementNode it);
    void visit(literalNode it);
    void visit(declarationNode it);
    void visit(declarationStatementNode it);
    void visit(declaratorNode it);
    void visit(functionDefNode it);
    void visit(functionParameterDefNode it);
    void visit(equalityExpressionNode it);
    void visit(exclusiveOrExpressionNode it);
    void visit(expressionNode it);
    void visit(expressionStatementNode it);
    void visit(idExpressionNode it);
    void visit(inclusiveOrExpressionNode it);
    void visit(iterationStatementNode it);
    void visit(jumpStatementNode it);
    void visit(lambdaExpressionNode it);
    void visit(logicAndExpressionNode it);
    void visit(logicOrExpressionNode it);
    void visit(multiplicativeExpressionNode it);
    void visit(newExpressionNode it);
    void visit(newArrayTypeNode it);
    void visit(postfixExpressionNode it);
    void visit(primaryExpressionNode it);
    void visit(relationalExpressionNode it);
    void visit(selectionStatementNode it);
    void visit(shiftExpressionNode it);
    void visit(statementNode it);
    void visit(unaryExpressionNode it);
}
