package Utility.Type;

import IR.Node.classDef;

public class IRType {
    public int intLen = 0, ptrNum = 0, arrayLen = 0;
    //ptrNUm 表示在 .ll 中需要输出的 * 个数，也是维数
    public IRType subArray = null;
    public classDef clsDef = null;
    public boolean isVoid = false, isString = false;

    public IRType(Type t) {
        if (t.kind != Type.Types.CLASS_TYPE) {
            if (t.kind == Type.Types.INT_TYPE) intLen = 32;
            else if (t.kind == Type.Types.BOOL_TYPE) intLen = 8;
            else if (t.kind == Type.Types.VOID_TYPE || t.kind == Type.Types.NULL) isVoid = true;
            else intLen = 32;

            ptrNum = t.dimension;
        }
    }

    public IRType(int len) {
        intLen = len;
    }

    public IRType(int ptrnum, int arraylen) {
        ptrNum = ptrnum;
        arrayLen = arraylen;
    }

    public IRType(int intlen, int ptrnum, int arraylen, classDef cDef, IRType subarr) {
        intLen = intlen;
        ptrNum = ptrnum;
        arrayLen = arraylen;
        clsDef = cDef;
        subArray = subarr;
    }

    public IRType getPtr() {
        IRType t = new IRType(intLen, ptrNum + 1, arrayLen, clsDef, subArray);
        t.isVoid = isVoid;
        t.isString = isString;
        return t;
    }

    public IRType reducePtr() {
        IRType t = new IRType(intLen, ptrNum - 1, arrayLen, clsDef, subArray);
        t.isVoid = isVoid;
        t.isString = isString;
        return t;
    }

    public int getAlign() {
        if (ptrNum > 0 || arrayLen > 0) return 4;
        if (clsDef != null) return 4;
        return intLen / 8;
    }

    public int getSize() {
        if (ptrNum > 0 || arrayLen > 0) return 4;
        if (clsDef != null) return clsDef.getSize();
        return intLen / 8;
    }

    public boolean equal(IRType rhs) {
        return rhs.intLen == intLen && rhs.ptrNum == ptrNum && rhs.arrayLen == arrayLen;
    }
}