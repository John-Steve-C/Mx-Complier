package AST.Node.Statement;

import AST.ASTVisitor;
import AST.Node.Expression.expressionNode;
import Utility.Position;

public class jumpStatementNode extends statementNode{

    public boolean isReturn = false, isBreak = false, isContinue = false;
    public expressionNode retExpr = null;

    public jumpStatementNode(Position pos) {super(pos);}

    @Override
    public void accept(ASTVisitor visitor) {visitor.visit(this);}
}
