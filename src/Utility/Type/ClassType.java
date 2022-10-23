package Utility.Type;

import java.util.HashMap;

public class ClassType extends Type{
    public HashMap<String, Type> member = new HashMap<>();
    public HashMap<String, FuncType> method = new HashMap<>();
    // 与globalScope中的写法类似，都需要按名字进行查找

    public ClassType() {
        super(Types.CLASS_TYPE);
    }

    public ClassType(Types t) {
        super(t);
    }
}
