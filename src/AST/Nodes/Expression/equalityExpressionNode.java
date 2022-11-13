package AST.Nodes.Expression;

import AST.ASTVisitor;
import Utility.Position;

import java.util.ArrayList;

public class equalityExpressionNode extends expressionNode{

    public ArrayList<String> opList = null;

    public equalityExpressionNode(Position pos) {super(pos);}

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
