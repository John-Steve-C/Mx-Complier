package Assembly;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

import Assembly.Operand.*;

public class AsmProgram {
    // RV32I 规定的通用寄存器名称
    public static ArrayList<String> physicalRegName = new ArrayList<>(Arrays.asList(
            "zero", "ra", "sp", "gp", "tp", "t0", "t1", "t2", "s0", "s1",
            "a0", "a1", "a2", "a3", "a4", "a5", "a6", "a7",
            "s2", "s3", "s4", "s5", "s6", "s7", "s8", "s9", "s10", "s11", "t3", "t4", "t5", "t6"));

    public ArrayList<physicalReg> physicalRegs = new ArrayList<>();
    public LinkedList<AsmFunc> functions = new LinkedList<>();
    public ArrayList<AsmGlobal> globals = new ArrayList<>();

    public AsmProgram() {
        for (int i = 0; i < 32;++i) {
            physicalRegs.add(new physicalReg(physicalRegName.get(i), i));
        }
    }
}
