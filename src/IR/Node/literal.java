package IR.Node;

public class literal {
    public enum TYPE{
        VOID, INT, BOOL, STRING
    }
    public TYPE type;
    public int intValue = 0;
    public String stringValue = null;

    public literal(){
        type = TYPE.VOID;
    }

    public literal(int val) {
        intValue = val;
        type = TYPE.INT;
    }

    public literal(boolean val) {
        intValue = val ? 1 : 0;
        type = TYPE.BOOL;
    }

    public literal(String val) {
        stringValue = val;
        type = TYPE.STRING;
    }

    public String toString() {
        if (type == TYPE.BOOL) return intValue > 0 ? "true" : "false";
        else if (type == TYPE.INT) return String.valueOf(intValue);
        else if (type == TYPE.STRING) return stringValue;
        return "void";
    }

    public String toConstString() {
        return "const " + this.toString();
    }
}
