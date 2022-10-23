package AST;

import Utility.Position;

public class iterationStatementNode extends statementNode{

    public boolean isWhile = false, isFor = false;
    public statementNode todoStmt = null, init = null;
    public expressionNode cond = null, stepExpr = null;

    public iterationStatementNode(Position pos) {super(pos);}

    @Override
    public void accept(ASTVisitor visitor) {visitor.visit(this);}
}
