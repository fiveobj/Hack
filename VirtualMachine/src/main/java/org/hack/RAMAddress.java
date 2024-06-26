package org.hack;

import static org.hack.tools.*;

/**
 * @Author: WuSM
 * @description: TODO
 * @Date: 2024-06-25 16:58
 * @version: 1.0
 */
public class  RAMAddress {
    public static int sp,local,argument,thisAddress,thatAddress;
    private static int pointer,temp;
    private static int staticAddress;
    private static int spVal;

//    private static Object[] RAM = new Object[24576];

    public static void RAMAddress(){
        sp=0;
        local=1;
        argument=2;
        thisAddress=3;
        thatAddress=4;
        spVal=256;
    }

//    public static void init(){
//        RAM[sp]="256";
//    }

    public static void setPointer(int pointer) {
        if (pointer==3||pointer==4)
        RAMAddress.pointer = pointer;
        else System.out.println("setPointer error!"+pointer);
    }

    public static void setTemp(int temp) {
        if (temp>=5&&temp<=12)
        RAMAddress.temp = temp;
        else System.out.println("setTemp error!"+temp);
    }

    public static void setStaticAddress(int staticAddress) {
        if (staticAddress>=16&&staticAddress<=255)
        RAMAddress.staticAddress = staticAddress;
        else System.out.println("setStaticAddress error!"+staticAddress);
    }

    /*public static int getArgument() {
        return argument;
    }

    public static int getLocal() {
        return local;
    }



    public static int getSp() {
        return sp;
    }



    public static int getThatAddress() {
        return thatAddress;
    }

    public static int getThisAddress() {
        return thisAddress;
    }*/

    public static int getStaticAddress() {
        return staticAddress;
    }

    public static int getPointer() {
        return pointer;
    }
    public static int getSpVal() {
        return spVal;
    }
    public static int getTemp() {
        return temp;
    }
    public static void setSpVal(int spVal) {
        if (spVal>=256&&spVal<=2047)
        RAMAddress.spVal = spVal;
        else System.out.println("setSpVal error!"+spVal);
    }

    /*public static void setRAM(int index, Object value){
        if (isNumeric(index))
        RAM[Integer.parseInt(index)]=value;
    }

    public static String  getRAM(int index){
        if (isNumeric(index))
        return RAM[Integer.parseInt(index)];
        return null;
    }

    public static int size(){
        return RAM.length;
    }*/
}
