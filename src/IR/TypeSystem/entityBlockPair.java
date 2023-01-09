package IR.TypeSystem;

import IR.Node.block;

public class entityBlockPair {
    // 主要用来处理 phi 指令，将 block 与 entity 合成一个对象
    public entity en;
    public block blk;

    public entityBlockPair(entity en, block bl){
        this.en = en;
        this.blk = bl;
    }
}
