package AST.Node;

import AST.ASTVisitor;
import AST.Node.Expression.expressionNode;
import Utility.Position;

import java.util.ArrayList;

public class newArrayTypeNode extends arraySpecifierNode {

    public ArrayList<expressionNode> lengths = null;

    public newArrayTypeNode(Position pos) {super(pos);}

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
