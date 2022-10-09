package AST;

// 接口类型，实现多重继承
// 类似 rust 的 trait
public interface ASTVisitor {
    void visit(RootNode it);

    void visit(addictiveExprNode it);
    void visit(andExpressionNode it);
    void visit(arraySpecifierNode it);
    void visit(assignExpressionNode it);
    void visit(classNode it);
    void visit(compoundStmtNode it);
    void visit(literalNode it);
    void visit(declarationNode it);
    void visit(declStmtNode it);
    void visit(declaratorNode it);
    void visit(functionParameterDefNode it);
    void visit(equalityExpressionNode it);
    void visit(exclusiveOrExpressionNode it);
    void visit(expressionNode it);
    void visit(exprStmtNode it);
    void visit(idExpressionNode it);
    void visit(inclusiveOrExpressionNode it);
    void visit(iterStmtNode it);
    void visit(jumpStmtNode it);
    void visit(lambdaExpressionNode it);
    void visit(logicAndExpressionNode it);
    void visit(logicOrExpressionNode it);
    void visit(multiExprNode it);
    void visit(newExprNode it);
    void visit(newArrayNode it);
    void visit(postfixExprNode it);
    void visit(primaryExpressionNode it);
    void visit(relationExprNode it);
    void visit(selectStmtNode it);
    void visit(shiftExprNode it);
    void visit(stmtNode it);
    void visit(unaryExprNode it);
    void visit(functionDefNode it);
}
