package AST.Node.Expression;

import AST.ASTVisitor;
import Utility.Position;

// 把它看成 constExpression
public class literalNode extends expressionNode {

    public boolean isInt = false, isString = false, isBool = false, isNull = false;
    public String content = null;

    public literalNode(Position pos) {super(pos);}

    @Override
    public void accept(ASTVisitor visitor) {visitor.visit(this);}
}
