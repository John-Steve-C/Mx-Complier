package Assembly.Instruction;

import Assembly.AsmBlock;

public class jumpAsm extends AsmInst {
    public AsmBlock dest;

    public jumpAsm(AsmBlock block) {
        dest = block;
    }

    @Override
    public String toString() {
        return "j " + dest;
    }
}
