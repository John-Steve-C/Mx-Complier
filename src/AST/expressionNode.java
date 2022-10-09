package AST;

import Utility.position;
import java.util.ArrayList;

// 作为多种 expressionNode 的基类
public class expressionNode extends ASTNode{

    public ArrayList<expressionNode> exprList = null;
    public boolean isConst = false;

    public expressionNode(position pos) {
        super(pos);
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
