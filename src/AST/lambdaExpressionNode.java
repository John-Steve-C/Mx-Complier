package AST;

import Utility.Position;
public class lambdaExpressionNode extends expressionNode{

    public functionParameterDefNode funcPar = null;
    public compoundStatementNode compoundStmt = null;

    public lambdaExpressionNode(Position pos) {super(pos);}

    @Override
    public void accept(ASTVisitor visitor) {visitor.visit(this);}
}
