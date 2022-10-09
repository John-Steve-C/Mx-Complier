package AST;

import Utility.position;
public class lambdaExpressionNode extends expressionNode{

    public lambdaExpressionNode(position pos) {super(pos);}

    @Override
    public void accept(ASTVisitor visitor) {visitor.visit(this);}
}
