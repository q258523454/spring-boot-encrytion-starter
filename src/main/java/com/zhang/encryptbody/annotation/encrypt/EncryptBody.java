package com.zhang.encryptbody.annotation.encrypt;


import com.zhang.encryptbody.enums.DigestType;
import com.zhang.encryptbody.enums.EncryptEnum;

import java.lang.annotation.*;

/**
 * <p>加密{@link org.springframework.web.bind.annotation.ResponseBody}响应数据，可用于整个控制类或者某个控制器上</p>
 * @author zj
 * @date 2020/5/11 14:15
 */
@Target(value = {ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EncryptBody {

    EncryptEnum value() default EncryptEnum.MD5;

    String key() default "";

    DigestType shaType() default DigestType.SHA256;

}
