package com.zhang.encryptbody.util;

import com.zhang.encryptbody.enums.DigestType;
import com.zhang.encryptbody.exception.DigestException;
import org.springframework.util.DigestUtils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public enum DigestUtil {
    /**
     * 实例
     */
    INSTANCE;


    /**
     * 初始化一个字符数组，用来存放每个16进制字符
     */
    private static final char[] HEX_CHARS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'a', 'b', 'c', 'd', 'e', 'f'};

    /**
     * 字符集
     */
    private static final String CHAR_SET = "UTF-8";


    /**
     *
     * @param input 加密字符
     * @param algorithm MD5,SHA-256,SHA512等算法
     * @return
     * @throws Exception
     */
    public String digest(String input, String algorithm) {
        MessageDigest messageDigest = null;
        byte[] digest = null;
        try {
            // 拿到一个MD5转换器（如果想要 SHA-256 参数换成 ”SHA-256”）
            messageDigest = MessageDigest.getInstance(algorithm);
            // 返回字节数组，元素长度固定为16个
            digest = messageDigest.digest(input.getBytes(CHAR_SET));
            // 字符数组转换成字符串返回
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            e.printStackTrace();
            throw new DigestException();
        }

        return new String(encodeHex(digest));
    }

    /**
     * 对字节进行16进制编码, byte 转换成 char
     * @param bytes
     * @return
     */
    private static char[] encodeHex(byte[] bytes) {
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

    public static void main(String[] args) throws Exception {
        String pwd = "1234";
        // 自定义方法 如果想要 SHA-256 参数换成 ”SHA-256”
        System.out.println(DigestUtil.INSTANCE.digest(pwd, DigestType.MD5.getCode()));
        // 第三方工具方法
        System.out.println(DigestUtils.md5DigestAsHex(pwd.getBytes()));
    }

}
