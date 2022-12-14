package AST.Node;

import AST.ASTVisitor;
import Utility.Position;

import java.util.ArrayList;

public class functionParameterDefNode extends ASTNode {

    public ArrayList<arraySpecifierNode> typeList = new ArrayList<>();
    public ArrayList<String> idList = new ArrayList<>();

    public functionParameterDefNode(Position pos) {super(pos);}

    @Override
    public void accept(ASTVisitor visitor) {visitor.visit(this);}
}
