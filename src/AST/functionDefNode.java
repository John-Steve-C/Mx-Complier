package AST;

import Utility.position;
public class functionDefNode extends ASTNode{

    public String retType = null;

    public functionDefNode(position pos) {super(pos);}
    @Override
    public void accept(ASTVisitor visitor) {visitor.visit(this);}
}
