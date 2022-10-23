package AST;

import Utility.Position;

public class declaratorNode extends ASTNode{

    public String id = null;
    public assignExpressionNode expr = null;

    public declaratorNode(Position pos) {super(pos);}

    @Override
    public void accept(ASTVisitor visitor) {visitor.visit(this);}
}
