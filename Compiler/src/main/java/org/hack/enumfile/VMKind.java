package org.hack.enumfile;

/**
 * @Author: WuSM
 * @description: TODO
 * @Date: 2024-07-25 13:07
 * @version: 1.0
 */
public enum VMKind {
    CONST("constant"),ARG("argument"),LOCAL("local"),STATIC("static"),THIS("this"),THAT("that"),POINTER("pointer"),TEMP("temp");

    private String sge;
    VMKind(String s){
        sge = s;
    }

    public String getSge() {
        return sge;
    }
}
