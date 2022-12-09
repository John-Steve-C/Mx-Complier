package IR.TypeSystem;

import IR.Node.GlobalUnit.classDef;
import Utility.Type.Type;

public class IRType {
    public int intLen = 0;
    public int ptrNum = 0;
    public int arrayLen = 0;
    public classDef cDef = null;    // 如果是 class Type，直接保存
    public IRType arraySubIR = null;    // 嵌套实现数组
    public boolean isVoid = false, isString = false;

    public IRType getPtr() {
        IRType irType = new IRType(intLen, ptrNum + 1, arrayLen, cDef, arraySubIR);
        irType.isVoid = isVoid;
        irType.isString = isString;
        return irType;
    }

    public IRType reducePtr() {
        IRType irType = new IRType(intLen, ptrNum - 1, arrayLen, cDef, arraySubIR);
        irType.isVoid = isVoid;
        irType.isString = isString;
        return irType;
    }

    public IRType(IRType irType) {
        intLen = irType.intLen;
        ptrNum = irType.ptrNum;
        arrayLen = irType.arrayLen;
        cDef = irType.cDef;
        isVoid = irType.isVoid;
        isString = irType.isString;
        arraySubIR = irType.arraySubIR;
    }

    public IRType() {
        intLen = 32;
    }

    public IRType(int intLen) {
        this.intLen = intLen;
    }

    public IRType(int intLen, int ptrNum, int arrayLen, classDef cDef, IRType arraySubIR) {
        this.intLen = intLen;
        this.ptrNum = ptrNum;
        this.arrayLen = arrayLen;
        this.cDef = cDef;
        this.arraySubIR = arraySubIR;
    }

    public IRType(int intLen, int ptrNum, int arrayLen, classDef cDef) {
        this.intLen = intLen;
        this.ptrNum = ptrNum;
        this.arrayLen = arrayLen;
        this.cDef = cDef;
    }

    public IRType(int ptrNum, int arrayLen, IRType arraySubIR) {
        this.ptrNum = ptrNum;
        this.arrayLen = arrayLen;
        this.arraySubIR = arraySubIR;
    }

    public IRType(Type t) {
        if (t.kind != Type.Types.CLASS_TYPE) {
            if (t.kind == Type.Types.INT_TYPE) intLen = 32;
            else if (t.kind == Type.Types.BOOL_TYPE) intLen = 8;
            else if (t.kind == Type.Types.VOID_TYPE || t.kind == Type.Types.NULL) isVoid = true;
            else intLen = 32;
            ptrNum = t.dimension;
        }
    }

    public IRType(classDef cDef, int ptrNum, int arrayLen) {
        this.cDef = cDef;
        this.ptrNum = ptrNum;
        this.arrayLen = arrayLen;
    }

    public int getAlign() {
        if (ptrNum > 0 || arrayLen > 0) return 4;
        if (cDef != null) return 4;
        return intLen / 8;
    }

    public int getSize() {
        if (ptrNum > 0 || arrayLen > 0) return 4;
        if (cDef != null) return cDef.getSize();
        return intLen / 8;
    }

    public boolean equal(IRType another) {
        return (ptrNum == another.ptrNum && arrayLen == another.arrayLen && intLen == another.intLen);
    }
}
