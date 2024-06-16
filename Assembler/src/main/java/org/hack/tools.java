package org.hack;

import java.io.File;
import java.nio.charset.StandardCharsets;

/**
 * @Author: WuSM
 * @description: TODO
 * @Date: 2024-06-14 15:28
 * @version: 1.0
 */
public class tools {
    public static boolean isNumeric(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        try {
            Integer.parseInt(str); // 对于整数
            // 或者使用 Double.parseDouble(str); // 对于浮点数
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static String mergeByteArraysToString(Byte[]... arrays) {
        StringBuilder stringBuilder = new StringBuilder();

        // 遍历每个 byte[] 数组
        for (Byte[] byteArray : arrays) {
            // 遍历每个 byte 数组中的元素
            for (byte b : byteArray) {
                // 将 byte 转换为字符串并追加到 StringBuilder 中
                stringBuilder.append(b);
            }
        }

        // 返回拼接后的字符串
        return stringBuilder.toString();
    }

    public static byte[] toPrimitive(Byte[] array) {
        byte[] primitive = new byte[array.length];
        for (int i = 0; i < array.length; i++) {
            primitive[i] = array[i];
        }
        return primitive;
    }

    public static String getFileNameWithoutExtension(String filePath) {
        // 创建文件对象
        File file = new File(filePath);

        // 获取文件名
        String fileName = file.getName();

        // 查找最后一个点的位置
        int lastDotIndex = fileName.lastIndexOf('.');

        // 如果找到了点，则去掉后缀；否则返回原文件名
        if (lastDotIndex > 0 && lastDotIndex < fileName.length() - 1) {
            return fileName.substring(0, lastDotIndex);
        } else {
            return fileName;
        }
    }
}
