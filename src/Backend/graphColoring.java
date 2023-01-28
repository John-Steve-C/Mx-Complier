package Backend;

import Assembly.AsmFunc;
import Assembly.AsmProgram;

public class graphColoring {
    private final int K = 32;

    public AsmProgram program;
    public AsmFunc func;
    public livenessAnalysis liveAnalysis;


    public graphColoring(AsmProgram program, AsmFunc func, livenessAnalysis live) {
        this.program = program;
        this.func = func;
        this.liveAnalysis = live;
    }

    public void build() {
        func.blocks.forEach(block -> {

        });
    }

}
