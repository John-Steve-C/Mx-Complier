package AST.Node.Expression;

import AST.ASTVisitor;
import Utility.Position;

public class assignExpressionNode extends expressionNode {

    public logicOrExpressionNode logicExpr = null;

    public assignExpressionNode(Position pos) {super(pos);}

    @Override
    public void accept(ASTVisitor visitor) {visitor.visit(this);}
}
