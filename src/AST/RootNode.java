package AST;

import Utility.Position;

import java.util.ArrayList;

public class RootNode extends ASTNode{
    public ArrayList<declarationNode> decList = null;

    public RootNode(Position pos) {
        super(pos);
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
