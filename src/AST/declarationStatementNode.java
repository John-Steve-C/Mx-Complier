package AST;

import Utility.Position;

import java.util.ArrayList;

public class declarationStatementNode extends statementNode{

    public ArrayList<declaratorNode> initList = null;
    public arraySpecifierNode arraySpec = null; // varType为 array/buildin
    public classSpecifierNode struct = null; //varType 为class定义时，对应的结点
    public boolean isClassDef = false, fail = false;
    // fail表示，此结点为class定义，且定义不符合规范

    public declarationStatementNode(Position pos) {super(pos);}

    @Override
    public void accept(ASTVisitor visitor) {visitor.visit(this);}
}
