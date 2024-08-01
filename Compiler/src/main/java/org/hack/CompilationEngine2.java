package org.hack;


import org.hack.enumfile.VMKind;
import org.hack.enumfile.symbolKind;
import org.hack.enumfile.tokenType;

import java.util.ArrayList;
import java.util.List;

import static org.hack.enumfile.VMCommand.*;
import static org.hack.enumfile.VMKind.*;
import static org.hack.enumfile.symbolKind.FIELD;
import static org.hack.enumfile.symbolKind.VAR;
import static org.hack.tools.isNumeric;
import static org.hack.xmlParam.*;

/**
 * @Author: WuSM
 * @description: TODO
 * @Date: 2024-07-16 16:53
 * @version: 1.0
 */
public class CompilationEngine2 {
    JackTokenizer jackTokenizer;
    List<String> outputList = new ArrayList<>();
    private static final String OP = "[&*+=|<>/-]";

    private SymbolTable classSymbolTable;//类的作用区间
    private SymbolTable funcSymbolTable;//程序的作用区间
    private VMWriter vmWriter;
    private String className;
    private int ifIndex=0, whileIndex=0;
    CompilationEngine2(JackTokenizer jackTokenizer){
        this.jackTokenizer = jackTokenizer;
        classSymbolTable = new SymbolTable();
        vmWriter = new VMWriter();

        CompileClass();
    }

    /**
     * 程序的初始入口
     * 'class' className '{' classVarDec* subroutineDec* '}'
     **/
    public void CompileClass(){
        String s = jackTokenizer.keyword();
//        System.out.println(s);
        if (s.equals("class")){
            outputList.add(class_.getXmlparamstart());
            CompileTokenKey("class");
            jackTokenizer.advance();

            //获取className
            className = jackTokenizer.identifier();

            CompileTokenIden();
            jackTokenizer.advance();
            CompileTokenSymbol("{");
            jackTokenizer.advance();
            String s1 = jackTokenizer.keyword();
            while (s1.equals("static")||s1.equals("field")){
                CompileClassVarDec();
                jackTokenizer.advance();
                s1 = jackTokenizer.keyword();
            }
            s1 = jackTokenizer.keyword();
            while (s1.equals("constructor")||s1.equals("function")||s1.equals("method")) {
                CompileSubroutineDec();
                jackTokenizer.advance();
                s1 = jackTokenizer.keyword();
            }
            CompileTokenSymbol("}");
            outputList.add(class_.getXmlparamend());

        }else {
            System.out.println("!s.equals(\"class\")");
        }
    }

    /**
     * 类变量匹配
     * ('static' | 'field‘) type varName (',' varName)* ';’
     * */
    public void CompileClassVarDec(){
        String s = jackTokenizer.keyword();
        if (s.equals("static")){

            String staticName,staticType="";

            outputList.add(classVarDec.getXmlparamstart());
            CompileTokenKey("static");
            jackTokenizer.advance();
            if ((jackTokenizer.tokenType()== tokenType.KEYWORD && (jackTokenizer.keyword().equals("int") || jackTokenizer.keyword().equals("char") || jackTokenizer.keyword().equals("boolean")))||jackTokenizer.tokenType()==tokenType.IDENTIFIER){
                CompileType();

                if (jackTokenizer.tokenType()==tokenType.KEYWORD)staticType = jackTokenizer.keyword();
                else staticType = jackTokenizer.identifier();

            }

            jackTokenizer.advance();

            staticName = jackTokenizer.identifier();
            classSymbolTable.Define(staticName, staticType, symbolKind.STATIC);

            CompileTokenIden();
            jackTokenizer.advance();
            String s1 = jackTokenizer.symbol();
            while (s1.equals(",")){
                CompileTokenSymbol(",");
                jackTokenizer.advance();

                staticName = jackTokenizer.identifier();
                classSymbolTable.Define(staticName, staticType, symbolKind.STATIC);

                CompileTokenIden();
                jackTokenizer.advance();
                s1 = jackTokenizer.symbol();
            }
            CompileTokenSymbol(";");
            outputList.add(classVarDec.getXmlparamend());
        }
        else if (s.equals("field")){

            String fieldName,fieldType="";

            outputList.add(classVarDec.getXmlparamstart());
            CompileTokenKey("field");
            jackTokenizer.advance();
            if ((jackTokenizer.tokenType()==tokenType.KEYWORD && (jackTokenizer.keyword().equals("int") || jackTokenizer.keyword().equals("char") || jackTokenizer.keyword().equals("boolean")))||jackTokenizer.tokenType()==tokenType.IDENTIFIER){
                CompileType();

                if (jackTokenizer.tokenType()==tokenType.KEYWORD)fieldType = jackTokenizer.keyword();
                else fieldType = jackTokenizer.identifier();

            }

            jackTokenizer.advance();

            fieldName = jackTokenizer.identifier();
            classSymbolTable.Define(fieldName, fieldType, FIELD);

            CompileTokenIden();
            jackTokenizer.advance();
            String s1 = jackTokenizer.symbol();
            while (s1.equals(",")){
                CompileTokenSymbol(",");
                jackTokenizer.advance();

                fieldName = jackTokenizer.identifier();
                classSymbolTable.Define(fieldName, fieldType, FIELD);

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
        //进入新的作用域区间
        funcSymbolTable = classSymbolTable.startSubroutine();
        String funcName,funcType;

        outputList.add(subroutineDec.getXmlparamstart());
        String s = jackTokenizer.keyword();
        CompileTokenKey(s);
        jackTokenizer.advance();
        if ((jackTokenizer.tokenType()==tokenType.KEYWORD && (jackTokenizer.keyword().equals("int") || jackTokenizer.keyword().equals("char") || jackTokenizer.keyword().equals("boolean")))||jackTokenizer.tokenType()==tokenType.IDENTIFIER)
        {
            CompileType();

            if (jackTokenizer.tokenType()==tokenType.KEYWORD)funcType = jackTokenizer.keyword();
            else funcType = jackTokenizer.identifier();
        }
        else {
            CompileTokenKey("void");
            funcType = "void";
        }

        jackTokenizer.advance();

        //子程序名称
        funcName = className+"."+jackTokenizer.identifier();

        CompileTokenIden();
        jackTokenizer.advance();
        CompileTokenSymbol("(");
        jackTokenizer.advance();
        int paramCount = CompileParameterList();//参数个数
        jackTokenizer.advance();
        CompileTokenSymbol(")");
        jackTokenizer.advance();
        CompileSubroutineBody(s, funcName);

        //如果返回类型是void，则0作为返回值
        if (funcType.equals("void")){
            vmWriter.writePush(CONST,0);
        }

        outputList.add(subroutineDec.getXmlparamend());
    }

    /**
     * 函数体匹配
     * '{' varDec* statements '}'
     */
    public void CompileSubroutineBody(String funType, String funName){
        outputList.add(subroutineBody.getXmlparamstart());
        CompileTokenSymbol("{");
        jackTokenizer.advance();
        String s = jackTokenizer.keyword();
        while (s.equals("var")){
            CompileVarDec();
            jackTokenizer.advance();
            s= jackTokenizer.keyword();
        }

        int lclCount = funcSymbolTable.VarCount(VAR);
        // 翻译成 vm
        // 函数定义 并为局部变量初始化为0
        switch (funType){
            case "constructor":
                //开辟空间，并保存this指针
                vmWriter.writeFunction(funName, lclCount);
                vmWriter.writePush(CONST, lclCount);
                vmWriter.writeCall("Memory.alloc",1);//返回是this指针
                vmWriter.writePop(POINTER,0);

                break;
            case "function":
                vmWriter.writeFunction(funName, lclCount);
                break;
            case "method":
                vmWriter.writeFunction(funName, lclCount+1);
                break;
        }

        CompileStatements();
        jackTokenizer.advance();
        CompileTokenSymbol("}");
        outputList.add(subroutineBody.getXmlparamend());
    }

    /**
     * 参数列表匹配，可能为空，不包含 "()"
     * ((type varName)(','type varName)*)?
     */
    public int CompileParameterList(){
        String paramName,paramType;
        int paramCount = 0;
        outputList.add(parameterList.getXmlparamstart());
        while ((jackTokenizer.tokenType()==tokenType.KEYWORD && (jackTokenizer.keyword().equals("int") || jackTokenizer.keyword().equals("char") || jackTokenizer.keyword().equals("boolean")))||jackTokenizer.tokenType()==tokenType.IDENTIFIER){

            if (jackTokenizer.tokenType()==tokenType.KEYWORD)paramType=jackTokenizer.keyword();
            else paramType=jackTokenizer.identifier();

            paramCount++;
            CompileType();
            jackTokenizer.advance();

            paramName=jackTokenizer.identifier();
            classSymbolTable.Define(paramName, paramType, symbolKind.ARG);

            CompileTokenIden();
            jackTokenizer.advance();
            String s = jackTokenizer.symbol();
            while (s.equals(",")){
                paramCount++;
                CompileTokenSymbol(",");
                jackTokenizer.advance();
                if ((jackTokenizer.tokenType()==tokenType.KEYWORD && (jackTokenizer.keyword().equals("int") || jackTokenizer.keyword().equals("char") || jackTokenizer.keyword().equals("boolean")))||jackTokenizer.tokenType()==tokenType.IDENTIFIER)
                {
                    CompileType();

                    if (jackTokenizer.tokenType()==tokenType.KEYWORD)paramType=jackTokenizer.keyword();
                    else paramType=jackTokenizer.identifier();

                }
                jackTokenizer.advance();

                paramName=jackTokenizer.identifier();
                classSymbolTable.Define(paramName, paramType, symbolKind.ARG);

                CompileTokenIden();
                jackTokenizer.advance();
                s = jackTokenizer.symbol();
            }
        }
        jackTokenizer.decIndex();
        outputList.add(parameterList.getXmlparamend());
        return paramCount;
    }

    /**
     * Var声明匹配
     * 'var' type varName (',' varName)* ';'
     */
    public void CompileVarDec(){
        String varName, varType="";
        outputList.add(varDec.getXmlparamstart());
        String s = jackTokenizer.keyword();
        if (s.equals("var")){
            CompileTokenKey("var");
            jackTokenizer.advance();
            if ((jackTokenizer.tokenType()==tokenType.KEYWORD && (jackTokenizer.keyword().equals("int") || jackTokenizer.keyword().equals("char") || jackTokenizer.keyword().equals("boolean")))||jackTokenizer.tokenType()==tokenType.IDENTIFIER)
            {
                CompileType();

                if (jackTokenizer.tokenType()==tokenType.KEYWORD)varType=jackTokenizer.keyword();
                else varType=jackTokenizer.identifier();

            }
            jackTokenizer.advance();

            //程序变量加入符号表
            varName=jackTokenizer.identifier();
            funcSymbolTable.Define(varName, varType, symbolKind.VAR);

            CompileTokenIden();
            jackTokenizer.advance();
            s = jackTokenizer.symbol();
            while (s.equals(",")){
                CompileTokenSymbol(",");
                jackTokenizer.advance();

                varName=jackTokenizer.identifier();
                funcSymbolTable.Define(varName, varType, symbolKind.VAR);

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
        while (s.equals("let")||s.equals("if")||s.equals("while")||s.equals("do")||s.equals("return")){
            switch (s){
                case "let":
                    CompileLet();
                    break;
                case "if":
                    CompileIf();
                    break;
                case "while":
                    CompileWhile();
                    break;
                case "do":
                    CompileDo();
                    break;
                case "return":
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
        CompileTokenKey("do");
        jackTokenizer.advance();
        CompileSubroutineCall();
        jackTokenizer.advance();
        CompileTokenSymbol(";");
        outputList.add(doStatement.getXmlparamend());

        //do 没有返回值，需要先弹出0
        vmWriter.writePop(TEMP,0);
    }

    /**
     * 子程序调用匹配
     * subroutineName '(' expressionList ')' | (className | varName) '.' subroutineName '(' expressionList ')'
     */
    public void CompileSubroutineCall(){
        //如果是对象的话，就是该类的变量，会被保存在符号表里
        String subroutineName = jackTokenizer.identifier();
        String subroutineType="";
        int subroutineIndex=0;
        boolean isMethod = false;//程序调用是否是方法
        //先看子程序作用区间有没有，再看父区间有没有
        if (funcSymbolTable.ishas(subroutineName)){
            subroutineType = funcSymbolTable.TypeOf(subroutineName);
            subroutineIndex = funcSymbolTable.IndexOf(subroutineName);
            isMethod=true;
        }
        if (funcSymbolTable.ishas(subroutineName)){
            subroutineType = funcSymbolTable.TypeOf(subroutineName);
            subroutineIndex = funcSymbolTable.IndexOf(subroutineName);
            isMethod = true;
        }

        CompileTokenIden();
        jackTokenizer.advance();
        String s = jackTokenizer.symbol();
        if (s.equals("(")){ // 好像没有这种情况
            CompileTokenSymbol("(");
            jackTokenizer.advance();
            int expressCount = CompileExpressionList();
            jackTokenizer.advance();
            CompileTokenSymbol(")");

            vmWriter.writeCall(className+subroutineName, expressCount);

        }else if (s.equals(".")){ //其他类的函数/构造函数 对象的方法
            if (isMethod)vmWriter.writePush(THIS, subroutineIndex);//如果是方法，将对象的this推入栈中
            CompileTokenSymbol(".");
            jackTokenizer.advance();

            subroutineName = subroutineName+"."+jackTokenizer.identifier();

            CompileTokenIden();
            jackTokenizer.advance();
            CompileTokenSymbol("(");
            jackTokenizer.advance();
            int expressCount = CompileExpressionList();
            jackTokenizer.advance();
            CompileTokenSymbol(")");
            if(isMethod)vmWriter.writeCall(subroutineName, expressCount+1);
            else vmWriter.writeCall(subroutineName, expressCount);
        }

    }

    /**
     * 'let' varName ('[' expression ']')? '=' expression ';' 基本类型/对象/数组
     * 如果varName是数组
     * 1.根据varName获取地址（得到数组的基址），推入到栈中
     * 2.经过第一个 expression 的操作之后，栈顶应该保存了 偏址
     * 3.add 计算 目的地址 = 基址 + 偏址
     * 4.将地址保存到 that 指针中
     * 5.计算第二个 expression， 即赋值结果
     * 6.将结果推入 that 指针
     * 如果是基本类型或者对象  对象不会出现 对象.字段 = xxx
     * 直接将结果推入到目的地址中
     */
    public void CompileLet(){
        boolean isArray = false;//判断是否是数组

        outputList.add(letStatement.getXmlparamstart());
        CompileTokenKey("let");
        jackTokenizer.advance();

        // 获取基址+分类
        String varName = jackTokenizer.identifier();//目标地址
        int baseIndex = 0;
        symbolKind varKind = null;
        if (funcSymbolTable.ishas(varName)){
            baseIndex = funcSymbolTable.IndexOf(varName);
            varKind = funcSymbolTable.kindOf(varName);
        }else {
            baseIndex = classSymbolTable.IndexOf(varName);
            varKind = classSymbolTable.kindOf(varName);
        }



        CompileTokenIden();
        jackTokenizer.advance();
        String s = jackTokenizer.symbol();
        if (s.equals("[")){ //如果是数组 就用that保存数组的最终地址

            isArray = true;
            //将基址推入栈中
            switch (varKind){
                case STATIC -> {
                    vmWriter.writePush(STATIC, baseIndex);
                }
                case FIELD -> {
                    vmWriter.writePush(THIS, baseIndex);
                }
                case ARG -> {
                    vmWriter.writePush(ARG, baseIndex);
                }
                case VAR -> {
                    vmWriter.writePush(LOCAL, baseIndex);
                }
            }

            CompileTokenSymbol("[");
            jackTokenizer.advance();
            CompileExpression();       //经过expression计算后 结果(偏址)被保存到栈顶
            jackTokenizer.advance();
            CompileTokenSymbol("]");
            jackTokenizer.advance();
            s = jackTokenizer.symbol();

            vmWriter.writeArithmetic(ADD);//计算目的地址
            //现在栈顶为目的地址,将其保存到that中
            vmWriter.writePop(POINTER,1);
        }



        if (s.equals("=")){
            CompileTokenSymbol("=");
            jackTokenizer.advance();
            CompileExpression();
            jackTokenizer.advance();
            CompileTokenSymbol(";");
            //此时栈顶为 expression 的结果，将结果保存到 目标地址中
            if (isArray)vmWriter.writePop(THAT, 0);
            else {
                switch (varKind){
                    case STATIC -> {
                        vmWriter.writePop(STATIC, baseIndex);
                    }
                    case FIELD -> {
                        vmWriter.writePop(THIS, baseIndex);
                    }
                    case ARG -> {
                        vmWriter.writePop(ARG, baseIndex);
                    }
                    case VAR -> {
                        vmWriter.writePop(LOCAL, baseIndex);
                    }
                }
            }
        }
        outputList.add(letStatement.getXmlparamend());
    }

    /**
     * 'while' '(' expression ')' '{' statements '}'
     */
    public void CompileWhile(){
        outputList.add(whileStatement.getXmlparamstart());

        //while的开始标签
        vmWriter.writeLabel("WHILE_LABLE_START_"+whileIndex);

        CompileTokenSymbol("while");
        jackTokenizer.advance();
        CompileTokenSymbol("(");
        jackTokenizer.advance();
        CompileExpression();

        //此时栈顶是判断
        vmWriter.writeIF("WHILE_LABLE_END_"+whileIndex);

        jackTokenizer.advance();
        CompileTokenSymbol(")");
        jackTokenizer.advance();
        CompileTokenSymbol("{");

        //while 的执行体
        jackTokenizer.advance();
        CompileStatements();
        jackTokenizer.advance();
        CompileTokenSymbol("}");

        //先无条件跳转到开头，再有开头判断要不要跳转到结束标签
        vmWriter.writeGoto("WHILE_LABLE_START_"+whileIndex);
        //while的结束标签
        vmWriter.writeLabel("WHILE_LABLE_END_"+whileIndex);
        whileIndex++;

        outputList.add(whileStatement.getXmlparamend());
    }

    /**
     * 'return' expression? ';'
     */
    public void CompileReturn(){
        outputList.add(returnStatement.getXmlparamstart());
        CompileTokenKey("return");
        jackTokenizer.advance();
        if (jackTokenizer.tokenType()==tokenType.SYMBOL&&jackTokenizer.symbol().equals(";")){
            CompileTokenSymbol(";");
        }else {
            CompileExpression();
            jackTokenizer.advance();
            CompileTokenSymbol(";");
        }
        outputList.add(returnStatement.getXmlparamend());

        vmWriter.writeReturn();
    }

    /**
     * 'if' '(' expression ')' '{' statements '}' ('else' '{' statements '}' )?
     */
    public void CompileIf(){
        outputList.add(ifStatement.getXmlparamstart());
        CompileTokenKey("if");
        jackTokenizer.advance();
        CompileTokenSymbol("(");
        jackTokenizer.advance();
        CompileExpression();

        //此时栈顶是判断元素 非0跳过if的执行语句，执行else(如果有)及之后的语句
        vmWriter.writeIF("IF_LABLE_ELSE_"+ifIndex);

        jackTokenizer.advance();
        CompileTokenSymbol(")");
        jackTokenizer.advance();
        CompileTokenSymbol("{");
        jackTokenizer.advance();
        CompileStatements();
        jackTokenizer.advance();
        CompileTokenSymbol("}");
        jackTokenizer.advance();

        //执行完if跳过else
        vmWriter.writeGoto("IF_LABLE_END_"+ifIndex);
        //else(如果有)及之后的语句加入跳转标签
        vmWriter.writeLabel("IF_LABLE_ELSE_"+ifIndex);

        if (jackTokenizer.tokenType()==tokenType.KEYWORD&& jackTokenizer.keyword().equals("else")){

            CompileTokenKey("else");
            jackTokenizer.advance();
            CompileTokenSymbol("{");
            jackTokenizer.advance();
            CompileStatements();
            jackTokenizer.advance();
            CompileTokenSymbol("}");
        }else {
            jackTokenizer.decIndex();
        }

        //跳过else语句加入跳转标签
        vmWriter.writeLabel("IF_LABLE_END_"+ifIndex);
        ifIndex++;

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

                //加入OP
                VMWriterOP(s);

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
        if (jackTokenizer.tokenType()==tokenType.SYMBOL&&jackTokenizer.symbol().matches("[-~]")){ // unaryOp term
            String s = jackTokenizer.symbol();
            CompileTokenSymbol(s);
            jackTokenizer.advance();
            CompileTerm();//操作数已被放入栈顶

            //加入OP
            switch (s){
                case "-":
                    vmWriter.writeArithmetic(NEG);
                    break;
                case "~":
                    vmWriter.writeArithmetic(NOT);
                    break;
            }

        }else if (jackTokenizer.tokenType()==tokenType.SYMBOL&&jackTokenizer.symbol().equals("(")){ // '(' expression ')'
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
                if (s.equals("(")||s.equals(".")){ // subroutineCall 子程序调用 函数/方法/构造函数
                    CompileSubroutineCall();
                }else if (s.equals("[")){ // varName '[' expression ']' 数组调用，将值放入栈内
                    CompileTokenIden();

                    //获取数组的基址
                    String varName = jackTokenizer.identifier();
                    int varIndex = 0;
                    symbolKind varKind = null;
                    if (funcSymbolTable.ishas(varName)){
                        varIndex = funcSymbolTable.IndexOf(varName);
                        varKind = funcSymbolTable.kindOf(varName);
                    }
                    if (classSymbolTable.ishas(varName)){
                        varIndex = classSymbolTable.IndexOf(varName);
                        varKind = classSymbolTable.kindOf(varName);
                    }
                    switch (varKind){
                        case STATIC -> {
                            vmWriter.writePush(STATIC, varIndex);
                        }
                        case FIELD -> {
                            vmWriter.writePush(THIS, varIndex);
                        }
                        case ARG -> {
                            vmWriter.writePush(ARG, varIndex);
                        }
                        case VAR -> {
                            vmWriter.writePush(LOCAL, varIndex);
                        }
                    }

                    jackTokenizer.advance();
                    CompileTokenSymbol("[");
                    jackTokenizer.advance();
                    CompileExpression();

                    //此时偏址已经在栈顶
                    vmWriter.writeArithmetic(ADD);//计算目的地址
                    vmWriter.writePop(POINTER,1);//将地址放到THAT
                    vmWriter.writePush(THAT,0);//将值放入堆栈

                    jackTokenizer.advance();
                    CompileTokenSymbol("]");
                }
            }else {
                jackTokenizer.decIndex();

                switch (jackTokenizer.tokenType()){
                    case INT_CONST -> {
                        CompileTokenIntCon();
                        vmWriter.writePush(CONST, isNumeric(jackTokenizer.intVal())?Integer.parseInt(jackTokenizer.intVal()):-1);
                    }
                    case STRING_CONST -> {
                        CompileTokenStrCon();
                        String s = jackTokenizer.stringVal();
                        int n = s.length();
                        char[] chars = s.toCharArray();
                        vmWriter.writePush(CONST, n);
                        vmWriter.writeCall(" String.new", 1);

                        //vm对字符串如何处理？ 用内置函数创建字符串，栈中最后保留String的基址
                    }case KEYWORD -> {
                        CompileTokenKey(jackTokenizer.keyword());
                        //关键字可能是 true/false/null/this
                        String varNameKey = jackTokenizer.keyword();
                        switch (varNameKey){
                            case "true"->{
                                vmWriter.writePush(CONST, 1);
                                vmWriter.writeArithmetic(NEG);
                            }
                            case "false","null"->{
                                vmWriter.writePush(CONST, 0);
                            }
                            case "this"->{
                                //还不知道咋操作？把this的值推入栈中？
                                vmWriter.writePush(THIS,0);
                            }
                        }

                    }case IDENTIFIER -> {
                        CompileTokenIden();

                        //标识符
                        String varNameIde = jackTokenizer.identifier();
                        symbolKind varKindIde = null;
                        int varIndexIde = 0;
                        if (funcSymbolTable.ishas(varNameIde)){
                            varKindIde = funcSymbolTable.kindOf(varNameIde);
                            varIndexIde = funcSymbolTable.IndexOf(varNameIde);
                        }else {
                            varKindIde = classSymbolTable.kindOf(varNameIde);
                            varIndexIde = classSymbolTable.IndexOf(varNameIde);
                        }
                        switch (varKindIde){
                            case STATIC -> {
                                vmWriter.writePush(STATIC, varIndexIde);
                            }
                            case FIELD -> {
                                vmWriter.writePush(THIS, varIndexIde);
                            }
                            case ARG -> {
                                vmWriter.writePush(ARG, varIndexIde);
                            }
                            case VAR -> {
                                vmWriter.writePush(LOCAL, varIndexIde);
                            }
                        }

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
    public int CompileExpressionList(){
        int expressCount = 0;
        outputList.add(expressionList.getXmlparamstart());

        if (!jackTokenizer.symbol().equals(")")){//参数列表是否为空
            expressCount++;
            CompileExpression();
            jackTokenizer.advance();
            String s = jackTokenizer.symbol();
            while (s.equals(",")){
                CompileTokenSymbol(",");
                expressCount++;
                jackTokenizer.advance();
                CompileExpression();
                jackTokenizer.advance();
                s = jackTokenizer.symbol();
            }
        }
        jackTokenizer.decIndex();
        outputList.add(expressionList.getXmlparamend());

        return expressCount;
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

    public List<String> getVMWriter(){return vmWriter.getAns();}

    public void VMWriterOP(String s){
        switch (s){
            case "+":
                vmWriter.writeArithmetic(ADD);
                break;
            case "-":
                vmWriter.writeArithmetic(SUB);
                break;
            case "*":

                break;
            case "/":

                break;
            case "&":
                vmWriter.writeArithmetic(AND);
                break;
            case "|":
                vmWriter.writeArithmetic(OR);
                break;
            case "<":
                vmWriter.writeArithmetic(LT);
                break;
            case ">":
                vmWriter.writeArithmetic(GT);
                break;
            case "=":

                break;
        }
    }
}
