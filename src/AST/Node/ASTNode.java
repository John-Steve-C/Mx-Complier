package AST.Node;

import AST.ASTVisitor;
import Utility.Position;

public abstract class ASTNode {
    public Position pos;

    public ASTNode(Position pos) {
        this.pos = pos;
    }
    abstract public void accept(ASTVisitor visitor);
}
