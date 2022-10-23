package AST;

import Utility.Position;

public class unaryExpressionNode extends expressionNode{

    public postfixExpressionNode postfixExpr = null;
    public newExpressionNode newExpr = null;
    public unaryExpressionNode unaryExpr = null;
    public String op = null;
    public unaryExpressionNode(Position pos) {super(pos);}

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
