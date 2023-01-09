package Assembly.Operand;

public class Imm extends operand{
    public int value = 0;

    public Imm(int val) {
        super();
        value = val;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
