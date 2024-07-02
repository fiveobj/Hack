package org.hack;

import jdk.jfr.ContentType;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.temporal.JulianFields;
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
    private static List<List<String>> finalAns = new ArrayList<>();
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
                    //把finalAns写入结果文件中
                    writeToFinalFile(getFileNameWithoutExtension(file.getPath()),file.getParent());
                }else {
                    System.out.println("请输入.vm文件");
                }
            }else if (file.isDirectory()){
                //如果是文件夹，统一翻译再添加到文件中
                /*String allVmFileName = filePath+File.separator + "allVMFile.vm";
                File allVmFile = new File(allVmFileName);
                // 检查文件是否存在，如果存在则删除
                if (allVmFile.exists()) {
                    if (allVmFile.delete()) {
                        System.out.println("allVmFile deleted successfully.");
                    } else {
                        System.err.println("Failed to delete the allVmFile.");
                        return;
                    }
                }*/



                List<File> vmFiles = getVmFiles(file);

                if (vmFiles.isEmpty()){
                    System.out.println("文件夹"+filePath+"下没有.vm文件");
                }else {
//                    mergeFilesContent(allVmFile, vmFiles);
//                    vmToAsm(allVmFile);
                    for (File file1 : vmFiles){
                        vmToAsm(file1);
                    }
                }

                //把finalAns写入结果文件中
                Path path = Paths.get(file.getPath());
                writeToFinalFile(path.getFileName().toString(),file.getPath());
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
                System.out.println(line);
                String[] line1s = line.split("//");//去掉注释
                if (0 == line1s.length) {
                    continue;
                }
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

        Parser parser = new Parser(fileList);
        CodeWriter codeWriter = new CodeWriter();
        while (parser.advance()){
            List<String> ans = new ArrayList<>();
            parser.setCurrentCommandType();
            CommandType commandType = parser.getCurrentCommandType();
            switch (commandType){
                case C_ARITHMETIC -> {
                    ans = codeWriter.writeArithmetic(parser.getCurrentCommand());
                }
                case C_POP, C_PUSH -> {
                    ans = codeWriter.writePushPop(commandType, parser.getArg1(), parser.getArg2(), getFileNameWithoutExtension(file.getPath()));
                }
                case C_LABEL -> {
                    ans = codeWriter.writeLable(parser.getArg1(),parser.getFunctionName()==null?"":parser.getFunctionName());
                }
                case C_GOTO -> {
                    ans = codeWriter.writeGoto(parser.getArg1(),parser.getFunctionName()==null?"":parser.getFunctionName());
                }
                case C_IF -> {
                    ans = codeWriter.writeIf(parser.getArg1(),parser.getFunctionName()==null?"":parser.getFunctionName());
                }
                case C_FUNCTION -> {
                    ans = codeWriter.writeFunction(parser.getArg1(), parser.getArg2());
                }
                case C_CALL -> {
                    ans = codeWriter.writeCall(parser.getArg1(), parser.getArg2());
                }
                case C_RETURN -> {
                    ans = codeWriter.writeReturn();
                }
            }

            finalAns.add(ans);


        }



    }

    public static void writeToFinalFile(String outputFileName, String directoryPath){
        //设置输出文件
//        String directoryPath = file.getParent();
//        String outputFileName = getFileNameWithoutExtension(filePath)+".asm";
        String outputFilePath = directoryPath + File.separator + outputFileName+".asm";//输出路径
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

        try {
            outputFileWriter = new FileWriter(outputFilePath,true);
            //写入初始化程序
            List<String> initAns = CodeWriter.writeInit();
            for (String is : initAns){
                outputFileWriter.write(is+System.lineSeparator());
                outputFileWriter.flush();
            }

            //把finalAns写入输出文件
            for (List<String> ans:finalAns){
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

    /**
     * 将多个文件添加到新文件中
     * @param newFilePath
     * @param filePaths
     */
    public static void mergeFilesContent(File newFilePath, List<File> filePaths) {
        // 使用 try-with-resources 语句确保文件资源正确关闭
        try (FileWriter writer = new FileWriter(newFilePath)) { // 创建新文件
            for (File filePath : filePaths) {
                try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
                    String line;
                    // 逐行读取当前文件的内容并写入新文件
                    while ((line = reader.readLine()) != null) {
                        writer.write(line);
                        writer.write(System.lineSeparator()); // 写入系统行分隔符
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("所有文件的内容已成功添加到新文件中。");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}