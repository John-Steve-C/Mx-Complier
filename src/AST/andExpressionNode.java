package AST;

import Utility.Position;

public class andExpressionNode extends expressionNode{

    public andExpressionNode(Position pos) {super(pos);}

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
