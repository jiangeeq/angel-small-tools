package com.jpsite.sensitiveJson;

import java.util.HashMap;
import java.util.Map;

/**
 * @author jiangpeng
 * @Description 脱敏规则
 */
public class SensitivityConstants {

    public static final Map<String, SensitiveTypeEnum> SENSITIVITY_RULES = new HashMap<>();

    static {
        /** 真实姓名 */
        SENSITIVITY_RULES.put("name", SensitiveTypeEnum.NAME);
        SENSITIVITY_RULES.put("*Name", SensitiveTypeEnum.NAME);
        /** 身份证 */
        SENSITIVITY_RULES.put("*IdCard", SensitiveTypeEnum.ID_CARD);
        SENSITIVITY_RULES.put("idCard", SensitiveTypeEnum.ID_CARD);
        /** 手机号 */
        SENSITIVITY_RULES.put("*Phone", SensitiveTypeEnum.MOBILE_PHONE);
        SENSITIVITY_RULES.put("*phone", SensitiveTypeEnum.MOBILE_PHONE);
        SENSITIVITY_RULES.put("phone", SensitiveTypeEnum.MOBILE_PHONE);
        SENSITIVITY_RULES.put("*Mobile", SensitiveTypeEnum.MOBILE_PHONE);
        SENSITIVITY_RULES.put("mobile", SensitiveTypeEnum.MOBILE_PHONE);
        /** 邮箱 */
        SENSITIVITY_RULES.put("*Email", SensitiveTypeEnum.EMAIL);
        SENSITIVITY_RULES.put("email", SensitiveTypeEnum.EMAIL);
        /** 密码 */
        SENSITIVITY_RULES.put("passWord", SensitiveTypeEnum.PASSWORD);
        SENSITIVITY_RULES.put("password", SensitiveTypeEnum.PASSWORD);
        SENSITIVITY_RULES.put("*Password", SensitiveTypeEnum.PASSWORD);
        SENSITIVITY_RULES.put("*PassWord", SensitiveTypeEnum.PASSWORD);
        /** 银行卡 */
        SENSITIVITY_RULES.put("*CardNo", SensitiveTypeEnum.CARD_NO);
    }
}
