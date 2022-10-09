package AST;

import Utility.position;
import java.util.ArrayList;

public class functionParameterDefNode extends ASTNode{

    public ArrayList<arraySpecifierNode> typeList = new ArrayList<>();
    public ArrayList<String> idList = new ArrayList<>();

    public functionParameterDefNode(position pos) {super(pos);}

    @Override
    public void accept(ASTVisitor visitor) {visitor.visit(this);}
}
