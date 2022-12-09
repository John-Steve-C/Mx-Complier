package AST.Node;

import AST.ASTVisitor;
import AST.Node.Statement.declarationStatementNode;
import Utility.Position;

public class declarationNode extends ASTNode {

    public boolean isDeclareStmt = false, isFuncDef = false;
    public functionDefNode funcDef = null;
    public declarationStatementNode declStmt = null;

    public declarationNode(Position pos) {
        super(pos);
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
