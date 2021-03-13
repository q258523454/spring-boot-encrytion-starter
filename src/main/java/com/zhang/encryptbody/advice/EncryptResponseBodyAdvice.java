package com.zhang.encryptbody.advice;

import com.zhang.encryptbody.annotation.encrypt.*;
import com.zhang.encryptbody.bean.EncryptAnnotation;
import com.zhang.encryptbody.config.EncryptBodyConfig;
import com.zhang.encryptbody.enums.DigestType;
import com.zhang.encryptbody.enums.EncryptEnum;
import com.zhang.encryptbody.exception.EncryptBodyException;
import com.zhang.encryptbody.exception.EncryptMethodNotFoundException;
import com.zhang.encryptbody.exception.KeyNotConfiguredException;
import com.zhang.encryptbody.util.AESUtil;
import com.zhang.encryptbody.util.DigestUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.lang.annotation.Annotation;


/**
 * 响应数据的加密处理 <br>
 *     本类只对控制器参数中含有
 *     <strong>{@link org.springframework.web.bind.annotation.ResponseBody}</strong>
 *     或者类上含有
 *     <strong>{@link org.springframework.web.bind.annotation.RestController}</strong>
 *     的注解进行拦截.
 *     处理以下注解
 *      <strong>{@link com.zhang.encryptbody.annotation.encrypt.SHAEncryptBody}
 *      <strong>{@link com.zhang.encryptbody.annotation.encrypt.RSAEncryptBody}
 *      <strong>{@link com.zhang.encryptbody.annotation.encrypt.MD5EncryptBody}
 *      <strong>{@link com.zhang.encryptbody.annotation.encrypt.EncryptBody}
 *      <strong>{@link com.zhang.encryptbody.annotation.encrypt.DESEncryptBody}
 *      <strong>{@link com.zhang.encryptbody.annotation.encrypt.AESEncryptBody}
 *
 * @see ResponseBodyAdvice
 * @author zj
 * @date 2020/5/11 14:15
 */
@Order(1)
@ControllerAdvice
@Slf4j
public class EncryptResponseBodyAdvice implements ResponseBodyAdvice<Object> {

    @Autowired
    private EncryptBodyConfig config;

    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        // 如果配置没有开启, 注解不失效
        if (!config.isOpen()) {
            if (config.isDebug()) {
                log.warn("加解密开关-未开启");
            }
            return false;
        }
        // 类注解
        Annotation[] classAnotations = returnType.getDeclaringClass().getAnnotations();
        if (null != classAnotations && classAnotations.length > 0) {
            for (Annotation annotation : classAnotations) {
                if (annotation instanceof EncryptBody ||
                        annotation instanceof AESEncryptBody ||
                        annotation instanceof DESEncryptBody ||
                        annotation instanceof RSAEncryptBody ||
                        annotation instanceof MD5EncryptBody ||
                        annotation instanceof SHAEncryptBody) {
                    return true;
                }
            }
        }
        // 方法注解
        Annotation[] methodAnnotations = null;
        if (null != returnType.getMethod()) {
            methodAnnotations = returnType.getMethod().getAnnotations();
        }
        if (null != methodAnnotations && methodAnnotations.length > 0) {
            for (Annotation annotation : methodAnnotations) {
                if (annotation instanceof EncryptBody ||
                        annotation instanceof AESEncryptBody ||
                        annotation instanceof DESEncryptBody ||
                        annotation instanceof RSAEncryptBody ||
                        annotation instanceof MD5EncryptBody ||
                        annotation instanceof SHAEncryptBody) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        // 只对String类型加解密
        if (!(body instanceof String)) {
            throw new EncryptBodyException("encrypt data is not type [java.lang.String]");
        }
        response.getHeaders().setContentType(MediaType.TEXT_PLAIN);
        String str = (String) body;
        if (config.isDebug()) {
            log.info("encrypt body is :" + str);
        }
        // 获取类控制器上的加密注解信息
        EncryptAnnotation classAnnotation = this.getClassAnnotation(returnType.getDeclaringClass());
        if (classAnnotation != null) {
            return doEncrypt(str, classAnnotation);
        }
        // 获取方法控制器上的加密注解信息
        EncryptAnnotation methodAnnotation = this.getMethodAnnotation(returnType);
        if (methodAnnotation != null) {
            return doEncrypt(str, methodAnnotation);
        }
        throw new EncryptBodyException();
    }

    /**
     * 获取方法控制器上的加密注解信息
     * @param methodParameter 控制器方法
     * @return 加密注解信息
     */
    private EncryptAnnotation getMethodAnnotation(MethodParameter methodParameter) {
        Annotation[] methodAnnotation = methodParameter.getMethodAnnotations();

        if (null != methodParameter.getMethodAnnotation(EncryptBody.class)) {
            EncryptBody encryptBody = methodParameter.getMethodAnnotation(EncryptBody.class);
            return EncryptAnnotation.builder()
                    .encryptEnum(encryptBody.value())
                    .key(encryptBody.key())
                    .digestType(encryptBody.shaType())
                    .build();
        }
        if (null != methodParameter.getMethodAnnotation(MD5EncryptBody.class)) {
            return EncryptAnnotation.builder()
                    .encryptEnum(EncryptEnum.MD5)
                    .build();
        }
        if (null != methodParameter.getMethodAnnotation(SHAEncryptBody.class)) {
            return EncryptAnnotation.builder()
                    .encryptEnum(EncryptEnum.SHA)
                    .digestType(methodParameter.getMethodAnnotation(SHAEncryptBody.class).value())
                    .build();
        }
        if (null != methodParameter.getMethodAnnotation(DESEncryptBody.class)) {
            return EncryptAnnotation.builder()
                    .encryptEnum(EncryptEnum.DES)
                    .key(methodParameter.getMethodAnnotation(DESEncryptBody.class).key())
                    .build();
        }
        if (null != methodParameter.getMethodAnnotation(AESEncryptBody.class)) {
            return EncryptAnnotation.builder()
                    .encryptEnum(EncryptEnum.AES)
                    .key(methodParameter.getMethodAnnotation(AESEncryptBody.class).key())
                    .build();
        }
        return null;
    }

    /**
     * 获取类控制器上的加密注解信息
     * @param clazz 控制器类
     * @return 加密注解信息
     */
    private EncryptAnnotation getClassAnnotation(Class<?> clazz) {
        Annotation[] annotations = clazz.getDeclaredAnnotations();
        if (annotations != null && annotations.length > 0) {
            for (Annotation annotation : annotations) {
                if (annotation instanceof EncryptBody) {
                    EncryptBody encryptBody = (EncryptBody) annotation;
                    return EncryptAnnotation.builder()
                            .encryptEnum(encryptBody.value())
                            .key(encryptBody.key())
                            .digestType(encryptBody.shaType())
                            .build();
                }
                if (annotation instanceof MD5EncryptBody) {
                    return EncryptAnnotation.builder()
                            .encryptEnum(EncryptEnum.MD5)
                            .build();
                }
                if (annotation instanceof SHAEncryptBody) {
                    return EncryptAnnotation.builder()
                            .encryptEnum(EncryptEnum.SHA)
                            .digestType(((SHAEncryptBody) annotation).value())
                            .build();
                }
                if (annotation instanceof DESEncryptBody) {
                    return EncryptAnnotation.builder()
                            .encryptEnum(EncryptEnum.DES)
                            .key(((DESEncryptBody) annotation).key())
                            .build();
                }
                if (annotation instanceof AESEncryptBody) {
                    return EncryptAnnotation.builder()
                            .encryptEnum(EncryptEnum.AES)
                            .key(((AESEncryptBody) annotation).key())
                            .build();
                }
            }
        }
        return null;
    }


    /**
     * 选择加密方式并进行加密
     * @param returnBody 目标加密字符串
     * @param infoBean 加密信息
     * @return 加密结果
     */
    private String doEncrypt(String returnBody, EncryptAnnotation infoBean) {
        EncryptEnum method = infoBean.getEncryptEnum();
        if (method == null) {
            throw new EncryptMethodNotFoundException();
        }
        if (method == EncryptEnum.MD5) {
            return DigestUtil.INSTANCE.digest(returnBody, DigestType.MD5.getCode());
        }
        if (method == EncryptEnum.SHA) {
            DigestType digestType = infoBean.getDigestType();
            if (null == digestType) {
                digestType = DigestType.SHA256;
            }
            return DigestUtil.INSTANCE.digest(returnBody, DigestType.SHA256.getCode());
        }

        String key = infoBean.getKey();

        if (method == EncryptEnum.DES) {
            if (StringUtils.isEmpty(key) && StringUtils.isEmpty(config.getDesKey())) {
                throw new KeyNotConfiguredException("DES key is not configured");
            }
            // 优先选择注解中的密钥/盐值
            String wKey = !StringUtils.isEmpty(key) ? key : config.getDesKey();
            if (config.isDebug()) {
                log.info("DES加密密钥:" + wKey);
            }
            return AESUtil.encrypt(returnBody, wKey);
        }

        if (method == EncryptEnum.AES) {
            if (StringUtils.isEmpty(key) && StringUtils.isEmpty(config.getAesKey())) {
                throw new KeyNotConfiguredException("AES key is not configured");
            }
            // 优先选择注解中的密钥/盐值
            String wKey = !StringUtils.isEmpty(key) ? key : config.getAesKey();
            if (config.isDebug()) {
                log.info("DES加密密钥:" + wKey);
            }
            return AESUtil.encrypt(returnBody, wKey);
        }
        throw new EncryptBodyException();
    }


}
