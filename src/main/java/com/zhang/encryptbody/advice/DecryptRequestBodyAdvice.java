package com.zhang.encryptbody.advice;

import com.zhang.encryptbody.annotation.decrypt.AESDecryptBody;
import com.zhang.encryptbody.annotation.decrypt.DESDecryptBody;
import com.zhang.encryptbody.annotation.decrypt.DecryptBody;
import com.zhang.encryptbody.annotation.decrypt.RSADecryptBody;
import com.zhang.encryptbody.bean.DecryptAnnotation;
import com.zhang.encryptbody.bean.DecryptHttpInputMessage;
import com.zhang.encryptbody.config.EncryptBodyConfig;
import com.zhang.encryptbody.enums.DecryptEnum;
import com.zhang.encryptbody.exception.DecryptBodyException;
import com.zhang.encryptbody.exception.DecryptMethodNotFoundException;
import com.zhang.encryptbody.exception.KeyNotConfiguredException;
import com.zhang.encryptbody.util.AESUtil;
import com.zhang.encryptbody.util.DESUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * 请求数据的加密信息解密处理 <br>
 *     本类只对控制器参数中含有注解
 *     <strong>{@link org.springframework.web.bind.annotation.RequestBody}</strong>
 *     进行的方法拦截.
 *     处理以下注解
 *     {@link com.zhang.encryptbody.annotation.decrypt.DecryptBody}
 *     {@link com.zhang.encryptbody.annotation.decrypt.AESDecryptBody}
 *     {@link com.zhang.encryptbody.annotation.decrypt.DESDecryptBody}
 *     {@link com.zhang.encryptbody.annotation.decrypt.RSADecryptBody}
 *
 * @see RequestBodyAdvice
 * @author zj
 * @date 2020/5/11 14:15
 */
@Order(1)
@ControllerAdvice
@Slf4j
public class DecryptRequestBodyAdvice implements RequestBodyAdvice {
    @Autowired
    private EncryptBodyConfig config;

    @Override
    public boolean supports(MethodParameter methodParameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        // 如果配置没有开启, 注解不失效

        if (null == config) {
            log.warn("未配置加解密");
            return false;
        }

        if (!config.isOpen()) {
            if (config.isDebug()) {
                log.warn("加解密开关-未开启");
            }
            return false;
        }
        // 类注解
        Annotation[] classAnotations = methodParameter.getDeclaringClass().getAnnotations();
        if (null != classAnotations && classAnotations.length > 0) {
            for (Annotation annotation : classAnotations) {
                if (annotation instanceof DecryptBody ||
                        annotation instanceof AESDecryptBody ||
                        annotation instanceof DESDecryptBody ||
                        annotation instanceof RSADecryptBody) {
                    return true;
                }
            }
        }
        // 方法注解
        Annotation[] methodAnnotations = null;
        if (null != methodParameter.getMethod()) {
            methodAnnotations = methodParameter.getMethod().getAnnotations();
        }
        if (null != methodAnnotations && methodAnnotations.length > 0) {
            for (Annotation annotation : methodAnnotations) {
                if (annotation instanceof DecryptBody ||
                        annotation instanceof AESDecryptBody ||
                        annotation instanceof DESDecryptBody ||
                        annotation instanceof RSADecryptBody) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public Object handleEmptyBody(Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        return body;
    }

    @Override
    public HttpInputMessage beforeBodyRead(HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) throws IOException {
        String body = "";
        try {
            body = IOUtils.toString(inputMessage.getBody(), config.getEncoding());
        } catch (IOException e) {
            throw new DecryptBodyException("Unable to get request body data, check body or request is compatible.");
        }
        if (body == null || StringUtils.isEmpty(body)) {
            throw new DecryptBodyException("The request body is null or empty, Decryption failed.");
        }


        String decryptBody = null;
        DecryptAnnotation methodAnnotation = this.getMethodAnnotation(parameter);
        DecryptAnnotation classAnnotation = this.getClassAnnotation(parameter.getDeclaringClass());

        // 替换Base64中可能将+替换成功空格
        body = body.replaceAll(" ", "+");
        if (config.isDebug()) {
            log.info("body before decrypt :" + body);
        }
        if (methodAnnotation != null) {
            decryptBody = doDecrypt(body, methodAnnotation);
        } else if (classAnnotation != null) {
            decryptBody = doDecrypt(body, classAnnotation);
        }
        if (decryptBody == null) {
            throw new DecryptBodyException("Decryption error, check if the source data is encrypted correctly.");
        }
        if (config.isDebug()) {
            log.info("body after decrypt :" + decryptBody);
        }
        try {
            InputStream inputStream = IOUtils.toInputStream(decryptBody, config.getEncoding());
            // 重点: 一定要 重置 HttpInputMessage中Headers的"Content-Length"值, 因为 Body 内容更改了, value
            // 下面方法等价于: inputMessage.getHeaders().setContentLength(String.valueOf(decryptBody.length()));
            // 注意 "Content-Length" 值是 byte的长度
            byte[] bodyBytes = decryptBody.getBytes();
            inputMessage.getHeaders().set(HttpHeaders.CONTENT_LENGTH, String.valueOf(bodyBytes.length));
            DecryptHttpInputMessage decryptHttpInputMessage = new DecryptHttpInputMessage(inputStream, inputMessage.getHeaders());
            return decryptHttpInputMessage;
        } catch (IOException e) {
            throw new DecryptBodyException("String convert exception. Please check if the format such as encoding is correct.");
        }
    }

    @Override
    public Object afterBodyRead(Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        return body;
    }

    /**
     * 获取方法控制器上的加密注解信息
     * @param methodParameter 方法参数
     * @return 加密注解信息
     */
    private DecryptAnnotation getMethodAnnotation(MethodParameter methodParameter) {
        if (null != methodParameter.getMethodAnnotation(DecryptBody.class)) {
            DecryptBody decryptBody = methodParameter.getMethodAnnotation(DecryptBody.class);
            return DecryptAnnotation.builder()
                    .decryptEnum(decryptBody.value())
                    .key(decryptBody.key())
                    .build();
        }
        if (null != methodParameter.getMethod().getAnnotation(DESDecryptBody.class)) {
            DESDecryptBody desDecryptBody = methodParameter.getMethodAnnotation(DESDecryptBody.class);
            return DecryptAnnotation.builder()
                    .decryptEnum(DecryptEnum.DES)
                    .key(desDecryptBody.key())
                    .build();
        }
        if (null != methodParameter.getMethod().getAnnotation(AESDecryptBody.class)) {
            AESDecryptBody aesDecryptBody = methodParameter.getMethodAnnotation(AESDecryptBody.class);
            return DecryptAnnotation.builder()
                    .decryptEnum(DecryptEnum.AES)
                    .key(aesDecryptBody.key())
                    .build();
        }
        return null;
    }

    /**
     * 获取类控制器上的加密注解信息
     * @param clazz 控制器类
     * @return 加密注解信息
     */
    private DecryptAnnotation getClassAnnotation(Class clazz) {
        Annotation[] annotations = clazz.getDeclaredAnnotations();
        if (annotations != null && annotations.length > 0) {
            for (Annotation annotation : annotations) {
                if (annotation instanceof DecryptBody) {
                    DecryptBody decryptBody = (DecryptBody) annotation;
                    return DecryptAnnotation.builder()
                            .decryptEnum(decryptBody.value())
                            .key(decryptBody.key())
                            .build();
                }
                if (annotation instanceof DESDecryptBody) {
                    return DecryptAnnotation.builder()
                            .decryptEnum(DecryptEnum.DES)
                            .key(((DESDecryptBody) annotation).key())
                            .build();
                }
                if (annotation instanceof AESDecryptBody) {
                    return DecryptAnnotation.builder()
                            .decryptEnum(DecryptEnum.AES)
                            .key(((AESDecryptBody) annotation).key())
                            .build();
                }
            }
        }
        return null;
    }


    /**
     * 选择加密方式并进行解密
     * @param inputBody 目标解密字符串
     * @param decryptAnnotation 加密信息
     * @return 解密结果
     */
    private String doDecrypt(String inputBody, DecryptAnnotation decryptAnnotation) {
        DecryptEnum method = decryptAnnotation.getDecryptEnum();
        if (method == null) {
            throw new DecryptMethodNotFoundException();
        }

        String key = decryptAnnotation.getKey();

        if (method == DecryptEnum.DES) {
            if (StringUtils.isEmpty(key) && StringUtils.isEmpty(config.getDesKey())) {
                throw new KeyNotConfiguredException("DES key is  not configured");
            }
            // 优先选择注解中的密钥/盐值
            String wKey = !StringUtils.isEmpty(key) ? key : config.getDesKey();
            if (config.isDebug()) {
                log.info("DES解密密钥:" + wKey);
            }
            return DESUtil.decrypt(inputBody, wKey);
        }
        if (method == DecryptEnum.AES) {
            if (StringUtils.isEmpty(key) && StringUtils.isEmpty(config.getAesKey())) {
                throw new KeyNotConfiguredException("AES key is  not configured");
            }
            // 优先选择注解中的密钥/盐值
            String wKey = !StringUtils.isEmpty(key) ? key : config.getAesKey();
            if (config.isDebug()) {
                log.info("AES解密密钥:" + wKey);
            }
            return AESUtil.decrypt(inputBody, wKey);
        }
        throw new DecryptBodyException();
    }


}
