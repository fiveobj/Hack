package org.hack;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

/**
 * @Author: WuSM
 * @description: TODO
 * @Date: 2024-06-07 20:36
 * @version: 1.0
 */
public class parser {
    private List<String> file;
    private String currentCommand;//当前待编译的命令行
    private String[] currentCommandSplit;

    private int currentCommandType;//当前指令类型：A-0,C-1,L-2,其他-3

    private int index;//当前读取命令的索引下标
    private int size;
    parser(){}
    parser(List<String> file){
        this.file=file;
        this.size = file.size();
        this.index = 0;
        if (size==0){
            System.out.println("输入内容为空");
        }

    }



    /**
     * @throws IOException
     * 从输入中读取下一条命令
     */
    public boolean advance() throws IOException {

        if (index>size-1){
            System.out.println("程序已读完");
            return false;
        }else {
            currentCommand = file.get(index++);
            System.out.println("currentCommand:"+currentCommand+"---index:"+index);
            return true;
        }
    }

    //判断是否已取当前指令
    private boolean isExitCurrentCommand(){
        if (currentCommand==null){
            System.out.println("当前命令为空");
            return false;
        }
        return true;
    }

    //设置当前命令类型
    public void setCurrentCommandType(){
        if (!isExitCurrentCommand()){
            return;
        }

        splitLine();
        char firstChar = readFirstChar(currentCommandSplit[0]);
        switch (firstChar){
            case '@':
                currentCommandType=0;
                break;
            case '(':
                currentCommandType=2;
                break;
            case '/':
                currentCommandType=3;
                break;
            case '-':
                currentCommandType=3;
                break;
            default:
                currentCommandType=1;
        }

    }

    public int getCurrentCommandType() {
        return currentCommandType;
    }

    public String getCurrentCommand() {
        return currentCommand;
    }

    //返回A/L指令中的当前符号或者十进制值
    public String symbol(){
        String symbol = null;
        if (currentCommandType==0){//A-指令
            symbol=currentCommandSplit[0].substring(1);
        }
        else if (currentCommandType==2){//L-指令
            symbol=currentCommandSplit[0].substring(1, currentCommandSplit[0].length()-1);
        }else {
            symbol="当前指令非A-指令/L-指令";
        }
        return symbol;
    }

    //返回当前C-指令的dest助记符-返回=前的字符串(去空格)
    public String getDest(){
        if (!isExitCurrentCommand()){
            return "null";
        }
        if (currentCommandType!=1){//C-指令
            System.out.println("当前指令非C-指令");
            return "null";
        }
        String[] parts  = currentCommand.split("=");
        if (parts.length>1){//有等号
            String dest = parts[0];
            return dest;
        }

        return "null";
    }

    //返回当前C-指令的comp助记符
    public String getComp(){
        if (!isExitCurrentCommand()){
            return "null";
        }
        if (currentCommandType!=1){//C-指令
            System.out.println("当前指令非C-指令");
            return "null";
        }
        String[] parts  = currentCommand.split("=");
        if (parts.length>1){//有等号
            String compJumpPart = parts[1];//包含=后面所有的部分，即comp+jump
            String[] compJumpParts = compJumpPart.split(";");
            String comp = compJumpParts[0];
            return comp;
        }else {//没有等号
            String compJumpPart = parts[0];//包含=后面所有的部分，即comp+jump
            String[] compJumpParts = compJumpPart.split(";");
            String comp = compJumpParts[0];
            return comp;
        }

    }

    //返回当前C-指令的jump助记符
    public String getJump(){
        if (!isExitCurrentCommand()){
            return "null";
        }
        if (currentCommandType!=1){//C-指令
            System.out.println("当前指令非C-指令");
            return "null";
        }
        String[] parts  = currentCommand.split(";");
        if (parts.length>1){//有分号
            String jump = parts[1];
            return jump;
        }

        return "null";
    }

    //当前命令分隔
    private void splitLine(){
        if (!isExitCurrentCommand()){
            return;
        }
        //将 currentCommand 字符串按照一个或多个空白字符分割成一个字符串数组。
        currentCommandSplit = currentCommand.split("\\s+");
    }



    //返回第一个字符
    private char readFirstChar(String s){
        if (s==null||s.isEmpty()){
            System.out.println("字符为空");
            return '-';
        }

        char[] chars = s.toCharArray();

        return chars[0];
    }


    /**
     * @return 当前指令的下标地址
     */
    public int getIndex() {
        return index;
    }
}
