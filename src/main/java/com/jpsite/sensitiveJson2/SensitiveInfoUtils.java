package com.jpsite.sensitiveJson2;

import org.apache.commons.lang3.StringUtils;

/**
 * 脱敏工具类
 */
public class SensitiveInfoUtils {
    /**
     * [真实姓名] 显示首位和末尾，其他隐藏。。<例子：刘**上>
     */
    public static String realName(final String realName, String encrypt) {
        if (StringUtils.isBlank(realName)) {
            return "";
        }
        if (StringUtils.isNotBlank(encrypt)) {
            return EncryptUtil.getInstance().AESencode(realName, encrypt);
        }
        return dealString(realName, 1, 0);
    }

    /**
     * [身份证号] 显示最后四位，其他隐藏。共计18位或者15位。<例子：*************5762>
     */
    public static String idCard(final String idCard, String encrypt) {
        if (StringUtils.isBlank(idCard)) {
            return "";
        }
        if (StringUtils.isNotBlank(encrypt)) {
            return EncryptUtil.getInstance().AESencode(idCard, encrypt);
        }
        return dealString(idCard, 3, 4);
    }

    /**
     * [手机号] 显示最后四位，其他隐藏。共计18位或者15位。<例子：*************5762>
     */
    public static String mobilePhone(final String idCard, String encrypt) {
        if (StringUtils.isBlank(idCard)) {
            return "";
        }
        if (StringUtils.isNotBlank(encrypt)) {
            return EncryptUtil.getInstance().AESencode(idCard, encrypt);
        }
        return dealString(idCard, 3, 4);
    }

    /**
     * [邮箱] 显示最后四位，其他隐藏。共计18位或者15位。<例子：*************5762>
     */
    public static String email(final String email, String encrypt) {
        if (StringUtils.isBlank(email)) {
            return "";
        }
        if (StringUtils.isNotBlank(encrypt)) {
            return EncryptUtil.getInstance().AESencode(email, encrypt);
        }
        int index = email.indexOf("@");
        return dealString(email, 3, email.length() - index);
    }

    /**
     * [账号] 显示最后四位，其他隐藏。共计18位或者15位。<例子：*************5762>
     */
    public static String cardNo(final String idCard, String encrypt) {
        if (StringUtils.isBlank(idCard)) {
            return "";
        }
        if (StringUtils.isNotBlank(encrypt)) {
            return EncryptUtil.getInstance().AESencode(idCard, encrypt);
        }
        final String name = StringUtils.left(idCard, 1);
        return StringUtils.rightPad(name, StringUtils.length(idCard), "*");
    }

    /**
     * [密码] 隐藏。<例子：*************>
     */
    public static String password(final String password, String encrypt) {
        if (StringUtils.isBlank(password)) {
            return "";
        }
        if (StringUtils.isNotBlank(encrypt)) {
            return EncryptUtil.getInstance().AESencode(password, encrypt);
        }
        return "*";
    }


    private static String dealString(String str, int headOff, int tailOff) {
        int length = str.length();
        StringBuilder sb = new StringBuilder();
        final String head = StringUtils.left(str, headOff);
        String tail = StringUtils.right(str, tailOff);
        sb.append(head);
        int size = length - (headOff + tailOff);
        if (size > 0) {
            while (size > 0) {
                sb.append("*");
                size--;
            }
        }
        sb.append(tail);
        return sb.toString();
    }


    /**
     * 提供给外部进行直接脱敏处理
     *
     * @param type
     * @param value
     * @return
     */
    public static String sensitiveValue(SensitiveTypeEnum type, String value, String encrypt) {
        switch (type) {
            case NAME: {
                return realName(String.valueOf(value), encrypt);
            }
            case ID_CARD: {
                return idCard(String.valueOf(value), encrypt);
            }
            case MOBILE_PHONE: {
                return mobilePhone(String.valueOf(value), encrypt);
            }
            case EMAIL: {
                return email(String.valueOf(value), encrypt);
            }
            case CARD_NO: {
                return cardNo(String.valueOf(value), encrypt);
            }
            case PASSWORD: {
                return password(String.valueOf(value), encrypt);
            }
            default:
                return String.valueOf(value);
        }

    }
}
