package Assembly.Operand;

public class virtualReg extends reg{
    public int index;
    public boolean isAlloca = false;
    public int overflow = -1;   // if there are too many parameters, it stands for the location out of PhysicalReg

    public virtualReg(int index) {
        this.index = index;
    }

    @Override
    public String toString() {
        return "%" + index;
    }

    @Override
    public int getNumber() {
        return index + 32;
    }
}
