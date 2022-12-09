package IR.TypeSystem;

import java.util.Objects;

public class constant extends entity{
    public enum constType {
        STRING, INT, VOID, BOOL
    }

    public constType kind;
    private int intValue;
    private String stringValue;

    public constant() {
        super();
        this.kind = constType.VOID;
    }

    public constant(boolean flag) {
        super();
        this.kind = constType.BOOL;
        this.intValue = flag ? 1 : 0;
    }

    public constant(int value) {
        super();
        this.intValue = value;
        kind = constType.INT;
    }

    public constant(String value) {
        super();
        this.stringValue = value;
        kind = constType.STRING;
    }

    public boolean entityEquals(entity en) {
        if (en instanceof register) return false;
        constant other = (constant) en;
        if (kind != other.kind) return false;
        if (this.kind == constType.BOOL) return intValue == other.intValue;
        if (this.kind == constType.INT) return intValue == other.intValue;
        if (this.kind == constType.STRING) return Objects.equals(stringValue, other.stringValue);
        return true;
    }

    @Override
    public entity clone() {
        if (kind == constType.INT) return new constant(intValue);
        if (kind == constType.BOOL) return new constant(intValue>0);
        if (kind == constType.STRING) return new constant(stringValue);
        return new constant();
    }

    public int getIntValue() {
        return intValue;
    }

    public String getStringValue() {
        return stringValue;
    }

    public boolean getBoolValue() {
        return intValue > 0;
    }

    public void setBoolValue(boolean flag) {
        intValue = flag ? 1 : 0;
    }

    public int getValue(){
        if (this.kind == constType.BOOL) return (intValue > 0 ? 1 : 0);
        if (this.kind == constType.INT) return intValue;
        return 0;
    }

    @Override
    public String toString() {
        if (this.kind == constType.BOOL) return "const " + (intValue > 0);
        if (this.kind == constType.INT) return "const " + intValue;
        if (this.kind == constType.STRING) return "const " + stringValue;
        return "const void";
    }
}
