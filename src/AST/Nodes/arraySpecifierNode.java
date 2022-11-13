package AST.Nodes;

import AST.ASTNode;
import AST.ASTVisitor;
import Utility.Position;
import Utility.Type.Type;

public class arraySpecifierNode extends ASTNode {

    public String type = null;
    public int emptyBracketPair = 0;
    public Type typeInfo = null;
    public arraySpecifierNode(Position pos) {
        super(pos);
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
