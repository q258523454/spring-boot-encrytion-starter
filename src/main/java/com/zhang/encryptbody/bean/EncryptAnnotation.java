package com.zhang.encryptbody.bean;

import com.zhang.encryptbody.enums.EncryptEnum;
import com.zhang.encryptbody.enums.DigestType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>加密注解信息</p>
 * @author zj
 * @date 2020/5/11 14:15
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EncryptAnnotation {

    private EncryptEnum encryptEnum;

    private String key;

    private DigestType digestType;

}
