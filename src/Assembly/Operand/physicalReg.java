package Assembly.Operand;

public class physicalReg extends reg{
    public String name;
    public int order = 0;

    public physicalReg(String name, int order) {
        this.name = name;
        this.order = order;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int getNumber() {
        return order;
    }
}
