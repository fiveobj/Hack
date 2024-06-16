package org.hack;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: WuSM
 * @description: TODO
 * @Date: 2024-06-14 12:44
 * @version: 1.0
 */
public class symbolTable {
    private Map<String, Integer> symbolTable = new HashMap<>();


    symbolTable(){
        init();
    }

    private void init(){
        symbolTable.put("SP",0);
        symbolTable.put("LCL",1);
        symbolTable.put("ARG",2);
        symbolTable.put("THIS",3);
        symbolTable.put("TAHT",4);
        symbolTable.put("R0",0);
        symbolTable.put("R1",1);
        symbolTable.put("R2",2);
        symbolTable.put("R3",3);
        symbolTable.put("R4",4);
        symbolTable.put("R5",5);
        symbolTable.put("R6",6);
        symbolTable.put("R7",7);
        symbolTable.put("R8",8);
        symbolTable.put("R9",9);
        symbolTable.put("R10",10);
        symbolTable.put("R11",11);
        symbolTable.put("R12",12);
        symbolTable.put("R13",13);
        symbolTable.put("R14",14);
        symbolTable.put("R15",15);
        symbolTable.put("SCREEN",16384);
        symbolTable.put("KBD",24576);
    }

    //加入符号表
    public boolean addEntry(String s, Integer address){
        if (symbolTable.containsKey(s))return false;
        else {
            symbolTable.put(s,address);
            return true;
        }

    }

    //判断是否包含指定的symbol
    public boolean contains(String s) {
        if (symbolTable.containsKey(s)) return true;
        return false;
    }

    //返回地址
    public Integer getAddress(String s){
        if (!symbolTable.containsKey(s))return -1;
        return symbolTable.get(s);
    }
}
