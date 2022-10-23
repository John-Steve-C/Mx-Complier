package AST;

import Utility.Position;

public class logicOrExpressionNode extends expressionNode{

    public logicOrExpressionNode(Position pos) {super(pos);}

    @Override
    public void accept(ASTVisitor visitor) {visitor.visit(this);}
}
