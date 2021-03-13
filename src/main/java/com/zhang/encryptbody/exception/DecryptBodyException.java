package com.zhang.encryptbody.exception;

/**
 * <p>解密数据失败异常</p>
 * @author zj
 * @date 2020/5/11 14:15
 */
public class DecryptBodyException extends RuntimeException {
    public DecryptBodyException() {
        super("Decrypting data failed.");
    }

    public DecryptBodyException(String message) {
        super(message);
    }
}