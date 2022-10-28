package Utility;

import Utility.Error.SemanticError;
import Utility.Type.ClassType;
import Utility.Type.FuncType;
import Utility.Type.Type;

import java.util.HashMap;
import java.util.HashSet;

public class GlobalScope extends Scope{

    // 类型名称 到 类型存储 的映射，表示整个程序中出现过的变量类型
    public HashMap<String, Type> types = new HashMap<>();
    public HashMap<String, FuncType> funcTypes = new HashMap<>();
    // 系统自带关键字集合
    public HashSet<String> Keywords = new HashSet<>();

    public GlobalScope(Scope parent) {
        super(parent);
        Keywords.add("int");
        Keywords.add("string");
        Keywords.add("bool");
        Keywords.add("void");
        Keywords.add("new");
        Keywords.add("this");
        Keywords.add("class");
        Keywords.add("null");
        Keywords.add("true");
        Keywords.add("false");
        Keywords.add("if");
        Keywords.add("else");
        Keywords.add("for");
        Keywords.add("while");
        Keywords.add("break");
        Keywords.add("continue");
        Keywords.add("return");
    }

    public void addVarType(Type t, String name, Position pos) {
        if (types.containsKey(name)) throw new SemanticError(pos,"multiple definition");
        if (funcTypes.containsKey(name)) throw new SemanticError(pos, "name conflict with the function");
        types.put(name, t);
    }

    public void addFuncType(FuncType func, String name, Position pos) {
        if (types.containsKey(name)) throw new SemanticError(pos, "name conflict with the class");
        if (funcTypes.containsKey(name)) throw new SemanticError(pos, "function re-definition");
        funcTypes.put(name,func);
    }

    public Type queryType(String name, Position pos) {
        if (types.containsKey(name)) return types.get(name);
        else throw new SemanticError(pos, "undefined type : " + name);
    }

    public FuncType queryFuncType(String name, Position pos) {
        if (funcTypes.containsKey(name)) return funcTypes.get(name);
        else throw new SemanticError(pos, "undefined function : " + name);
    }

    // 查找 class ’name‘ 中是否存在 ’member‘ 类型的成员
    public Type queryMemberType(String name, String member, Position pos) {
        if (types.containsKey(name)) {
            Type t = types.get(name);
            if (t.kind == Type.Types.CLASS_TYPE)
                return ((ClassType) t).member.getOrDefault(member, null);
            else
                throw new SemanticError(pos, "undefined class : " + name);
        }
        else throw new SemanticError(pos, "undefined type : " + name);
    }

    // 查找 class ’name‘ 中是否存在 'member' 函数类型的成员
    public FuncType queryMemberFuncType(String name, String member, Position pos) {
        if (types.containsKey(name)) {
            Type t = types.get(name);
            if (t.kind == Type.Types.CLASS_TYPE)
                return ((ClassType) t).method.getOrDefault(member, null);
            else
                throw new SemanticError(pos, "undefined class : " + name);
        }
        else throw new SemanticError(pos, "undefined function : " + name);
    }

    public void checkNameConflict(String name, Position pos) {
        if (types.containsKey(name)) throw new SemanticError(pos, name + "has conflicts with types");
        if (Keywords.contains(name)) throw new SemanticError(pos, name + "has conflicts with keywords");
    }
}
