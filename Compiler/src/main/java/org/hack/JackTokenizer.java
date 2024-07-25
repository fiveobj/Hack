package org.hack;

import org.hack.enumfile.tokenType;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author: WuSM
 * @description: TODO
 * @Date: 2024-07-09 20:45
 * @version: 1.0
 */
public class JackTokenizer {

    // 关键字
    private static final Set<String> KEYWORDS = new HashSet<>(Arrays.asList(
            "class", "constructor", "function", "method", "field", "static", "var", "int", "char", "boolean", "void",
            "true", "false", "null", "this", "let", "do", "if", "else", "while", "return"
    ));

    // 符号和运算符
    private static final String SYMBOLS = "[{}()\\[\\].,;:!?@#%^&*+=|<>/~-]";

    // 字符串常量 不包括双引号和新行符
    private static final String STRING_PATTERN = "\"(.*?)\"";

    // 整数常量
    private static final String INTEGER_PATTERN = "\\b\\d+\\b";

    // 标识符
    private static final String IDENTIFIER_PATTERN = "\\b[A-Za-z_][A-Za-z0-9_]*\\b";
    private List<String> file;
    private List<String> tokenList;
    private int tokenListIndex=0;


    /**
     * @param file 输入的文件，已经去掉注释和空行
     */
    JackTokenizer(List<String> file){
        this.file = file;
        tokenList = new ArrayList<>();
        //将文件内容分析为字元
        stringTotoken();
    }

    private void stringTotoken(){
        for (int i=0;i<file.size();i++){
            String s = file.get(i);
            parseJackCode(s);
        }
    }

    private void parseJackCode(String code){

        // 组合所有的模式
        String combinedPattern = String.format(
                "(%s)|(%s)|(%s)|(%s)",
                STRING_PATTERN, INTEGER_PATTERN, SYMBOLS, IDENTIFIER_PATTERN
        );

        Pattern pattern = Pattern.compile(combinedPattern);
        Matcher matcher = pattern.matcher(code);

        while (matcher.find()) {
            String token = matcher.group();
            System.out.println("token: "+token);
            tokenList.add(token);
        }
    }

    public boolean hasMoreTokens(){
        int n = tokenList.size();
        if (tokenListIndex < n-1){
            return true;
        }
        return false;
    }

    public void advance(){
        if (hasMoreTokens()){
            tokenListIndex++;
        }
    }

    public void decIndex(){
        tokenListIndex--;
    }


    public tokenType tokenType(){
        String currentToken = tokenList.get(tokenListIndex);
        if (currentToken.matches(STRING_PATTERN)) {
            return tokenType.STRING_CONST;// 字符串常量
        } else if (currentToken.matches(INTEGER_PATTERN)) {
           return tokenType.INT_CONST; // 整数常量
        } else if (currentToken.matches(SYMBOLS)) {
            return tokenType.SYMBOL; // 符号
        } else if (KEYWORDS.contains(currentToken)) {
            return tokenType.KEYWORD; // 关键字
        } else if (currentToken.matches(IDENTIFIER_PATTERN)) {
            return tokenType.IDENTIFIER; // 标识符
        }
        return tokenType.UNKNOW;
    }

    public String keyword(){
        String currentToken = tokenList.get(tokenListIndex);
        if (tokenType()==tokenType.KEYWORD){
            return currentToken;
        }
        return "Error";
    }

    public String symbol(){
        String currentToken = tokenList.get(tokenListIndex);
        if (tokenType()==tokenType.SYMBOL){
            return currentToken;
        }
        System.out.println("Error tokenType != SYMBOL");
        return "Error";
    }

    public String identifier(){
        String currentToken = tokenList.get(tokenListIndex);
        if (tokenType()==tokenType.IDENTIFIER){
            return currentToken;
        }
        System.out.println("Error tokenType != IDENTIFIER");
        return "Error";
    }

    public String intVal(){
        String currentToken = tokenList.get(tokenListIndex);
        if (tokenType()==tokenType.INT_CONST){
            return currentToken;
        }
        System.out.println("Error tokenType != INT_CONST");
        return "Error";
    }

    public String stringVal(){
        String currentToken = tokenList.get(tokenListIndex);
        if (tokenType()==tokenType.STRING_CONST){
            return currentToken;
        }
        System.out.println("Error tokenType != STRING_CONST");
        return "Error";
    }

}
