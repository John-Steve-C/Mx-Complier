package AST;

import Utility.Position;

public class selectionStatementNode extends statementNode{

    public statementNode cond = null, trueStmt = null, falseStmt = null;

    public selectionStatementNode(Position pos) {super(pos);}

    @Override
    public void accept(ASTVisitor visitor) {visitor.visit(this);}
}
