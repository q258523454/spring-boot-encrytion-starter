package com.zhang.encryptbody.util;


/**
 * @Description: 16进制工具
 * @date 2020/5/25 9:21
 */
public enum HexUtil {

    ;

    /**
     * 初始化一个字符数组，用来存放每个16进制字符
     */
    private static final char[] HEX_CHARS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'a', 'b', 'c', 'd', 'e', 'f'};


    /**
     * 二进位组转十六进制
     * @param bytes 二进位组
     * @return 十六进制字符串
     */
    public static String parseByte2HexStr(byte[] bytes) {
        return new String(parseByte2HexChar(bytes));
    }

    /**
     * @Description: 二进位组转十六进制
     * @date 2020/5/25 9:22
     * @param bytes 二进位组
     */
    private static char[] parseByte2HexChar(byte[] bytes) {

        /**
         * 一个byte是八位二进制，也就是2位十六进制字符（2的8次方等于16的2次方）
         * 如果是md5, bytes 的长度固定为16位, 那么返回的 chars固定长度为 32
         * 如果是SHA-256, bytes 的长度固定为32位, 那么返回的 chars固定长度为 64
         */
        char[] chars = new char[bytes.length * 2];
        // 遍历字节数组，通过位运算（位运算效率高），转换成字符放到字符数组中去
        for (int i = 0; i < chars.length; i = i + 2) {
            byte b = bytes[i / 2];
            // >>> 是无符号右移(忽略最高位符号位, 当成0), 高位补0, 下面是取8 bit的高位的4位
            chars[i] = HEX_CHARS[(b >>> 0x4) & 0xf];
            chars[i + 1] = HEX_CHARS[b & 0xf];
        }
        return chars;
    }

    /**
     * @Description: 十六进制字符串转二进位组
     * @date 2020/5/25 9:22
     * @param hexStr 十六进制字符串
     * @return 二进位组
     */
    public static byte[] parseHexStr2Byte(String hexStr) {
        int len = hexStr.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            // 通过位运算（位运算效率高）
            int high = Character.digit(hexStr.charAt(i), 16) << 4;
            int low = Character.digit(hexStr.charAt(i + 1), 16);
            data[i / 2] = (byte) (high + low);
        }
        return data;
    }

}
