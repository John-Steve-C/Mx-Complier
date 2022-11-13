package AST.Nodes;

import AST.ASTNode;
import AST.ASTVisitor;
import AST.Nodes.Statement.compoundStatementNode;
import Utility.Position;

public class functionDefNode extends ASTNode {

    public arraySpecifierNode retType = null;
    public String funcName = null;
    public functionParameterDefNode funcPar = null;
    public compoundStatementNode compoundStmt = null;
    public boolean isConstructFunc = false;

    public functionDefNode(Position pos) {super(pos);}
    @Override
    public void accept(ASTVisitor visitor) {visitor.visit(this);}
}
