package AST.Type;

import java.util.ArrayList;

public class FuncType extends Type{

    public Type returnType = null;
    public ArrayList<Type> parameter = null;
//    public ArrayList<String> parName = null;
    public boolean isBuiltin = false;

    public FuncType() {
        super(Types.FUNC_TYPE);
    }

    public FuncType(String _name, Type retType) {
        super(Types.FUNC_TYPE);
        returnType = retType;
        name = _name;
    }

    public FuncType(Types t) {
        super(t);
    }

    public FuncType(Types t, boolean flag) {
        super(t);
        isBuiltin = flag;
    }
}
