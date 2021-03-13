package com.zhang.encryptbody.exception;

/**
 * <p>加密方式未找到或未定义异常</p>
 * @author zj
 * @date 2020/5/11 14:15
 */
public class EncryptMethodNotFoundException extends RuntimeException {

    public EncryptMethodNotFoundException() {
        super("Encryption method is not defined.");
    }

    public EncryptMethodNotFoundException(String message) {
        super(message);
    }
}
