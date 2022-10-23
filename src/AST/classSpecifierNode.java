package AST;

import Utility.Position;

import java.util.ArrayList;

public class classSpecifierNode extends ASTNode{

    public String name = null;
    public ArrayList<declarationNode> declareList = null;
    public functionDefNode constructFunc = null;

    public classSpecifierNode(Position pos) {
        super(pos);
    }

    @Override
    public void accept(ASTVisitor visitor) {visitor.visit(this);}
}
