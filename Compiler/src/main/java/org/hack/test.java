package org.hack;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author: WuSM
 * @description: TODO
 * @Date: 2024-07-15 21:00
 * @version: 1.0
 */
public class test {
    // Java关键字
    private static final Set<String> KEYWORDS = new HashSet<>(Arrays.asList(
            "abstract", "assert", "boolean", "break", "byte", "case", "catch", "char", "class", "const", "continue",
            "default", "do", "double", "else", "enum", "extends", "final", "finally", "float", "for", "goto", "if",
            "implements", "import", "instanceof", "int", "interface", "long", "native", "new", "package", "private",
            "protected", "public", "return", "short", "static", "strictfp", "super", "switch", "synchronized", "this",
            "throw", "throws", "transient", "try", "void", "volatile", "while"
    ));

    // 符号和运算符
    private static final String SYMBOLS = "[{}()\\[\\].,;:!?@#%^&*+=|<>/-]";

    // 正则表达式模式
    private static final String STRING_PATTERN = "\"(.*?)\"";
    private static final String INTEGER_PATTERN = "\\b\\d+\\b";
    private static final String IDENTIFIER_PATTERN = "\\b[A-Za-z_][A-Za-z0-9_]*\\b";

    public static List<String> parseJavaCode(String code) {
        List<String> result = new ArrayList<>();

        // 组合所有的模式
        String combinedPattern = String.format(
                "(%s)|(%s)|(%s)|(%s)",
                STRING_PATTERN, INTEGER_PATTERN, SYMBOLS, IDENTIFIER_PATTERN
        );
        System.out.println(combinedPattern);

        Pattern pattern = Pattern.compile(combinedPattern);
        Matcher matcher = pattern.matcher(code);

        while (matcher.find()) {
            String token = matcher.group();
            System.out.println(token);
            if (token.matches(STRING_PATTERN)) {
                result.add(token); // 字符串常量
            } else if (token.matches(INTEGER_PATTERN)) {
                result.add(token); // 整数常量
            } else if (token.matches(SYMBOLS)) {
                result.add(token); // 符号
            } else if (KEYWORDS.contains(token)) {
                result.add(token); // 关键字
            } else if (token.matches(IDENTIFIER_PATTERN)) {
                result.add(token); // 标识符
            }
        }

        return result;
    }

    public static void main(String[] args) {
        String code = "public class Test { int x = 10; String str = \"Hello, World!\"; }";
        List<String> parsedTokens = parseJavaCode(code);

        for (String token : parsedTokens) {
//            System.out.println(token);
        }


        String codes = "public class Example {\n" +
                "    // This is a single line comment\n" +
                "    public static void main(String[] args) {\n" +
                "        /* This is a \n" +
                "           multi-line comment */\n" +
                "        System.out.println(\"Hello, world!\");\n" +
                "        /**\n" +
                "         * This is a documentation comment\n" +
                "         */\n" +
                "    }\n" +
                "}";

        String cleanedCode = removeComments(codes);

        System.out.println("Original Code:\n" + codes);
        System.out.println("\nCode after removing documentation comments:\n" + cleanedCode);
    }

    public static String removeComments(String code) {
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
