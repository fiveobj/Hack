package org.hack;

import org.hack.enumfile.VMCommand;
import org.hack.enumfile.VMKind;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: WuSM
 * @description: TODO
 * @Date: 2024-07-25 13:04
 * @version: 1.0
 */
public class VMWriter {
    private List<String> ans;

    VMWriter(){
        ans = new ArrayList<>();
    }

    public void writePush(VMKind seg, int index){
        ans.add("push "+seg+" "+index);
    }

    public void writePop(VMKind seg, int index){
        ans.add("pop "+seg+" "+index);
    }

    /**
     * 输入算数命令
     * @param command
     */
    public void writeArithmetic(VMCommand command){
        ans.add(command.getOp());
    }

    public void writeLabel(String label){
        ans.add("label "+label);
    }

    public void writeGoto(String label){
        ans.add("goto "+label);
    }

    public void writeIF(String label){
        ans.add("if-goto "+label);
    }
    public void writeCall(String name, int nArgs){
        ans.add("call "+name+" "+nArgs);
    }

    public void writeFunction(String name, int nArgs){
        ans.add("function "+name+" "+nArgs);
    }

    public void writeReturn(){
        ans.add("return");
    }

    public void close(){

    }

    public List<String> getAns() {
        return ans;
    }
}
