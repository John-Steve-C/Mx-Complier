package AST;

import Utility.Position;

import java.util.ArrayList;

public class additiveExpressionNode extends expressionNode{
    public ArrayList<String> opList = null;

    public additiveExpressionNode(Position pos) {super(pos);}

    public void accept(ASTVisitor visitor) {visitor.visit(this);}
}
