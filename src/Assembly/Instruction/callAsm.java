package Assembly.Instruction;

import java.util.LinkedList;
import Assembly.Operand.*;
public class callAsm extends AsmInst {
    public String funcName;
    public LinkedList<reg> parameters = new LinkedList<>();

    public callAsm(String name) {
        this.funcName = name;
    }

    @Override
    public String toString() {
        return "call " + funcName;
    }
}
