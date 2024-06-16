package org.hack;

import static org.hack.tools.mergeByteArraysToString;

/**
 * @Author: WuSM
 * @description: TODO
 * @Date: 2024-06-14 21:45
 * @version: 1.0
 */
public class test {
    public static void main(String[] args) {
        Byte[] array1 = { 72, 101, 108, 108, 111 }; // "Hello"
        Byte[] array2 = { 32, 87, 111, 114, 108, 100 }; // " World"
        Byte[] array3 = { 33 }; // "!"

        String result = mergeByteArraysToString(array1, array2, array3);
        System.out.println("Merged String: " + result);
    }
}
