package IR.Node;

import IR.Node.Instruction.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;

import IR.Node.Instruction.instruction;

public class block {
    public String name, label, comment; // comment is used for debug
    public instruction headStmt = null, tailStmt = null;
    // 保存前继和后继的 block
    public HashSet<block> successors = new HashSet<>(), predecessor = new HashSet<>(), children = null;
    public LinkedList<phi> Phis = new LinkedList<>();

    public boolean jump = false;
    public int loopDepth = 0, blockIndex = 0;

    public terminator tail = null;

    public block(int loopDepth) {
        this.loopDepth = loopDepth;
    }

    public void push_back(instruction stmt) {
        if (tail != null) return;
        stmt.parentBlock = this;
        if (stmt instanceof phi) Phis.push((phi) stmt);
        if (stmt instanceof terminator t) {
            tail = t;
            if (tail instanceof br b) {
                if (b.val == null) successors.add(b.trueBranch);
                else {
                    successors.add(b.trueBranch);
                    successors.add(b.falseBranch);
                }
            }
        }
        if (tailStmt == null) tailStmt = headStmt = stmt;
        else {
            tailStmt.next = stmt;
            stmt.pre = tailStmt;
            tailStmt = stmt;
        }
    }

    public void push_front(instruction stmt) {
        stmt.parentBlock = this;
        if (stmt instanceof phi) Phis.push((phi) stmt);
        if (headStmt == null) headStmt = tailStmt = stmt;
        else {
            headStmt.pre = stmt;
            stmt.next = headStmt;
            headStmt = stmt;
        }
    }
}
