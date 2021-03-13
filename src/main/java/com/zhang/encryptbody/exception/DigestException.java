package com.zhang.encryptbody.exception;

/**
 * <p>数据Digest失败异常</p>
 * @author zj
 * @date 2020/5/11 14:15
 */
public class DigestException extends RuntimeException {

    public DigestException() {
        super("Digest data failed");
    }

    public DigestException(String message) {
        super(message);
    }
}