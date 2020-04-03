package com.jpsite.sensitiveJson;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.collect.ImmutableList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author jiangpeng
 * Json脱敏工具类
 */
@Slf4j
public class JsonSensitive {
    private static final List<String> JAVA_BASIC_TYPE = ImmutableList.of("String", "byte", "int", "short", "long", "float", "double", "char",
            "boolean");
    private static final List<String> JAVA_PACKAGE_TYPE = ImmutableList.of("Byte", "Short", "Integer", "Long", "Float", "Double", "Character",
            "Boolean");
    private static ObjectMapper objectMapperSensitivity = new ObjectMapper();

    static {
        //脱敏日志创建
        objectMapperSensitivity.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        objectMapperSensitivity.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapperSensitivity.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        objectMapperSensitivity.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapperSensitivity.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        objectMapperSensitivity.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        objectMapperSensitivity.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        objectMapperSensitivity.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
        //脱敏
        objectMapperSensitivity.setSerializerFactory(objectMapperSensitivity.getSerializerFactory().
                withSerializerModifier(new SensitiveSerializerModifier()));
    }

    /**
     * 对象转Json格式字符串----脱敏处理(包含map)
     *
     * @return String
     */
    public static String toJson(Object propName) {
        try {
            Object newObj = propName.getClass().newInstance();

            if (newObj instanceof Map) {
                ((Map) newObj).putAll((Map) propName);
            } else if (newObj instanceof List) {
                ((List) newObj).addAll((List) propName);
            } else {
                BeanUtils.copyProperties(propName, newObj);
            }

            if (newObj instanceof Map) {
                Map map = (Map) newObj;

                if (!map.isEmpty()) {
                    Set<Map.Entry> set = map.entrySet();
                    for (Map.Entry item : set) {
                        Object key = item.getKey();
                        Object value = item.getValue();
                        if (key instanceof String) {
                            String keyString = key.toString();
                            String s = dealSensitivity(keyString, value);
                            map.put(keyString, s);
                        }
                    }
                }
            }

            return objectMapperSensitivity.writeValueAsString(newObj);
        } catch (Exception e) {
            log.error("json脱敏序列化异常", e);
            return "";
        }
    }

    private static String dealSensitivity(String mapKey, Object mapValue) {
        String valueType = mapValue.getClass().getSimpleName();
        boolean isJavaType = JAVA_BASIC_TYPE.contains(valueType) || JAVA_PACKAGE_TYPE.contains(valueType);
        String value = isJavaType ? mapValue.toString() : toJson(mapValue);

        if(isJavaType) {
            for (Map.Entry<String, SensitiveTypeEnum> entry : SensitivityConstants.SENSITIVITY_RULES.entrySet()) {
                if(SensitiveSerializerModifier.isSensitiveField(entry.getKey(), mapKey)) {
                    return SensitiveSerializerModifier.writerSensitiveValue(null,
                            () -> SensitiveInfoUtils.sensitiveValue(entry.getValue(), value));
                }
            }
        }
        return value;
    }
}
