package IR.Node;

import Utility.Type.ClassType;

import java.util.ArrayList;
import java.util.HashMap;

public class classDef {
    public String name = null;
    public ArrayList<IRType> members = new ArrayList<>();
    public ArrayList<funcDef> memFuncType;
    public HashMap<String, IRType> memberType = new HashMap<>();


    public classDef(ClassType t) {
        name = t.name;
    }

    public void addMember(IRType type, String id) {
        members.add(type);
        memberType.put(id, type);
    }

    public int getSize() {
        return 4;
    }
}
