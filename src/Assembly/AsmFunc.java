package Assembly;

import java.util.ArrayList;

public class AsmFunc {
    public AsmBlock rootBlock, tailBlock;
    public String name;
    public int stackLength = 0, stackReserved, regCnt = 0, callSpilledCount = 0;
    public ArrayList<AsmBlock> blocks = new ArrayList<>();

    public AsmFunc(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
