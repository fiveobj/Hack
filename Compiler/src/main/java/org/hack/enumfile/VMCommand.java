package org.hack.enumfile;

/**
 * @Author: WuSM
 * @description: TODO
 * @Date: 2024-07-25 13:31
 * @version: 1.0
 */
public enum VMCommand {
    ADD("add"),SUB("sub"),NEG("neg"),EQ("eq"),GT("gt"),LT("lt"),AND("and"),OR("or"),NOT("not");
    private String op;
    VMCommand(String op){
        this.op = op;
    }

    public String getOp() {
        return op;
    }
}
