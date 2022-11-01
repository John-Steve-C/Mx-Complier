import AST.*;
import Test.SemanticChecker;
import Utility.Error.Error;
import Utility.*;
import Parser.*;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.IOException;
import java.io.InputStream;


public class Compiler
{
    public static void main(String[] args) throws IOException
    {
        InputStream input_stream = System.in;
        CharStream input = CharStreams.fromStream(input_stream);
//        CharStream input = CharStreams.fromFileName("/mnt/d/Coding/Mx_Compiler/testcases/sema/lambda-package/lambda-5.mx");

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

            new SymbolCollector(gScope).visit(ASTRoot);

            SemanticChecker semanticChecker = new SemanticChecker(gScope);
            semanticChecker.visit(ASTRoot);

            // Sort of IRBuilder, maybe opt
            // Sort of ASMBuilder, maybe opt
//            BuiltinFunctionASMPrinter builtin_printer = new BuiltinFunctionASMPrinter("builtin.s");
        }
        catch (Error err)
        {
            err.show_error();
            throw new RuntimeException();
        }
    }
}