package AST;

import Utility.Position;
import Utility.Type.FuncType;

public class postfixExpressionNode extends expressionNode{

    public boolean isBracket = false, isParen = false, isDot = false, isPlusPlus = false, isMinusMinus = false;
    public primaryExpressionNode primaryExpr = null;
    public postfixExpressionNode postfixExpr = null;
    public expressionNode expr = null;
    public FuncType func = null;

    public postfixExpressionNode(Position pos) {super(pos);}

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
