package IR.TypeSystem;

public abstract class entity {
    // 表示一系列实体的基类
    // 既可以表示 constant，也可以表示 register
    abstract public boolean entityEquals(entity en);

    abstract public entity clone();
}
