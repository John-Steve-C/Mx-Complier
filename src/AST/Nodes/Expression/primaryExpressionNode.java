package AST.Nodes.Expression;

import AST.ASTVisitor;
import Utility.Position;
import Utility.Type.FuncType;

public class primaryExpressionNode extends expressionNode{

    public expressionNode expr = null;
    public FuncType func = null;
    public boolean isThis = false, isLambda = false, isLiteral = false, isExpr = false, isIdExpr = false;

    public primaryExpressionNode(Position pos) {super(pos);}
    @Override
    public void accept(ASTVisitor visitor) {visitor.visit(this);}
}
