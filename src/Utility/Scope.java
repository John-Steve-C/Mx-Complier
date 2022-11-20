package Utility;

import Utility.Error.SemanticError;
import Utility.Type.*;
import IR.Node.register;

import java.util.HashMap;

public class Scope {

    // 变量名 到 变量类型 的映射
    public HashMap<String, Type> members;
    // entity stands for var/inst in IR
    public HashMap<String, register> entities = new HashMap<>();
    public Scope parentScope;

    public Scope(Scope parent) {
        members = new HashMap<>();
        parentScope = parent;
    }

    public void defineVar(String name, Type t, Position pos) {
        if (members.containsKey(name)) throw new SemanticError(pos, "variable re-definition");
        members.put(name, t);
    }

    public boolean containVar(String name, boolean checkUp) {
        if (members.containsKey(name)) return true;
        else if (parentScope != null && checkUp) return parentScope.containVar(name, true);

        return false;
    }

    public Type getType(String name, boolean checkUp) {
        if (members.containsKey(name)) return members.get(name);
        else if (parentScope != null && checkUp) return parentScope.getType(name, true);

        return null;
    }

    public void linkReg(String name, register reg) {
        entities.put(name, reg);
    }

    public register getEntity(String name, boolean checkUp) {
        if (entities.containsKey(name)) return entities.get(name);
        else if (parentScope != null && checkUp) return parentScope.getEntity(name, true);

        return null;
    }
}
