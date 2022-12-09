package AST.Node;

import AST.ASTVisitor;
import Utility.Position;

public class arraySpecifierNode extends ASTNode {

    public String type = null;
    public int emptyBracketPair = 0;
    public arraySpecifierNode(Position pos) {
        super(pos);
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
