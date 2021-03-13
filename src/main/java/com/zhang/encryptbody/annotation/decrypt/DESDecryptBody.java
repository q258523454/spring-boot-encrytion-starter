package com.zhang.encryptbody.annotation.decrypt;

import java.lang.annotation.*;

/**
 * @author zj
 * @date 2020/5/11 14:15
 */
@Target(value = {ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DESDecryptBody {

    String key() default "";

}
