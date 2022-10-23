package AST;

import Utility.Position;
import Utility.Type.Type;

import java.util.ArrayList;

// 作为多种 expressionNode 的基类
public class expressionNode extends ASTNode{

    public ArrayList<expressionNode> exprList = null;
    public Type type = null;    // 表示 表达式运算结果 的类型
    public boolean isConst = false;

    public expressionNode(Position pos) {
        super(pos);
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
