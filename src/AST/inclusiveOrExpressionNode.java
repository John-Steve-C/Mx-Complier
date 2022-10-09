package AST;

import Utility.position;

public class inclusiveOrExpressionNode extends expressionNode{

    public inclusiveOrExpressionNode(position pos) {super(pos);}

    @Override
    public void accept(ASTVisitor visitor) {visitor.visit(this);}
}
