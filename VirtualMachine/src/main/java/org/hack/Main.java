package org.hack;

import jdk.jfr.ContentType;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static org.hack.tools.getFileNameWithoutExtension;

/**
 * @Author: WuSM
 * @description: TODO
 * @Date: 2024-06-25 14:01
 * @version: 1.0
 */
public class Main {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java Main <file-path>");
            return;
        }

        String filePath = args[0];
        File file = new File(filePath);
        if (file.exists()){
            if (file.isFile()){
                //如果是文件，检查是否是.vm文件
                if (filePath.endsWith(".vm")){
                    vmToAsm(file);
                }else {
                    System.out.println("请输入.vm文件");
                }
            }else if (file.isDirectory()){
                //如果是文件夹，获取文件下的所有.vm文件
                List<File> vmFiles = getVmFiles(file);
                if (vmFiles.isEmpty()){
                    System.out.println("文件夹"+filePath+"下没有.vm文件");
                }else {
                    for (File file1 : vmFiles){
                        vmToAsm(file1);
                    }
                }
            }

        }else {
            System.out.println("路径"+filePath+"不存在");
        }
    }

    // 获取文件夹下所有 .vm 文件的函数
    public static List<File> getVmFiles(File folder) {
        List<File> vmFiles = new ArrayList<>();
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getName().endsWith(".vm")) {
                    vmFiles.add(file);
                }
            }
        }
        return vmFiles;
    }

    //对每个.vm文件进行翻译，并输出相应的汇编文件
    public static void vmToAsm(File file){
        List<String> fileList = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            System.out.println("读取文件 " + file.getAbsolutePath() + " 的内容:");
            while ((line = reader.readLine()) != null) {
                String[] line1s = line.split("//");//去掉注释
                String line1 = line1s[0].trim();//去掉前后空格
                String trimmedLine = line1.trim();
                if (!trimmedLine.isEmpty()){//去掉空行
                    System.out.println(trimmedLine);
                    fileList.add(trimmedLine);
                }
            }
        } catch (IOException e) {
            System.err.println("读取文件时出错: " + e.getMessage());
        }


        /**
         * 翻译每一行命令
         */
        //设置输出文件
        String directoryPath = file.getParent();
        String outputFileName = getFileNameWithoutExtension(file.getPath())+".asm";
        String outputFilePath = directoryPath + File.separator + outputFileName;//输出路径
        File outputFile = new File(outputFilePath);
        // 检查文件是否存在，如果存在则删除
        if (outputFile.exists()) {
            if (outputFile.delete()) {
                System.out.println("File deleted successfully.");
            } else {
                System.err.println("Failed to delete the file.");
                return;
            }
        }
        FileWriter outputFileWriter = null;
        Parser parser = new Parser(fileList);
        CodeWriter codeWriter = new CodeWriter();

        try {
            outputFileWriter = new FileWriter(outputFilePath,true);
            while (parser.advance()){
                List<String> ans = new ArrayList<>();
                parser.setCurrentCommandType();
                CommandType commandType = parser.getCurrentCommandType();
                if (commandType==CommandType.C_ARITHMETIC){
                    ans = codeWriter.writeArithmetic(parser.getCurrentCommand());
                }else if (commandType==CommandType.C_POP||commandType==CommandType.C_PUSH){
                    ans = codeWriter.writerPushPop(commandType, parser.getArg1(), parser.getArg2(), getFileNameWithoutExtension(file.getPath()));
                }
                //把ans写入输出文件
                for(String s : ans){
                    outputFileWriter.write(s+System.lineSeparator());
                    outputFileWriter.flush();
                }

            }
        }catch (IOException e) {
            System.out.println("An error occurred while writing to the file.");
            e.printStackTrace();
        } finally {
            // 确保文件流关闭
            if (outputFileWriter != null) {
                try {
                    outputFileWriter.close();
                } catch (IOException e) {
                    System.err.println("Failed to close the FileWriter: " + e.getMessage());
                }
            }
        }

    }
}