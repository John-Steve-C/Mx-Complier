package AST;

import Utility.position;

public class idExpressionNode extends expressionNode{

    public String content = null;

    public idExpressionNode(position pos) {super(pos);}

    @Override
    public void accept(ASTVisitor visitor) {visitor.visit(this);}
}
