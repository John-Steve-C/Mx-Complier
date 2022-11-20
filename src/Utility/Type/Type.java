package Utility.Type;

public class Type {
    public enum Types {INT_TYPE, BOOL_TYPE, VOID_TYPE, CLASS_TYPE, FUNC_TYPE, NULL, CONST_NULL}
    // string is a special class, not a type.
    // 所以通过 name = "string" 进行区分

    public Types kind;  //与 type 区分，kind 表示的是 the type of Type
    public String name = null;
    public boolean assignable = false;
    public int dimension = 0;

    public Type(Types t) {
        kind = t;
        if (t == Types.INT_TYPE) name = "int";
        else if (t == Types.BOOL_TYPE) name = "bool";
        else if (t == Types.VOID_TYPE) name = "void";
        else if (t == Types.NULL) name = "null";
        else if (t == Types.CONST_NULL) name = "const_null";
    }

    // 拷贝构造函数
    public Type(Type t) {
        name = t.name;
        kind = t.kind;
        assignable = t.assignable;
        dimension = t.dimension;
    }
}
