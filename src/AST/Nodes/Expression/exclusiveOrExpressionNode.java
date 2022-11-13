package AST.Nodes.Expression;

import AST.ASTVisitor;
import Utility.Position;

public class exclusiveOrExpressionNode extends expressionNode{

    public exclusiveOrExpressionNode(Position pos) {super(pos);}

    @Override
    public void accept(ASTVisitor visitor) {visitor.visit(this);}
}
