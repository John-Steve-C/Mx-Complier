package AST.Nodes.Expression;

import AST.ASTVisitor;
import Utility.Position;

import java.util.ArrayList;

public class shiftExpressionNode extends expressionNode{

    public ArrayList<String> opList = null;

    public shiftExpressionNode(Position pos) {super(pos);}

    public void accept(ASTVisitor visitor) {visitor.visit(this);}
}
