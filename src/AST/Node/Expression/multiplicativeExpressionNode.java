package AST.Node.Expression;

import AST.ASTVisitor;
import Utility.Position;

import java.util.ArrayList;

public class multiplicativeExpressionNode extends expressionNode{

    public ArrayList<String> opList = null;

    public multiplicativeExpressionNode(Position pos) {super(pos);}

    public void accept(ASTVisitor visitor) {visitor.visit(this);}
}
