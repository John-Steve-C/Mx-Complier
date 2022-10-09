package AST;

import Utility.position;
public class primaryExpressionNode extends expressionNode{

    public expressionNode expr = null;
    public boolean isThis = false, isLambda = false, isLiteral = false, isExpr = false, isIdExpr = false;

    public primaryExpressionNode(position pos) {super(pos);}
    @Override
    public void accept(ASTVisitor visitor) {visitor.visit(this);}
}
