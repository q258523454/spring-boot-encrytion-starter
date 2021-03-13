package com.zhang.encryptbody.util;

import com.zhang.encryptbody.util.HexUtil;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;


/**
 * @Description:
 *              算法模式: ECB
 *              补码方式: PKCS5Padding
 *              数据块: 默认128位
 *              加解密串编码: Base64 (相当于2次加密)
 *              字符集: utf-8
 * @date 2020/5/25 9:19
 */

public enum AESUtil {

    ;

    private static final String AES = "AES";


    /**
     * @Description: 加密
     * @date 2020/5/25 9:19
     * @param src 目标字符串
     * @param encryptionKey 密钥
     */
    public static String encrypt(String src, String encryptionKey) {
        try {
            // "算法/模式/补码方式"
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            Key secretKey = makeSecretKey(encryptionKey);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encrypted = cipher.doFinal(src.getBytes(StandardCharsets.UTF_8));
            // 此处使用BASE64做转码功能，同时能起到2次加密的作用。
            return new BASE64Encoder().encodeBuffer(encrypted);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * @Description: 解密
     * @date 2020/5/25 9:19
     * @param src 目标字符串
     * @param encryptionKey 密钥
     */
    public static String decrypt(String src, String encryptionKey) {
        String decrypted = "";
        try {
            Cipher cipher = Cipher.getInstance(AES);
            cipher.init(Cipher.DECRYPT_MODE, makeSecretKey(encryptionKey));
            BASE64Decoder base64Decoder = new BASE64Decoder();
            decrypted = new String(cipher.doFinal(base64Decoder.decodeBuffer(src)));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
        return decrypted;
    }

    /**
     * @Description: 将密钥转换成Key类型
     * @date 2020/5/25 9:20
     * @param encryptionKey 密钥
     * @return java.security.Key
     * @throws
     */
    public static Key makeSecretKey(String encryptionKey) {
        try {
            KeyGenerator generator = KeyGenerator.getInstance("AES");
            // 默认也是 SHA1
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            // 可选 128,192,256
            generator.init(128, random);
            byte[] keyBytes = encryptionKey.getBytes(StandardCharsets.UTF_8);
            SecretKeySpec key = new SecretKeySpec(keyBytes, AES);
            return key;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @Description: 生成长度为32(满足256位)的AES密钥
     * @date 2020/5/25 9:20
     */
    public static String genKey() {
        try {
            KeyGenerator generator = KeyGenerator.getInstance("AES");
            // 默认也是 SHA1
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            // 可选 128,192,256
            generator.init(128, random);
            SecretKey secretKey = generator.generateKey();
            byte[] encoded = secretKey.getEncoded();
            String encodeBuffer = HexUtil.parseByte2HexStr(encoded);
            return encodeBuffer;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }
}
