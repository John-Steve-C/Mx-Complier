package IR.Node.Instruction;

import IR.TypeSystem.*;

// 计算出结构体（复合类型）内部的地址
// 也可以计算数组某一个元素的地址
public class getelementptr extends instruction {
    // rs : 指向 rsType.reducePtr() (结构体/数组) 的指针
    public register rd, rs;
    public entity locator1, locator2;
    // 编号 0-based
    // locator1 : *rs为数组时，数组的第几个元素
    // locator2 : *rs为结构体时，第几个成员
    public IRType rsType;

    public getelementptr(register rd, register rs, IRType rsType, entity locator1, entity locator2) {
        this.rd = rd;
        this.rs = rs;
        this.locator1 = locator1;
        this.locator2 = locator2;
        this.rsType = rsType;
    }
}
