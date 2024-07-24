package org.hack;

import java.util.ArrayList;
import java.util.List;

import static org.hack.xmlParam.*;

/**
 * @Author: WuSM
 * @description: TODO
 * @Date: 2024-07-16 16:53
 * @version: 1.0
 */
public class CompilationEngine {
    JackTokenizer jackTokenizer;
    List<String> outputList = new ArrayList<>();
    private static final String OP = "[&*+=|<>/-]";
    CompilationEngine(JackTokenizer jackTokenizer){
        this.jackTokenizer = jackTokenizer;
        CompileClass();
    }

    /**
     * 程序的初始入口
     * 'class' className '{' classVarDec* subroutineDec* '}'
    * */
    public void CompileClass(){
        String s = jackTokenizer.keyword();
//        System.out.println(s);
        if (s.equals("CLASS")){
            outputList.add(class_.getXmlparamstart());
            CompileTokenKey("CLASS");
            jackTokenizer.advance();
            CompileTokenIden();
            jackTokenizer.advance();
            CompileTokenSymbol("{");
            jackTokenizer.advance();
            String s1 = jackTokenizer.keyword();
            while (s1.equals("STATIC")||s1.equals("FIELD")){
                CompileClassVarDec();
                jackTokenizer.advance();
                s1 = jackTokenizer.keyword();
            }
            s1 = jackTokenizer.keyword();
            while (s1.equals("CONSTRUCTOR")||s1.equals("FUNCTION")||s1.equals("METHOD")) {
                CompileSubroutineDec();
                jackTokenizer.advance();
                s1 = jackTokenizer.keyword();
            }
            CompileTokenSymbol("}");
            outputList.add(class_.getXmlparamend());

        }else {
            System.out.println("!s.equals(\"CLASS\")");
        }
    }

    /**
     * 类变量匹配
     * ('static' | 'field‘) type varName (',' varName)* ';’
     * */
    public void CompileClassVarDec(){
        String s = jackTokenizer.keyword();
        if (s.equals("STATIC")){
            outputList.add(classVarDec.getXmlparamstart());
            CompileTokenKey("STATIC");
            jackTokenizer.advance();
            if ((jackTokenizer.tokenType()==tokenType.KEYWORD && (jackTokenizer.keyword().equals("INT") || jackTokenizer.keyword().equals("CHAR") || jackTokenizer.keyword().equals("BOOLEAN")))||jackTokenizer.tokenType()==tokenType.IDENTIFIER)
                CompileType();
            jackTokenizer.advance();
            CompileTokenIden();
            jackTokenizer.advance();
            String s1 = jackTokenizer.symbol();
            while (s1.equals(",")){
                CompileTokenSymbol(",");
                jackTokenizer.advance();
                CompileTokenIden();
                jackTokenizer.advance();
                s1 = jackTokenizer.symbol();
            }
            CompileTokenSymbol(";");
            outputList.add(classVarDec.getXmlparamend());
        }
        else if (s.equals("FIELD")){
            outputList.add(classVarDec.getXmlparamstart());
            CompileTokenKey("FIELD");
            jackTokenizer.advance();
            if ((jackTokenizer.tokenType()==tokenType.KEYWORD && (jackTokenizer.keyword().equals("INT") || jackTokenizer.keyword().equals("CHAR") || jackTokenizer.keyword().equals("BOOLEAN")))||jackTokenizer.tokenType()==tokenType.IDENTIFIER)
                CompileType();
            jackTokenizer.advance();
            CompileTokenIden();
            jackTokenizer.advance();
            String s1 = jackTokenizer.symbol();
            while (s1.equals(",")){
                CompileTokenSymbol(",");
                jackTokenizer.advance();
                CompileTokenIden();
                jackTokenizer.advance();
                s1 = jackTokenizer.symbol();
            }
            CompileTokenSymbol(";");
            outputList.add(classVarDec.getXmlparamend());
        }else {
            System.out.println("s != static|field");
        }
    }

    /**
     * type 要判断是否是int char boolean classname
     * ‘int' | 'char' | 'boolean' | className
     */
    public void CompileType(){
        String s = jackTokenizer.keyword();
        if (!s.equals("Error")){
            CompileTokenKey(s);
        }else {
            CompileTokenIden();
        }
    }

    /**
     * 方法、函数或构造函数匹配
     * ('constructor' | 'function' | 'method") ('void' | 'type') subroutineName '(' parameterList ')' subroutineBody
     **/
    public void CompileSubroutineDec(){
        outputList.add(subroutineDec.getXmlparamstart());
        String s = jackTokenizer.keyword();
        CompileTokenKey(s);
        jackTokenizer.advance();
        if ((jackTokenizer.tokenType()==tokenType.KEYWORD && (jackTokenizer.keyword().equals("INT") || jackTokenizer.keyword().equals("CHAR") || jackTokenizer.keyword().equals("BOOLEAN")))||jackTokenizer.tokenType()==tokenType.IDENTIFIER)
            CompileType();
        else CompileTokenKey("VOID");
        jackTokenizer.advance();
        CompileTokenIden();
        jackTokenizer.advance();
        CompileTokenSymbol("(");
        jackTokenizer.advance();
        CompileParameterList();
        jackTokenizer.advance();
        CompileTokenSymbol(")");
        jackTokenizer.advance();
        CompileSubroutineBody();
        outputList.add(subroutineDec.getXmlparamend());
    }

    /**
     * 函数体匹配
     * '{' varDec* statements '}'
     */
    public void CompileSubroutineBody(){
        outputList.add(subroutineBody.getXmlparamstart());
        CompileTokenSymbol("{");
        jackTokenizer.advance();
        String s = jackTokenizer.keyword();
        while (s.equals("VAR")){
            CompileVarDec();
            jackTokenizer.advance();
            s= jackTokenizer.keyword();
        }
        CompileStatements();
        jackTokenizer.advance();
        CompileTokenSymbol("}");
        outputList.add(subroutineBody.getXmlparamend());
    }

    /**
     * 参数列表匹配，可能为空，不包含 "()"
     * (expression (',' expression)*)?
     */
    public void CompileParameterList(){
        outputList.add(parameterList.getXmlparamstart());
        while ((jackTokenizer.tokenType()==tokenType.KEYWORD && (jackTokenizer.keyword().equals("INT") || jackTokenizer.keyword().equals("CHAR") || jackTokenizer.keyword().equals("BOOLEAN")))||jackTokenizer.tokenType()==tokenType.IDENTIFIER){
            CompileType();
            jackTokenizer.advance();
            CompileTokenIden();
            jackTokenizer.advance();
            String s = jackTokenizer.symbol();
            while (s.equals(",")){
                CompileTokenSymbol(",");
                jackTokenizer.advance();
                if ((jackTokenizer.tokenType()==tokenType.KEYWORD && (jackTokenizer.keyword().equals("INT") || jackTokenizer.keyword().equals("CHAR") || jackTokenizer.keyword().equals("BOOLEAN")))||jackTokenizer.tokenType()==tokenType.IDENTIFIER)
                    CompileType();
                jackTokenizer.advance();
                CompileTokenIden();
                jackTokenizer.advance();
                s = jackTokenizer.symbol();
            }
        }
        jackTokenizer.decIndex();
        outputList.add(parameterList.getXmlparamend());
    }

    /**
     * Var声明匹配
     * 'var' type varName (',' varName)* ';'
     */
    public void CompileVarDec(){
        outputList.add(varDec.getXmlparamstart());
        String s = jackTokenizer.keyword();
        if (s.equals("VAR")){
            CompileTokenKey("VAR");
            jackTokenizer.advance();
            if ((jackTokenizer.tokenType()==tokenType.KEYWORD && (jackTokenizer.keyword().equals("INT") || jackTokenizer.keyword().equals("CHAR") || jackTokenizer.keyword().equals("BOOLEAN")))||jackTokenizer.tokenType()==tokenType.IDENTIFIER)
                CompileType();
            jackTokenizer.advance();
            CompileTokenIden();
            jackTokenizer.advance();
            s = jackTokenizer.symbol();
            while (s.equals(",")){
                CompileTokenSymbol(",");
                jackTokenizer.advance();
                CompileTokenIden();
                jackTokenizer.advance();
                s = jackTokenizer.symbol();
            }
            CompileTokenSymbol(";");
        }
        outputList.add(varDec.getXmlparamend());
    }

    /**
     * 一系列语句，不包括大括号 "{}"
     * statement*
     * letStatement | ifStatement | whileStatement | doStatement | returnStatement
     */
    public void CompileStatements(){
        outputList.add(statements.getXmlparamstart());
        String s = jackTokenizer.keyword();
        while (s.equals("LET")||s.equals("IF")||s.equals("WHILE")||s.equals("DO")||s.equals("RETURN")){
            switch (s){
                case "LET":
                    CompileLet();
                    break;
                case "IF":
                    CompileIf();
                    break;
                case "WHILE":
                    CompileWhile();
                    break;
                case "DO":
                    CompileDo();
                    break;
                case "RETURN":
                    CompileReturn();
                    break;
            }
            jackTokenizer.advance();
            s = jackTokenizer.keyword();
        }
        jackTokenizer.decIndex();
        outputList.add(statements.getXmlparamend());
    }

    /**
     * 'do' subroutineCall ';'
     */
    public void CompileDo(){
        outputList.add(doStatement.getXmlparamstart());
        CompileTokenKey("DO");
        jackTokenizer.advance();
        CompileSubroutineCall();
        jackTokenizer.advance();
        CompileTokenSymbol(";");
        outputList.add(doStatement.getXmlparamend());
    }

    /**
     * 子程序调用匹配
     * subroutineName '(' expressionList ')' | (className | varName) '.' subroutineName '(' expressionList ')'
     */
    public void CompileSubroutineCall(){
        CompileTokenIden();
        jackTokenizer.advance();
        String s = jackTokenizer.symbol();
        if (s.equals("(")){
            CompileTokenSymbol("(");
            jackTokenizer.advance();
            CompileExpressionList();
            jackTokenizer.advance();
            CompileTokenSymbol(")");
        }else if (s.equals(".")){
            CompileTokenSymbol(".");
            jackTokenizer.advance();
            CompileTokenIden();
            jackTokenizer.advance();
            CompileTokenSymbol("(");
            jackTokenizer.advance();
            CompileExpressionList();
            jackTokenizer.advance();
            CompileTokenSymbol(")");
        }

    }

    /**
     * 'let' varName ('[' expression ']')? '=' expression ';'
     */
    public void CompileLet(){
        outputList.add(letStatement.getXmlparamstart());
        CompileTokenKey("LET");
        jackTokenizer.advance();
        CompileTokenIden();
        jackTokenizer.advance();
        String s = jackTokenizer.symbol();
        while (s.equals("[")){
            CompileTokenSymbol("[");
            jackTokenizer.advance();
            CompileExpression();
            jackTokenizer.advance();
            CompileTokenSymbol("]");
            jackTokenizer.advance();
            s = jackTokenizer.symbol();
        }

        if (s.equals("=")){
            CompileTokenSymbol("=");
            jackTokenizer.advance();
            CompileExpression();
            jackTokenizer.advance();
            CompileTokenSymbol(";");
        }
        outputList.add(letStatement.getXmlparamend());
    }

    /**
     * 'while' '(' expression ')' '{' statements '}'
     */
    public void CompileWhile(){
        outputList.add(whileStatement.getXmlparamstart());
        CompileTokenSymbol("WHILE");
        jackTokenizer.advance();
        CompileTokenSymbol("(");
        jackTokenizer.advance();
        CompileExpression();
        jackTokenizer.advance();
        CompileTokenSymbol(")");
        jackTokenizer.advance();
        CompileTokenSymbol("{");
        jackTokenizer.advance();
        CompileStatements();
        jackTokenizer.advance();
        CompileTokenSymbol("}");
        outputList.add(whileStatement.getXmlparamend());
    }

    /**
     * 'return' expression? ';'
     */
    public void CompileReturn(){
        outputList.add(returnStatement.getXmlparamstart());
        CompileTokenKey("RETURN");
        jackTokenizer.advance();
        if (jackTokenizer.tokenType()==tokenType.SYMBOL&&jackTokenizer.symbol().equals(";")){
            CompileTokenSymbol(";");
        }else {
            CompileExpression();
            jackTokenizer.advance();
            CompileTokenSymbol(";");
        }
        outputList.add(returnStatement.getXmlparamend());
    }

    /**
     * 'if' '(' expression ')' '{' statements '}' ('else' '{' statements '}' )?
     */
    public void CompileIf(){
        outputList.add(ifStatement.getXmlparamstart());
        CompileTokenKey("IF");
        jackTokenizer.advance();
        CompileTokenSymbol("(");
        jackTokenizer.advance();
        CompileExpression();
        jackTokenizer.advance();
        CompileTokenSymbol(")");
        jackTokenizer.advance();
        CompileTokenSymbol("{");
        jackTokenizer.advance();
        CompileStatements();
        jackTokenizer.advance();
        CompileTokenSymbol("}");
        jackTokenizer.advance();
        if (jackTokenizer.tokenType()==tokenType.KEYWORD&& jackTokenizer.keyword().equals("ELSE")){
            CompileTokenKey("ELSE");
            jackTokenizer.advance();
            CompileTokenSymbol("{");
            jackTokenizer.advance();
            CompileStatements();
            jackTokenizer.advance();
            CompileTokenSymbol("}");
        }else {
            jackTokenizer.decIndex();
        }
        outputList.add(ifStatement.getXmlparamend());
    }

    /**
     * 表达式匹配
     * term (op term)*
     */
    public void CompileExpression(){
        outputList.add(expression.getXmlparamstart());
        CompileTerm();
        jackTokenizer.advance();
        if (jackTokenizer.tokenType()==tokenType.SYMBOL){
            String s = jackTokenizer.symbol();
            while(s.matches(OP)){
                CompileTokenSymbol(s);
                jackTokenizer.advance();
                CompileTerm();
                jackTokenizer.advance();
                s = jackTokenizer.symbol();
            }
            jackTokenizer.decIndex();
        }
        outputList.add(expression.getXmlparamend());
    }

    /**
     * integerConstant | stringConstant | keywordConstant | varName | varName '[' expression ']' | subroutineCall | '(' expression ')' | unaryOp term
     */
    public void CompileTerm(){
        outputList.add(term.getXmlparamstart());
        if (jackTokenizer.tokenType()==tokenType.SYMBOL&&jackTokenizer.symbol().matches("[-~]")){
            String s = jackTokenizer.symbol();
            CompileTokenSymbol(s);
            jackTokenizer.advance();
            CompileTerm();
        }else if (jackTokenizer.tokenType()==tokenType.SYMBOL&&jackTokenizer.symbol().equals("(")){
            CompileTokenSymbol("(");
            jackTokenizer.advance();
            CompileExpression();
            jackTokenizer.advance();
            CompileTokenSymbol(")");
        }else {
            jackTokenizer.advance();
            if (jackTokenizer.tokenType()==tokenType.SYMBOL&&(jackTokenizer.symbol().matches("[.(\\[]"))){
                String s = jackTokenizer.symbol();
                jackTokenizer.decIndex();
                if (s.equals("(")||s.equals(".")){
                    CompileSubroutineCall();
                }else if (s.equals("[")){
                    CompileTokenIden();
                    jackTokenizer.advance();
                    CompileTokenSymbol("[");
                    jackTokenizer.advance();
                    CompileExpression();
                    jackTokenizer.advance();
                    CompileTokenSymbol("]");
                }
            }else {
                jackTokenizer.decIndex();

                switch (jackTokenizer.tokenType()){
                    case INT_CONST -> {
                        CompileTokenIntCon();
                    }
                    case STRING_CONST -> {
                        CompileTokenStrCon();
                    }case KEYWORD -> {
                        CompileTokenKey(jackTokenizer.keyword());
                    }case IDENTIFIER -> {
                        CompileTokenIden();
                    }case SYMBOL -> {
                        //是符号的话只能是')'
                        jackTokenizer.decIndex();
                    }
                }

            }
        }
        outputList.add(term.getXmlparamend());
    }

    /**
     * 由逗号分隔的表达式列表，可能为空
     * (expression(',' expression)*)?
     */
    public void CompileExpressionList(){
        outputList.add(expressionList.getXmlparamstart());

        if (!jackTokenizer.symbol().equals(")")){
            CompileExpression();
            jackTokenizer.advance();
            String s = jackTokenizer.symbol();
            while (s.equals(",")){
                CompileTokenSymbol(",");
                jackTokenizer.advance();
                CompileExpression();
                jackTokenizer.advance();
                s = jackTokenizer.symbol();
            }
        }
        jackTokenizer.decIndex();
        outputList.add(expressionList.getXmlparamend());
    }

    public void CompileTokenKey(String key) {
        outputList.add(keyword.getXmlparamstart()+key+keyword.getXmlparamend());
    }
    public void CompileTokenSymbol(String symbol) {
        if (symbol.equals("<"))symbol="&lt;";
        if (symbol.equals(">"))symbol="&gt;";
        outputList.add(xmlParam.symbol.getXmlparamstart()+symbol+xmlParam.symbol.getXmlparamend());
    }
    public void CompileTokenIntCon() {
        outputList.add(integerConstant.getXmlparamstart()+jackTokenizer.intVal()+integerConstant.getXmlparamend());
    }
    public void CompileTokenStrCon() {
        outputList.add(stringConstant.getXmlparamstart()+jackTokenizer.stringVal().replaceAll("^\"|\"$", "")+stringConstant.getXmlparamend());
    }
    public void CompileTokenIden() {
        outputList.add(identifier.getXmlparamstart()+jackTokenizer.identifier()+identifier.getXmlparamend());
    }

    public List<String> getOutputList() {
        return outputList;
    }
}
