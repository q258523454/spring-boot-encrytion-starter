package com.zhang.encryptbody.bean;

import com.zhang.encryptbody.enums.DecryptEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>解密注解信息</p>
 * @author zj
 * @date 2020/5/11 14:15
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DecryptAnnotation {

    private DecryptEnum decryptEnum;

    private String key;

    /**
     * 发送方名称
     * 根据发送方名称从keyMap中获取对应的Key
     */
    private String senderName;
}
