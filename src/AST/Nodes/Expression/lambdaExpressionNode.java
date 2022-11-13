package AST.Nodes.Expression;

import AST.ASTVisitor;
import AST.Nodes.Statement.compoundStatementNode;
import AST.Nodes.functionParameterDefNode;
import Utility.Position;
public class lambdaExpressionNode extends expressionNode{

    public functionParameterDefNode funcPar = null;
    public compoundStatementNode compoundStmt = null;
    public boolean is_global = false;

    public lambdaExpressionNode(Position pos) {super(pos);}

    @Override
    public void accept(ASTVisitor visitor) {visitor.visit(this);}
}
