package com.zhang.encryptbody.annotation.encrypt;

import com.zhang.encryptbody.enums.DigestType;

import java.lang.annotation.*;

/**
 * @author zj
 * @date 2020/5/11 14:15
 */
@Target(value = {ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SHAEncryptBody {

    DigestType value() default DigestType.SHA256;

}
