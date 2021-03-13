package com.zhang.encryptbody.annotation.decrypt;

import com.zhang.encryptbody.enums.DecryptEnum;

import java.lang.annotation.*;

/**
 * <p>解密含有{@link org.springframework.web.bind.annotation.RequestBody}注解的参数请求数据，可用于整个控制类或者某个控制器上</p>
 * @author zj
 * @date 2020/5/11 14:15
 */
@Target(value = {ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DecryptBody {

    DecryptEnum value() default DecryptEnum.AES;

    String key() default "";

}
