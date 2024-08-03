package org.hack;

import org.hack.enumfile.symbolKind;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @Author: WuSM
 * @description: TODO
 * @Date: 2024-07-25 10:57
 * @version: 1.0
 */
public class SymbolTable {
     private class table{
         String name;
         String type;
         symbolKind kind;
         int index;

         public symbolKind getKind() {
             return kind;
         }

         public String getName() {
             return name;
         }

         public String getType() {
             return type;
         }

         public int getIndex() {
             return index;
         }

         public void setIndex(int index) {
             this.index = index;
         }

         public void setKind(symbolKind kind) {
             this.kind = kind;
         }

         public void setName(String name) {
             this.name = name;
         }

         public void setType(String type) {
             this.type = type;
         }
     }
     private HashMap<String, table> tableMap;
     private int staticIndex=0, fieldIndex=0, argIndex=0, varIndex=0;
     private SymbolTable childTable;

     SymbolTable(){
         this.argIndex = 0;
         this.varIndex = 0;
         tableMap = new HashMap<>();
     }

     public SymbolTable startSubroutine(){
         SymbolTable chile = new SymbolTable();
         return chile;
     }

    /**
     * 添加标识符操作
     * @param name 标识符名称
     * @param type 数据类型：基本数据类型、对象、数组...
     * @param kind 分类
     */
     public void Define(String name, String type, symbolKind kind){
         table table = new table();
         table.setName(name);
         table.setType(type);
         table.setKind(kind);
         switch (kind){
             case ARG -> table.setIndex(argIndex++);
             case VAR -> table.setIndex(varIndex++);
             case STATIC -> table.setIndex(staticIndex++);
             case FIELD -> table.setIndex(fieldIndex++);
         }

         tableMap.put(name, table);
     }

    /**
     * 返回分类的个数
     * @param symbolkind
     * @return
     */
     public int VarCount(symbolKind symbolkind){
         switch (symbolkind){
             case ARG -> {
                 return argIndex;
             }
             case VAR -> {
                 return varIndex;
             }
             case STATIC -> {
                 return staticIndex;
             }
             case FIELD -> {
                 return fieldIndex;
             }
         }
         return -1;
     }

    /**
     * 返回标识符分类
     * @param name
     * @return
     */
     public symbolKind kindOf(String name){
         if (!tableMap.containsKey(name))return null;
         table table = tableMap.get(name);
         return table.getKind();
     }

    /**
     * 返回标识符类型
     * @param name
     * @return
     */
     public String TypeOf(String name){
         table table = tableMap.get(name);
         return table.getType();
     }

    /**
     * 返回标识符索引
     * @param name
     * @return
     */
     public int IndexOf(String name){
         table table = tableMap.get(name);
         return table.getIndex();
     }

    /**
     * 是否有当标识符
     */
    public boolean ishas(String name){
        if (tableMap.containsKey(name))return true;
        return false;
    }
}


