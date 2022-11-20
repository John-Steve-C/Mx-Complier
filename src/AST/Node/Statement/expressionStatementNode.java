package AST.Node.Statement;

import AST.ASTVisitor;
import AST.Node.Expression.expressionNode;
import Utility.Position;

public class expressionStatementNode extends statementNode {

    public expressionNode expr = null;

    public expressionStatementNode(Position pos) {super(pos);}

    @Override
    public void accept(ASTVisitor visitor) {visitor.visit(this);}

}
