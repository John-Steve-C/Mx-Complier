package AST;

import Utility.Position;

public class idExpressionNode extends expressionNode{

    public String content = null;

    public idExpressionNode(Position pos) {super(pos);}

    @Override
    public void accept(ASTVisitor visitor) {visitor.visit(this);}
}
