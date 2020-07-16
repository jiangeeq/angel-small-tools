package com.jpsite.sensitiveJson2;


import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import com.fasterxml.jackson.databind.util.Annotations;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * 提供给JsonUtils工具类进行脱敏
 *
 * @author jiangpeng
 */
public class SensitiveSerializerModifier extends BeanSerializerModifier {

    @Override
    public List<BeanPropertyWriter> changeProperties(SerializationConfig config, BeanDescription beanDesc, List<BeanPropertyWriter> beanProperties) {
        String encrypt = "";
        final Annotations classAnnotations = beanDesc.getClassAnnotations();

        if (Objects.nonNull(classAnnotations.get(JsonEncrypt.class))) {
            encrypt = "zhaocaiwai";
        }
        // 循环所有的beanPropertyWriter
        for (BeanPropertyWriter writer : beanProperties) {
            final String propName = writer.getName();
            //正则匹配
            for (Map.Entry<String, SensitiveTypeEnum> entry : SensitivityConstants.SENSITIVITY_RULES.entrySet()) {
                if (isSensitiveField(entry.getKey(), propName)) {
                    //  String finalEncrypt = encrypt;
                    String finalEncrypt = encrypt;
                    writerSensitiveValue(x -> writer.assignSerializer(new SensitiveInfoSerialize(entry.getValue(), finalEncrypt)), null);
                }
            }
        }
        return beanProperties;
    }

    public static <T> T writerSensitiveValue(Consumer<T> consumer, Supplier<T> supplier) {
        if (consumer != null) {
            consumer.accept(null);
            return null;
        }
        return supplier.get();
    }

    public static boolean isSensitiveField(String rule, String propName) {
        int ruleLength = rule.length();
        int propLen = propName.length();
        // 不是脱敏规则范围内
        if (propName.length() < ruleLength) {
            return false;
        }
        int temp = rule.indexOf("*");
        String key;
        String substring;
        if (temp >= 0) {
            if (temp < (ruleLength >> 2)) {
                key = rule.substring(temp + 1, ruleLength);
                substring = propName.substring(propLen - key.length(), propLen);
            } else {
                key = rule.substring(0, temp);
                substring = propName.substring(0, temp);
            }
            if (substring.equals(key)) {
                return true;
            }
        } else if (rule.equals(propName)) {
            return true;
        }
        return false;
    }
}
