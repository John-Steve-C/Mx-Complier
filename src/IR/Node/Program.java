package IR.Node;

import IR.Node.GlobalUnit.*;

import java.util.ArrayList;

public class Program {

    public ArrayList<funcDef> func = new ArrayList<>();
    public ArrayList<classDef> cls = new ArrayList<>();
    public ArrayList<block> blk = new ArrayList<>();
    public ArrayList<declare> decl = new ArrayList<>();
    public ArrayList<globalVarDeclaration> varDecl = new ArrayList<>();
    public ArrayList<globalStringConst> strConst = new ArrayList<>();
    public funcDef mainFunc, builtinFunc;

    public void push_back(funcDef node) {
        func.add(node);
    }

    public void push_back(classDef node) { cls.add(node);}

    public void push_back(block node) {
        blk.add(node);
    }

    public void push_back(declare dec) {
        decl.add(dec);
    }

    public void push_back(globalVarDeclaration var) { varDecl.add(var); }

    public void push_back(globalStringConst str) {
        strConst.add(str);
    }

}
