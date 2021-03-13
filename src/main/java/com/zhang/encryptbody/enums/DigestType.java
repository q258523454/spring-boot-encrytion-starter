package com.zhang.encryptbody.enums;

/**
 * <p>SHA加密类型</p>
 * @author zj
 * @date 2020/5/11 14:15
 */
public enum DigestType {

    /**
     * MD5
     */
    MD5("MD5", "算法-MD5"),
    /**
     * SHA224
     */
    SHA224("sha-224", "算法-sha-224"),
    /**
     * SHA256
     */
    SHA256("sha-256", "算法-sha-256"),
    /**
     * SHA384
     */
    SHA384("sha-384", "算法-sha-384"),
    /**
     * SHA512
     */
    SHA512("sha-512", "算法-sha-512"),
    ;

    private String code;
    private String desc;

    DigestType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static DigestType parse(String code) {
        for (DigestType item : values()) {
            if (item.code.equals(code)) {
                return item;
            }
        }
        throw new RuntimeException("无法匹配正确的枚举类型");
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

}
