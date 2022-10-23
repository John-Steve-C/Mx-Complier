package AST;

import Utility.Position;

public class logicAndExpressionNode extends expressionNode{

    public logicAndExpressionNode(Position pos) {super(pos);}

    @Override
    public void accept(ASTVisitor visitor) {visitor.visit(this);}
}
