package AST;

import Utility.Position;

import java.util.ArrayList;

public class compoundStatementNode extends statementNode{

    public ArrayList<statementNode> stmtList = null;

    public compoundStatementNode(Position pos) {super(pos);}

    @Override
    public void accept(ASTVisitor visitor) {visitor.visit(this);}
}
