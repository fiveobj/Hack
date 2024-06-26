package org.hack;

import java.util.List;

import static org.hack.tools.isNumeric;

/**
 * @Author: WuSM
 * @description: TODO 分析.vm文件，封装对输入代码的访问。读取VM命令并解析，移除代码中所有的空格和注释
 * @Date: 2024-06-25 14:08
 * @version: 1.0
 */
public class Parser {

    private List<String> file;
    private int index;//当前读取命令的索引下标
    private int size;
    private String currentCommand;//当前待编译的命令行
    private CommandType currentCommandType;//命令类型
    private String[] currentCommandSplit;
    Parser(List<String> file){
        this.file=file;
        this.size = file.size();
        this.index = 0;
        if (size==0){
            System.out.println("输入内容为空");
        }
    }

    /**
     * 从输入中读取下一条命令
     */
    public boolean advance(){
        if (index>size-1){
            System.out.println("程序已读完");
            return false;
        }else {
            currentCommand = file.get(index++);
            System.out.println("currentCommand:"+currentCommand+"---index:"+index);
            return true;
        }
    }

    //设置当前命令类型
    public void setCurrentCommandType(){
        if (!isExitCurrentCommand()){
            return;
        }

        splitLine();
        String firstChar = currentCommandSplit[0];
        switch (firstChar){
            case "push":
                currentCommandType=CommandType.C_PUSH;
                break;
            case "pop":
                currentCommandType=CommandType.C_POP;
                break;
            case "label":
                currentCommandType=CommandType.C_LABEL;
                break;
            case "goto":
                currentCommandType=CommandType.C_GOTO;
                break;
            case "if":
                currentCommandType=CommandType.C_IF;
                break;
            case "function":
                currentCommandType=CommandType.C_FUNCTION;
                break;
            case "return":
                currentCommandType=CommandType.C_RETURN;
                break;
            case "call":
                currentCommandType=CommandType.C_CALL;
                break;
            default:
                currentCommandType=CommandType.C_ARITHMETIC;
        }

    }

    public CommandType getCurrentCommandType() {
        return currentCommandType;
    }

    /**
     * 返回当前命令的第一个参数
     */
    public String getArg1(){
        if (currentCommandType==CommandType.C_RETURN)return null;
        else if (currentCommandSplit.length>1){
            return currentCommandSplit[1];
        }
        return null;
    }

    /**
     * 返回当前命令的第二个参数，调用前需加前置条件
     * if (currentCommandType==CommandType.C_PUSH||currentCommandType==CommandType.C_POP||currentCommandType==CommandType.C_FUNCTION||currentCommandType==CommandType.C_CALL)
     */
    public int getArg2(){
        return Integer.parseInt(currentCommandSplit[2]);
    }
    /**
     * 对当前命令按照空格分隔
     */
    private void splitLine(){
        if (!isExitCurrentCommand()){
            return;
        }
        //将 currentCommand 字符串按照一个或多个空白字符分割成一个字符串数组。
        currentCommandSplit = currentCommand.split("\\s+");
    }

    /**
     * 判断是否已取当前指令
     */
    private boolean isExitCurrentCommand(){
        if (currentCommand==null){
            System.out.println("当前命令为空");
            return false;
        }
        return true;
    }

    public String getCurrentCommand() {
        return currentCommand;
    }
}
