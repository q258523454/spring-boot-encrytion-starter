package com.zhang.encryptbody.util;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;


/**
 *
 * 算法模式: ECB
 * 补码方式: PKCS5Padding
 * 数据库: 默认128位
 * 加解密串编码: Base64 (相当于2次加密)
 * 字符集: utf-8
 */
public enum DESUtil {
    ;

    private static final String DES = "DES";


    /**
     * @Description: 加密
     * @date 2020/5/6 16:52
     * @param src 目标字符串
     * @param encryptionKey 加密盐值/密钥
     * @throws
     */
    public static String encrypt(String src, String encryptionKey) {
        try {
            // DES 加密盐值/密钥 的字节长度必须为 8
            if (null == encryptionKey || 8 != encryptionKey.getBytes().length) {
                throw new IllegalArgumentException("key byte length must be 8");
            }
            // "算法/模式/补码方式"
            Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, makeKey(encryptionKey));
            byte[] encrypted = cipher.doFinal(src.getBytes(StandardCharsets.UTF_8));
            // 此处使用BASE64做转码功能，同时能起到2次加密的作用。
            return new BASE64Encoder().encodeBuffer(encrypted);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @Description: 解密
     * @date 2020/5/6 16:52
     * @param src 目标字符串
     * @param encryptionKey 解密盐值/密钥
     */
    public static String decrypt(String src, String encryptionKey) {
        String decrypted = "";
        try {
            Cipher cipher = Cipher.getInstance(DES);
            cipher.init(Cipher.DECRYPT_MODE, makeKey(encryptionKey));
            BASE64Decoder base64Decoder = new BASE64Decoder();
            decrypted = new String(cipher.doFinal(base64Decoder.decodeBuffer(src)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return decrypted;
    }

    /**
     * 将密钥盐值转换成Key类型
     * @param encryptionKey 密钥/盐值
     * @return
     */
    public static Key makeKey(String encryptionKey) {
        try {
            KeyGenerator generator = KeyGenerator.getInstance("DES");
            generator.init(56);
            SecretKey secretKey = generator.generateKey();
            byte[] keyBytes = encryptionKey.getBytes(StandardCharsets.UTF_8);
            SecretKeySpec key = new SecretKeySpec(keyBytes, DES);
            return key;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        System.out.println(makeKey("zhang"));
    }

}
