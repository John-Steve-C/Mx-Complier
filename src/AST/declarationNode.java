package AST;

import Utility.position;

public class declarationNode extends ASTNode{

    public String type = null;

    public declarationNode(position pos) {
        super(pos);
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
