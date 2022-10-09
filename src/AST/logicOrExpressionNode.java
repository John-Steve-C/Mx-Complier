package AST;

import Utility.position;

import java.util.ArrayList;

public class logicOrExpressionNode extends expressionNode{

    public logicOrExpressionNode(position pos) {super(pos);}

    @Override
    public void accept(ASTVisitor visitor) {visitor.visit(this);}
}
