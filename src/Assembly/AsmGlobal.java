package Assembly;

public class AsmGlobal {
    // global def info
    public String label, asciz = null, word;
    public int size;
    public boolean isString;

    public AsmGlobal(String label, int size, String s, boolean isString) {
        this.label = label;
        this.size = size;
        this.isString = isString;
        if (isString) asciz = s;
        else word = s;
    }

    @Override
    public String toString() {
        if (isString) {
            return "\t.type\t" + label + ",@object\n" + label +
                    ":\n\t.asciz\t" + asciz + "\n\t.size\t" + label + ", " + size + "\n";
        } else {
            return "\t.type\t" + label + ",@object\n" + label +
                    ":\n\t.word\t" + word + "\n\t.size\t" + label + ", " + size + "\n";
        }
    }
}
