package com.zhang.encryptbody.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>加密数据配置读取类</p>
 * <p>在SpringBoot项目中的application.yml中添加配置信息即可</p>
 * <pre>
 *     base:
 *      encrypt:
 *       aes-key: 12345678 # AES加密秘钥
 *       des-key: 12345678 # DES加密秘钥
 * </pre>
 * @author zj
 * @date 2020/5/11 14:15
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "base.encrypt")
public class EncryptBodyConfig {

    private String aesKey = "";

    private String desKey = "";

    private String privateKey = "";

    private String publicKey = "";

    /**
     * 这里只是指定 body 串的字符编码格式, 与加解密的编码无关。 加解密编码固定是 utf-8
     */
    private String encoding = "UTF-8";

    /**
     * 配置开关
     */
    private boolean open = true;

    /**
     * 是否开启日志
     */
    private boolean debug = false;

    /**
     * TODO: Map<发送方名称,aesKey>
     */
    private Map<String, String> aesKeyMap = new HashMap<>();

    /**
     * TODO: Map<发送方名称,desKey>
     */
    private Map<String, String> desKeyMap = new HashMap<>();


}
