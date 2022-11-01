package Utility;

import Utility.Error.SemanticError;
import Utility.Type.*;

import java.util.HashMap;

public class Scope {

    // 变量名 到 变量类型 的映射
    public HashMap<String, Type> members;
//    public HashMap<String, > entities = new HashMap<>();
    public Scope parentScope;

    public Scope(Scope parent) {
        members = new HashMap<>();
        parentScope = parent;
    }

    public void defineVar(String name, Type t, Position pos) {
        if (members.containsKey(name)) throw new SemanticError(pos, "variable re-definition");
        members.put(name, t);
    }

    public boolean containVar(String name, boolean checkGlobal) {
        if (parentScope == null && !checkGlobal) return false;
        // parentScope=null 表示目前scope就是global,如果还不用查global就矛盾了
        if (members.containsKey(name)) return true;
        if (parentScope != null && checkGlobal) return parentScope.containVar(name, checkGlobal);

        return false;
    }

    public Type getType(String name, boolean checkUp) {
        if (members.containsKey(name)) return members.get(name);
        else if (parentScope != null && checkUp) return parentScope.getType(name, true);

        return null;
    }
}
