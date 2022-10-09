package AST;

import Utility.position;

public class RootNode extends ASTNode{

    public RootNode(position pos) {
        super(pos);
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
