package org.hack;

/**
 * @Author: WuSM
 * @description: TODO
 * @Date: 2024-07-16 18:02
 * @version: 1.0
 */
public enum xmlParam {

    class_("<class>","</class>"),classVarDec("<classVarDec>","</classVarDec>"),subroutineDec("<subroutineDec>","</subroutineDec>"),parameterList("<parameterList>","</parameterList>"), subroutineBody("<subroutineBody>","</subroutineBody>"),varDec("<varDec>","</varDec>"),
    statements("<statements>","</statements>"),whileStatement("<whileStatement>","</whileStatement>"),ifStatement("<ifStatement>","</ifStatement>"),returnStatement("<returnStatement>","</returnStatement>"),letStatement("<letStatement>","</letStatement>"),doStatement("<doStatement>","</doStatement>"),
    expression("<expression>","</expression>"),term("<term>","</term>"),expressionList("<expressionList>","</expressionList>"),
    keyword("<keyword>","</keyword>"),symbol("<symbol>","</symbol>"),integerConstant("<integerConstant>","</integerConstant>"),stringConstant("<stringConstant>","</stringConstant>"),identifier("<identifier>","</identifier>");
    private final String xmlparamstart,xmlparamend;
    private xmlParam(String xmlparamstart, String xmlparamend){
        this.xmlparamstart = xmlparamstart;
        this.xmlparamend = xmlparamend;
    }

    public String getXmlparamend() {
        return xmlparamend;
    }

    public String getXmlparamstart() {
        return xmlparamstart;
    }
}
