package AST;

import Utility.position;
public class assignExpressionNode extends expressionNode{

    public logicOrExpressionNode logicExpr = null;

    public assignExpressionNode(position pos) {super(pos);}

    @Override
    public void accept(ASTVisitor visitor) {visitor.visit(this);}
}
