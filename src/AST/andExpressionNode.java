package AST;

import Utility.position;

public class andExpressionNode extends expressionNode{

    public andExpressionNode(position pos) {super(pos);}

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
