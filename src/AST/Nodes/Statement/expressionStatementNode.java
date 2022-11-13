package AST.Nodes.Statement;

import AST.ASTVisitor;
import AST.Nodes.Expression.expressionNode;
import AST.Nodes.Statement.statementNode;
import Utility.Position;

public class expressionStatementNode extends statementNode {

    public expressionNode expr = null;

    public expressionStatementNode(Position pos) {super(pos);}

    @Override
    public void accept(ASTVisitor visitor) {visitor.visit(this);}

}
