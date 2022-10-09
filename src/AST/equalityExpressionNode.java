package AST;

import Utility.position;

import java.util.ArrayList;

public class equalityExpressionNode extends expressionNode{

    public ArrayList<> opList = null;

    public equalityExpressionNode(position pos) {super(pos);}

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
