package IR.Node;

import java.util.ArrayList;

public class program {

    public ArrayList<funcDef> func = new ArrayList<>();
    public ArrayList<classDef> cls = new ArrayList<>();
    public ArrayList<block> blk = new ArrayList<>();
    public ArrayList<declaration> decl = new ArrayList<>();
    public ArrayList<globalVarDeclaration> varDecl = new ArrayList<>();
    public ArrayList<globalStringConst> strConst = new ArrayList<>();
    public funcDef mainFunc;

    public void add(funcDef node) {
        func.add(node);
    }

    public void add(classDef node) {
        cls.add(node);
    }

    public void add(block node) {
        blk.add(node);
    }

    public void add(declaration dec) {
        decl.add(dec);
    }

    public void add(globalVarDeclaration var){
        varDecl.add(var);
    }

    public void add(globalStringConst str) {
        strConst.add(str);
    }

}
