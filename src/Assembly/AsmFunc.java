package Assembly;

import java.util.ArrayList;
import java.util.BitSet;

public class AsmFunc {
    public AsmBlock rootBlock, tailBlock;
    public String name;
    public int stackLength = 0, stackReserved, regCnt = 0, originRegCnt = 0;
    public int calleeSavedCount, callSpilledCount = 0;
    public BitSet calleeSavedUsed;
    public ArrayList<AsmBlock> blocks = new ArrayList<>();

    public AsmFunc(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
