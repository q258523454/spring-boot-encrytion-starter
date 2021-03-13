package com.zhang.encryptbody.exception;

/**
 * <p>加密数据失败异常</p>
 * @author zj
 * @date 2020/5/11 14:15
 */
public class EncryptBodyException extends RuntimeException {

    public EncryptBodyException() {
        super("Encrypted data failed");
    }

    public EncryptBodyException(String message) {
        super(message);
    }
}