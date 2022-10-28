package AST;

import Utility.Position;

public class selectionStatementNode extends statementNode{

    public statementNode trueStmt = null, falseStmt = null;
    public expressionNode cond = null;

    public selectionStatementNode(Position pos) {super(pos);}

    @Override
    public void accept(ASTVisitor visitor) {visitor.visit(this);}
}
