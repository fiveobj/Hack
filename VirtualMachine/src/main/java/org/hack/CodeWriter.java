package org.hack;

import java.util.ArrayList;
import java.util.List;

import static org.hack.RAMAddress.*;

/**
 * @Author: WuSM
 * @description: TODO
 * @Date: 2024-06-25 16:15
 * @version: 1.0
 */
public class CodeWriter {

    private static int eq_i = 0,gt_i=0,lt_i=0;

    /**
     * 编写执行VM初始化的汇编代码，引导程序代码，该代码必须被置于输出文件的开头
     * @return
     */
    public List<String> writeInit(){
        return null;
    }
    /**
     * 将给定的算术操作所对应的汇编代码写至输出
     * @param command
     */
    public List<String> writeArithmetic(String command){
        List<String> ans = new ArrayList<>();

        /*int sp = getSpVal();
        setSpVal(sp-1);
        int sp1 = getSpVal();
        setSpVal(sp1-1);
        int sp2 = getSpVal();*/



        switch (command){
            /**
             * add: RAM[sp2]=RAM[sp2]+RAM[sp1]
             * @sp
             * AM=M-1
             * D=M
             * A=A-1
             * M=D+M
             */
            case "add":
                ans.add("@sp");
                ans.add("AM=M-1");
                ans.add("D=M");
                ans.add("A=A-1");
                ans.add("M=D+M");
                break;
            /**
             * sub: RAM[sp2]=RAM[sp2]-RAM[sp1]
             * @sp
             * AM=M-1
             * D=M
             * A=A-1
             * M=M-D
             */
            case "sub":
                ans.add("@sp");
                ans.add("AM=M-1");
                ans.add("D=M");
                ans.add("A=A-1");
                ans.add("M=M-D");
                break;
            /**
             * eq:val=RAM[sp2]-RAM[sp1]  val=0:RAM[sp2]=0,-1
             * @sp
             * AM=M-1
             * D=M
             * A=A-1
             * D=M-D
             * M=0
             * @eq_0
             * D;JNE
             * @sp
             * A=M-1
             * M=-1
             * (eq_0)
             */
            case "eq":
                ans.add("@sp");
                ans.add("AM=M-1");
                ans.add("D=M");
                ans.add("A=A-1");
                ans.add("D=M-D");
                ans.add("M=0");
                ans.add("@eq_"+eq_i);
                ans.add("D;JNE");
                ans.add("@sp");
                ans.add("A=M-1");
                ans.add("M=-1");
                ans.add("(eq_"+eq_i+")");
                eq_i++;
                break;
            /**
             * gt:val=RAM[sp2]-RAM[sp1]  val>0:true,false
             * @sp
             * AM=M-1
             * D=M
             * A=A-1
             * D=M-D
             * M=0
             * @gt_1
             * D;JLE
             * @sp
             * A=M-1
             * M=-1
             * (gt_1)
             */
            case "gt":
                ans.add("@sp");
                ans.add("AM=M-1");
                ans.add("D=M");
                ans.add("A=A-1");
                ans.add("D=M-D");
                ans.add("M=0");
                ans.add("@gt_"+gt_i);
                ans.add("D;JLE");
                ans.add("@sp");
                ans.add("A=M-1");
                ans.add("M=-1");
                ans.add("(gt_"+gt_i+")");
                gt_i++;
                break;
            /**
             * lt:val=RAM[sp2]-RAM[sp1]  val<0:true,false
             * @sp
             * AM=M-1
             * D=M
             * A=A-1
             * D=M-D
             * M=0
             * @lt_1
             * D;JGE
             * @sp
             * A=M-1
             * M=-1
             * (lt_1)
             */
            case "lt":
                ans.add("@sp");
                ans.add("AM=M-1");
                ans.add("D=M");
                ans.add("A=A-1");
                ans.add("D=M-D");
                ans.add("M=0");
                ans.add("@lt_"+lt_i);
                ans.add("D;JGE");
                ans.add("@sp");
                ans.add("A=M-1");
                ans.add("M=-1");
                ans.add("(lt_"+lt_i+")");
                lt_i++;
                break;
            case "and":
                ans.add("@sp");
                ans.add("AM=M-1");
                ans.add("D=M");
                ans.add("A=A-1");
                ans.add("M=D&M");
                break;
            case "or":
                ans.add("@sp");
                ans.add("AM=M-1");
                ans.add("D=M");
                ans.add("A=A-1");
                ans.add("M=D|M");
                break;
            case "neg":
                ans.add("@sp");
                ans.add("A=M-1");
                ans.add("M=-M");
                break;
            case "not":
                ans.add("@sp");
                ans.add("A=M-1");
                ans.add("M=!M");
                break;
            default:
                break;
        }

        return ans;
    }

    public List<String> writerPushPop(CommandType command,String segment,int index,String filename){
        List<String> ans = new ArrayList<>();
        switch (command){
            case C_POP:
                //获取弹出要存的目的地址存到R15
                switch (segment){
                    /**
                     * ARG、LCL、THIS、THAT中存的是对应段的地址，即基址
                     * 1. 获取偏址index
                     * 2. 偏址保存到D中
                     * 3. 获取基址
                     * 4. 获取最终地址到D中
                     */
                    case "argument":
                        ans.add("@"+index);
                        ans.add("D=A");
                        ans.add("@ARG");
                        ans.add("D=D+M");
                        break;
                    case "local":
                        ans.add("@"+index);
                        ans.add("D=A");
                        ans.add("@LCL");
                        ans.add("D=D+M");
                        break;
                    case "this":
                        ans.add("@"+index);
                        ans.add("D=A");
                        ans.add("@THIS");
                        ans.add("D=D+M");
                        break;
                    case "that":
                        ans.add("@"+index);
                        ans.add("D=A");
                        ans.add("@THAT");
                        ans.add("D=D+M");
                        break;
                    /**
                     * static 是静态变量 以 文件名.变量值 作为符号，从16开始存储
                     */
                    case "static":
                        ans.add("@"+filename+"."+index);//获取静态变量对应的地址
                        ans.add("D=M");//地址存到D中，存疑，我觉得应该是D=A
                        break;
                    /**
                     * 虚拟段，不存在，即没有 pop constant index 的命令
                     */
                    case "constant":
                        break;
                    /**
                     * pointer指向的是RAM3-4，即THIS、THAT,对pointer的操作是对THIS、THAT所存内容(即基址)的操作，对THIS、THAT的操作是对基址所指向段的操作
                     * temp指向的是RAM5-12，对其内容的修改
                     * 即 pointer、temp直接等于基址，而不是指向的内存为基址
                     */
                    case "pointer":
                        ans.add("@"+index);//获取偏址
                        ans.add("D=A");//偏址存到D中
                        ans.add("@3");//获取存储基地址的地址
                        ans.add("D=D+A");//获取最终的地址 this/that
                        break;
                    case "temp":
                        ans.add("@"+index);//获取偏址
                        ans.add("D=A");//偏址存到D中
                        ans.add("@5");//获取存储基地址的地址
                        ans.add("D=D+A");//获取最终的地址
                        break;
                }

                //将最终地址存到R15中
                ans.add("@R15");
                ans.add("M=D");

                //弹出栈顶保存到D
                ans.add("@sp");
                ans.add("AM=M-1");
                ans.add("D=M");

                //将弹出的值D存到目的地址R15中
                ans.add("@R15");
                ans.add("A=M");
                ans.add("M=D");
                break;
            case C_PUSH:
                //先把值存到D

                switch (segment){
                    case "argument":
                        ans.add("@"+index);
                        ans.add("D=A");
                        ans.add("@ARG");
                        ans.add("A=D+M");
                        ans.add("D=M");
                        break;
                    case "local":
                        ans.add("@"+index);
                        ans.add("D=A");
                        ans.add("@LCL");
                        ans.add("A=D+M");
                        ans.add("D=M");
                        break;
                    case "this":
                        ans.add("@"+index);
                        ans.add("D=A");
                        ans.add("@THIS");
                        ans.add("A=D+M");
                        ans.add("D=M");
                        break;
                    case "that":
                        ans.add("@"+index);
                        ans.add("D=A");
                        ans.add("@THAT");
                        ans.add("A=D+M");
                        ans.add("D=M");
                        break;
                    case "static":
                        ans.add("@"+filename+"."+index);
                        ans.add("D=M");
                        break;
                    case "constant":
                        ans.add("@"+index);
                        ans.add("D=A");
                        break;
                    case "pointer":
                        ans.add("@"+index);
                        ans.add("D=A");
                        ans.add("@3");
                        ans.add("A=D+A");
                        ans.add("D=M");
                        break;
                    case "temp":
                        ans.add("@"+index);
                        ans.add("D=A");
                        ans.add("@5");
                        ans.add("A=D+A");
                        ans.add("D=M");
                        break;
                }

                //将值存入栈中
                ans.add("@sp");
                ans.add("A=M");
                ans.add("M=D");
                ans.add("@sp");
                ans.add("M=M+1");
            default:
                break;
        }
        return ans;
    }
}
