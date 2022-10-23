package AST;

import Utility.Position;

public class expressionStatementNode extends statementNode{

    public expressionNode expr = null;

    public expressionStatementNode(Position pos) {super(pos);}

    @Override
    public void accept(ASTVisitor visitor) {visitor.visit(this);}

}
