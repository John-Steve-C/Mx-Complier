package AST;

import java.lang.reflect.Type;
import Utility.position;
public class arraySpecifierNode extends ASTNode {

    public String type = null;
    public int emptyBracketPair = 0;
    public Type typeInfo = null;
    public arraySpecifierNode(position pos) {
        super(pos);
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
