package IR.Node.GlobalUnit;

import IR.TypeSystem.IRType;
import IR.TypeSystem.IRTypeWithCounter;

import java.util.ArrayList;
import java.util.HashMap;

public class classDef extends globalUnit {
    public String name = null;
    public ArrayList<IRType> members = new ArrayList<>();
    public ArrayList<IRTypeWithCounter> memberForAsm = new ArrayList<>();
    public HashMap<String, IRTypeWithCounter> memberType = new HashMap<>();

    public int counter = 0, align = 1;
    private int sizeAlign1 = 0, sizeAlign2 = 0, sizeAlign4 = 0, sizeAlign8 = 0;
    private int currentAlign2, currentAlign4, currentAlign8;


    public void addMember(IRType irType, String Identifier) {
        members.add(irType);
        IRType reduceIR = irType.reducePtr();
        IRTypeWithCounter irTypeWithCounter = new IRTypeWithCounter();
        irTypeWithCounter.counter = counter++;
        irTypeWithCounter.irType = irType;
        memberType.put(Identifier, irTypeWithCounter);
        memberForAsm.add(irTypeWithCounter);

        // update align space
        int align = reduceIR.getAlign();
        if (align > this.align) this.align = align;
        irTypeWithCounter.offset1 = sizeAlign1;
        sizeAlign1 += align;
        // 进位
        if (align > 2 - currentAlign2) {
            ++sizeAlign2;
            currentAlign2 = align;
        } else {
            if (currentAlign2 == 0) ++sizeAlign2;
            currentAlign2 += align;
        }

        if (align > 4 - currentAlign4) {
            irTypeWithCounter.offset4 = sizeAlign4 * 4;
            ++sizeAlign4;
            currentAlign4 = align;
        } else {
            if (currentAlign4 == 0) {
                irTypeWithCounter.offset4 = sizeAlign4 * 4;
                ++sizeAlign4;
            } else irTypeWithCounter.offset4 = sizeAlign4 * 4 + align - 4;
            currentAlign4 += align;
        }

        if (align > 8 - currentAlign8) {
            ++sizeAlign8;
            currentAlign8 = align;
        } else {
            if (currentAlign8 == 0) ++sizeAlign8;
            currentAlign8 += align;
        }
    }

    public int getSize() {
        if (align == 1) return sizeAlign1;
        if (align == 2) return sizeAlign2 * 2;
        if (align == 4) return sizeAlign4 * 4;
        return sizeAlign8 * 8;
    }
}
