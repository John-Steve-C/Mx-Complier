package AST.Node.Statement;

import AST.ASTVisitor;
import AST.Node.ASTNode;
import Utility.Position;
import Utility.Type.Type;

public class statementNode extends ASTNode {

    public boolean isEmpty = false;
    public Type type = null;

    public statementNode(Position pos) {super(pos);}

    @Override
    public void accept(ASTVisitor visitor) {visitor.visit(this);}
}
