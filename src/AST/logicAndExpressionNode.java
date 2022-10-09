package AST;

import Utility.position;

public class logicAndExpressionNode extends expressionNode{

    public logicAndExpressionNode(position pos) {super(pos);}

    @Override
    public void accept(ASTVisitor visitor) {visitor.visit(this);}
}
