package IR.Node.GlobalUnit;

import IR.TypeSystem.register;
import IR.TypeSystem.IRType;

public class globalStringConst extends globalUnit{
    public String content, rawStr;  // content 是 llvm 格式
    public int counter = 0; // 表示是全局中的第几个，输出在 label 中
    public register rd;
    public IRType type;

    public globalStringConst(String content, String rawStr, int counter, register rd, IRType type) {
        this.content = content;
        this.rawStr = rawStr;
        this.counter = counter;
        this.rd = rd;
        this.type = type;
    }
}
