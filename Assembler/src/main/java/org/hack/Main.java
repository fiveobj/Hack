package org.hack;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static org.hack.tools.*;

/**
 * @Author: WuSM
 * @description: TODO
 * @Date: 2024-06-07 20:09
 * @version: 1.0
 */
public class Main {

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.out.println("Usage: java ReadFileLineByLine <file-path>");
            return;
        }
       //将输入文件的内容保存下来
        String filePath = args[0];
        List<String> fileStrings = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {

                String[] line1s = line.split("//");//去掉注释
                String line1 = line1s[0].trim();//去掉前后空格
                String trimmedLine = line1.trim();
                if (!trimmedLine.isEmpty()){
                    System.out.println(trimmedLine);
                    fileStrings.add(trimmedLine);
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        //保存转为无符号文件内容
        List<String> fileMiddleStrings = new ArrayList<>();


        parser parser = new parser(fileStrings);//传入文件内容

        symbolTable symbolTable = new symbolTable();
        int symbolAddress = 16;//当前可存储地址
        //----------------------------中间处理：从有符号变成无符号-----------------
        int lCommandCount = 0;//记录伪命令条数，得到正确下标
        while (parser.advance()){//判断是否已读完，并且读取下一条指令
            parser.setCurrentCommandType();//设置当前命令类型;
            int currentCommandType = parser.getCurrentCommandType();//获取当前指令类型
            if(currentCommandType==0){//A-指令
                /*//将@后字符进行转换
                String aSymbol = parser.symbol();//获取指令字符
                //判断字符内容是否伪数字
                if (isNumeric(aSymbol)){//如果是，直接将当前指令写入中间文件
                    fileMiddleStrings.add(parser.getCurrentCommand());
                }else {//从字符表中查找对应地址
                    if (!symbolTable.contains(aSymbol)){//如果符号表不包含
                        symbolTable.addEntry(aSymbol,symbolAddress++);
                    }
                    Integer address = symbolTable.getAddress(aSymbol);
                    String newCommand = "@"+address;
                    fileMiddleStrings.add(newCommand);
                }*/
                fileMiddleStrings.add(parser.getCurrentCommand());
            }else if (currentCommandType==1){//c-指令
                fileMiddleStrings.add(parser.getCurrentCommand());
            }else if (currentCommandType==2){//伪指令
                //将()中的字符进行转换
                lCommandCount++;
                String lSymbol = parser.symbol();//获取指令字符
                if (!symbolTable.contains(lSymbol)){//如果符号表不包含
                    symbolTable.addEntry(lSymbol, parser.getIndex()-lCommandCount);
                    System.out.println("lSymbol:"+lSymbol+"--index:"+(parser.getIndex()-lCommandCount));
                }
            }/*else {//其他指令-先跳过

            }*/
        }

        //--------第二遍处理，把无符号换为二进制写入文件---------------------------------
        //设置输出文件
        File inputFile = new File(filePath);
        String directoryPath = inputFile.getParent();
        String outputFileName = getFileNameWithoutExtension(filePath)+".hack";

        /*
        * File.separator 是 Java 提供的一个静态字段，用于表示当前操作系统的文件路径分隔符。由于不同操作系统使用不同的路径分隔符，例如，Windows 使用反斜杠 (\) 作为路径分隔符，而 Unix/Linux 使用正斜杠 (/) 作为路径分隔符，因此，File.separator 提供了一种跨平台的方式来构建文件路径。
        * 使用 File.separator 可以确保你的代码在不同操作系统上都能正确运行，而无需手动处理路径分隔符的差异。
        * */
        String outputFilePath = directoryPath + File.separator + outputFileName;//输出路径
        parser outputParser = new parser(fileMiddleStrings);

        File file = new File(outputFilePath);
        // 检查文件是否存在，如果存在则删除
        if (file.exists()) {
            if (file.delete()) {
                System.out.println("File deleted successfully.");
            } else {
                System.err.println("Failed to delete the file.");
                return;
            }
        }
        FileWriter fileWriter = null;

        try {
            fileWriter = new FileWriter(outputFilePath,true);
            while (outputParser.advance()){//判断是否已读完，并且读取下一条指令
                outputParser.setCurrentCommandType();//设置当前命令类型;
                int currentCommandType = outputParser.getCurrentCommandType();//获取当前指令类型
                if(currentCommandType==0){//A-指令
                    //将@后字符进行转换
                    String aSymbol = outputParser.symbol();//获取指令字符
                    int value;
                    //判断字符内容是否伪数字
                    if (isNumeric(aSymbol)){//如果是，直接将当前指令写入中间文件
                        value = Integer.parseInt(aSymbol);//十进制
                    }else {//从字符表中查找对应地址
                        if (!symbolTable.contains(aSymbol)){//如果符号表不包含
                            symbolTable.addEntry(aSymbol,symbolAddress++);
                            System.out.println("AAAAAAA"+aSymbol);
                        }
                        value = symbolTable.getAddress(aSymbol);
                    }

                    String betyValue = Integer.toBinaryString(value);
                    String command = String.format("%16s", betyValue).replace(' ','0');
                    System.out.println("index:"+outputParser.getIndex()+"A-command:"+command);
                    fileWriter.write(command+System.lineSeparator());
                    fileWriter.flush();
                }else if (currentCommandType==1){//c-指令
                    String dest = outputParser.getDest();
                    String comp = outputParser.getComp();
                    String jump = outputParser.getJump();
                    code code = new code(dest,comp,jump);
                    Byte[] destCode = code.transformDest();
                    Byte[] compCode = code.transformComp();
                    Byte[] jumpCode = code.transformJump();

                    System.out.println("dest:"+dest+"//comp"+comp+"//jump:"+jump);
                    String command = mergeByteArraysToString(compCode, destCode, jumpCode);
                    System.out.println("index:"+outputParser.getIndex()+"//C-command:"+command);
                    System.out.println("dest:"+dest+mergeByteArraysToString(destCode)+"//comp"+comp+mergeByteArraysToString(compCode)+"//jump:"+jump+mergeByteArraysToString(jumpCode));
                    fileWriter.write("111"+command+System.lineSeparator());
                    fileWriter.flush();
                }
            }

        }catch (IOException e) {
            System.out.println("An error occurred while writing to the file.");
            e.printStackTrace();
        } finally {
            // 确保文件流关闭
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (IOException e) {
                    System.err.println("Failed to close the FileWriter: " + e.getMessage());
                }
            }
        }


    }
}