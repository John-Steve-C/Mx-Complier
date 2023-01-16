package Assembly;

import java.io.PrintStream;

import Assembly.Instruction.*;

public class AsmPrinter {
    public PrintStream output;
    public AsmProgram program;

    public AsmPrinter(PrintStream output, AsmProgram program) {
        this.output = output;
        this.program = program;
    }

    public void printFunc(AsmFunc func) {
        func.blocks.forEach(this::printBlock);
    }

    public void printBlock(AsmBlock block) {
        output.println(block + ":");
        for (AsmInst i = block.headInst; i != null; i = i.next)
            output.println("\t" + i);
    }

    public void print() {
        output.println("""
                \t.text
                \t.globl main
                \t.p2align\t2
                \t.type\tmain,@function""");
        program.functions.forEach(this::printFunc);
        output.println(".data");
        program.globals.forEach(it -> output.println(it.toString()));
    }
}
