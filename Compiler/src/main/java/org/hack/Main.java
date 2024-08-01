package org.hack;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.hack.tools.getFileNameWithoutExtension;

/**
 * @Author: WuSM
 * @description: TODO
 * @Date: 2024-07-09 20:25
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
                //如果是文件，检查是否是.jack文件
                if (filePath.endsWith(".jack")){
                    //开始翻译文件
                    jackToXml(file);
                }else {
                    System.out.println("请输入.vm文件");
                }
            }else if(file.isDirectory()){
                List<File> jackFiles = getJackFiles(file);
                if (jackFiles.isEmpty()){
                    System.out.println("文件夹"+filePath+"下没有.jack文件");
                }else {
                    for (File file1 : jackFiles){
                        jackToXml(file1);
                    }
                }
            }
        }else {
            System.out.println("路径"+filePath+"不存在");
        }
    }

    // 获取文件夹下所有 .jack 文件的函数
    public static List<File> getJackFiles(File folder) {
        List<File> vmFiles = new ArrayList<>();
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getName().endsWith(".jack")) {
                    vmFiles.add(file);
                }
            }
        }
        return vmFiles;
    }

    // 对每个 .jack 文件进行翻译并输出
    public static void jackToXml(File file){

        //去掉 注释、空行输出 新的中间文件
        String tmpDirectoryPath = file.getParent();
        String tmpFileName = getFileNameWithoutExtension(file.getName())+"Tmp.jack";
        String tmpFilePath = tmpDirectoryPath+File.separator+tmpFileName;
        File tmpFile = new File(tmpFilePath);
        try {
            // 读取文件内容
            String content = new String(Files.readAllBytes(file.toPath()));

            // 去除注释和空行
            String noCommentsOrEmptyLines = removeCommentsAndEmptyLines(content);

            // 将处理后的内容写入输出文件
            Files.write(tmpFile.toPath(), noCommentsOrEmptyLines.getBytes());

            System.out.println("注释和空行部分已去除，并将结果写入输出文件。");
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<String> fileList = new ArrayList<>();


        try (BufferedReader reader = new BufferedReader(new FileReader(tmpFile))) {
            String line;
            System.out.println("读取文件 " + tmpFile.getAbsolutePath() + " 的内容:");
            while ((line = reader.readLine()) != null) {
//                System.out.println(line);
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

        JackTokenizer jackTokenizer = new JackTokenizer(fileList);
        CompilationEngine2 compilationEngine = new CompilationEngine2(jackTokenizer);
        List<String> outputFileList = new ArrayList<>();
        if (jackTokenizer.hasMoreTokens()){//读取内容
            System.out.println("字元未处理完成");
        }

        outputFileList = compilationEngine.getOutputList();
        List<String> VMWriterAns = compilationEngine.getVMWriter();

        writeToFinalFile(tmpFile, outputFileList, "XML");
        writeToFinalFile(tmpFile, VMWriterAns, "VMW");
    }

    public static void writeToFinalFile(File file, List<String> finalAns, String type){
        String directoryPath = file.getParent();
        String outputFileName="";
        if (type.equals("XML")){
            outputFileName = getFileNameWithoutExtension(file.getName())+".xml";
        }else {
            outputFileName = getFileNameWithoutExtension(file.getName())+".vm";
        }

        String outputFilePath = directoryPath+File.separator+outputFileName;
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

            //把finalAns写入输出文件
            for (String ans:finalAns){
                outputFileWriter.write(ans+System.lineSeparator());
                outputFileWriter.flush();
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

    // 去除注释和空行的方法
    public static String removeCommentsAndEmptyLines(String code) {
        // Regular expressions for different types of comments
        String singleLineComment = "//.*?\\n";
        String multiLineComment = "/\\*.*?\\*/";
        String docComment = "/\\*\\*.*?\\*/";

        // Combine all regexes
        String regex = singleLineComment + "|" + multiLineComment + "|" + docComment;

        // Use Pattern and Matcher to replace comments
        Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
        Matcher matcher = pattern.matcher(code);
        return matcher.replaceAll("");
    }
}