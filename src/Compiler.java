import AST.*;
import AST.Node.RootNode;
import Assembly.*;
import Backend.*;
import Frontend.SemanticChecker;
import Frontend.SymbolCollector;
import Utility.Error.Error;
import Utility.*;
import Parser.*;
import IR.*;
import IR.Node.Program;
import IR.Node.GlobalUnit.*;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.HashMap;


public class Compiler
{
    public static void main(String[] args) throws IOException
    {
        InputStream input_stream = System.in;
        CharStream input = CharStreams.fromStream(input_stream);
//        CharStream input = CharStreams.fromFileName("/mnt/d/Coding/Mx_Compiler/testcases/sema/lambda-package/lambda-5.mx");
//        CharStream input = CharStreams.fromFileName("mytest/test.mx");
//        CharStream input = CharStreams.fromFileName("/mnt/d/Coding/Mx_Compiler/mytest/test.mx");

        // print as file
//        PrintStream output_llvm = new PrintStream("mytest/test.ll");
//        PrintStream output_asm = new PrintStream("mytest/test.s");
        // for OJ-test
//        PrintStream output_llvm = new PrintStream("output.ll");
        PrintStream output_asm = new PrintStream("output.s");

        try
        {
            // Sort of MxLexer & Parser
            GlobalScope gScope = new GlobalScope(null);
            MxLexer lexer = new MxLexer(input);
            lexer.removeErrorListeners();   //修改为自己的 error
            lexer.addErrorListener(new MxErrorListener());
            MxParser parser = new MxParser(new CommonTokenStream(lexer));
            parser.removeErrorListeners();
            parser.addErrorListener(new MxErrorListener());
            ParseTree parseTreeRoot = parser.program();

            // Sort of ASTBuiler & SemanticChecker
            RootNode ASTRoot;
            ASTBuilder astBuilder = new ASTBuilder(gScope);
            ASTRoot = (RootNode) astBuilder.visit(parseTreeRoot);

            // for IR generation
            HashMap<String, classDef> idToDef = new HashMap<>();
            HashMap<String, funcDef> idToFuncDef = new HashMap<>();
            gScope.idToClassDef = idToDef;
            gScope.idToFuncDef = idToFuncDef;
            new SymbolCollector(gScope).visit(ASTRoot); // both semantic & IR

            SemanticChecker semanticChecker = new SemanticChecker(gScope);
            semanticChecker.visit(ASTRoot);

            // Sort of IRBuilder, maybe opt
            Program pg = new Program();
            new IRBuilder(pg, gScope, idToDef, idToFuncDef).visit(ASTRoot);
//            new IRPrinter(System.out).visitProgram(pg);
//            new IRPrinter(output_llvm).visitProgram(pg);    // print in file

            // Sort of ASMBuilder, maybe opt
            new BuiltinFunctionASMPrinter("builtin.s"); // we print the buildInFunction by builtin.s
            AsmProgram asmPg = new AsmProgram();
            new AsmBuilder(asmPg).visitProgram(pg);
//            new AsmPrinter(output_asm, asmPg).print();  // print raw asmCode on screen

            // solution 1: 直接压栈
//            new regAllocationBasic(asmPg).work();

            // solution 2: 图染色
//            new livenessAnalysis(asmPg).work();
            new regAllocation(asmPg).work();

            new AsmPrinter(output_asm, asmPg).print();
        }
        catch (Error err)
        {
            err.show_error();
            throw new RuntimeException();
        }
    }
}