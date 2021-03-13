package com.zhang.encryptbody.exception;


import lombok.NoArgsConstructor;

/**
 * <p>未配置KEY运行时异常</p>
 * @author zj
 * @date 2020/5/11 14:15
 */
@NoArgsConstructor
public class KeyNotConfiguredException extends RuntimeException {

    public KeyNotConfiguredException(String message) {
        super(message);
    }
}
