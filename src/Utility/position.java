package Utility;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.TerminalNode;

public class position {
    // 记录当前词素在源代码中对应的 行数和列数
    public int row, column;

    public position(int row, int column) {
        this.row = row;
        this.column = column;
    }

    public position(Token token) {
        this.row = token.getLine();
        this.column = token.getCharPositionInLine();
    }

    public position(TerminalNode node) {
        this(node.getSymbol());
    }

    public position(ParserRuleContext ctx) {
        this(ctx.getStart());
    }

    public String toString() {
        return row + "," + column;
    }
}
