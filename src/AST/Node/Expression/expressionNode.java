package AST.Node.Expression;

import AST.ASTVisitor;
import AST.Node.ASTNode;
import Utility.Position;
import Utility.Type.Type;
import IR.TypeSystem.*;

import java.util.ArrayList;

// 作为多种 expressionNode 的基类
public class expressionNode extends ASTNode {

    public ArrayList<expressionNode> exprList = null;
    public Type type = null;    // 表示 表达式运算结果 的类型
//    public boolean isConst = false;

    // used in IR
    public entity rd = null;        // 一般作为 constant，表示实际保存的值
    public register idReg = null;   // 保存 id 的 register
    public IRType irType = null;    // ASTType -> IRType

    public expressionNode(Position pos) {
        super(pos);
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
