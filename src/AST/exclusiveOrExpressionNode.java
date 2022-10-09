package AST;

import Utility.position;

public class exclusiveOrExpressionNode extends expressionNode{

    public exclusiveOrExpressionNode(position pos) {super(pos);}

    @Override
    public void accept(ASTVisitor visitor) {visitor.visit(this);}
}
