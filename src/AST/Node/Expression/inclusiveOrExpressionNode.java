package AST.Node.Expression;

import AST.ASTVisitor;
import Utility.Position;

public class inclusiveOrExpressionNode extends expressionNode{

    public inclusiveOrExpressionNode(Position pos) {super(pos);}

    @Override
    public void accept(ASTVisitor visitor) {visitor.visit(this);}
}
