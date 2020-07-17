package com.jpsite.sensitiveJson2;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableList;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;

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
    public static final String ENCRYPT_KEY = "zhaocaiwai";
    private static ObjectMapper objectMapperSensitivity = new ObjectMapper();

    public static final JavaType StringJavaType = objectMapperSensitivity.getTypeFactory().constructType(
            String.class);
    public static final JavaType StringObjectMap = objectMapperSensitivity.getTypeFactory().constructMapType(HashMap.class, String.class, Object
            .class);
    public static final JavaType StringStringMap = objectMapperSensitivity.getTypeFactory().constructMapType(HashMap.class, String.class, String
            .class);
    public static final JavaType StringStringLinkedMap = objectMapperSensitivity.getTypeFactory().constructMapType(LinkedHashMap.class,
            String.class, String
                    .class);
    public static final JavaType StringBooleanMap = objectMapperSensitivity.getTypeFactory().constructMapType(HashMap.class, String.class, Boolean
            .class);
    public static final JavaType IntegerStringMap = objectMapperSensitivity.getTypeFactory().constructMapType(HashMap.class, Integer.class,
            String.class);
    public static final JavaType ObjectList = objectMapperSensitivity.getTypeFactory().constructCollectionType(ArrayList.class, Object.class);
    public static final JavaType StringList = objectMapperSensitivity.getTypeFactory().constructCollectionType(ArrayList.class, String.class);
    public static final JavaType StringStringStringMap = objectMapperSensitivity.getTypeFactory()
            .constructMapType(HashMap.class, StringJavaType, StringStringMap);
    public static final JavaType StringStringStringStringMap = objectMapperSensitivity.getTypeFactory()
            .constructMapType(HashMap.class, StringJavaType, StringStringStringMap);
    public static final JavaType StringListStringMap = objectMapperSensitivity.getTypeFactory()
            .constructMapType(HashMap.class, StringJavaType, StringList);

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
        return toJson(propName, null);
    }


    /**
     * 对象转Json格式字符串----加密处理(包含map)
     *
     * @return String
     */
    public static String toJson(Object propName, String encrypt) {
        try {
            if (Objects.isNull(propName)) {
                return StringUtils.EMPTY;
            }

            Object newObj = propName.getClass().newInstance();

            if (newObj instanceof Map) {
                ((Map) newObj).putAll((Map) propName);
                ((Map) newObj).put("encrypt", encrypt);
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
                            String valueString = dealSensitivity(keyString, value, encrypt);
                            map.put(keyString, valueString);
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

    /**
     * 将加密后的json串解密为明文数据后转换成目标对象
     *
     * @param json
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T decodeEncrypt(String json, Class<T> clazz) {
        try {
            ObjectNode jsonNode = (ObjectNode) objectMapperSensitivity.readTree(json);
            jsonNode.fields().forEachRemaining(node -> {
                for (Map.Entry<String, SensitiveTypeEnum> entry :
                        SensitivityConstants.SENSITIVITY_RULES.entrySet()) {
                    if (SensitiveSerializerModifier.isSensitiveField(entry.getKey(), node.getKey())) {
                        String value = node.getValue().asText();
                        String valueDecode = EncryptUtil.getInstance().AESdecode(value, ENCRYPT_KEY);
                        jsonNode.put(node.getKey(), valueDecode);
                    }
                }
            });
            return objectMapperSensitivity.readValue(jsonNode.toString(), clazz);
        } catch (Exception e) {
            log.error("json序列化对象失败", e);
            return null;
        }
    }

    /**
     * 将加密后的json串解密为明文数据后转换成目标对象
     *
     * @param json
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T decodeEncrypt(String json, JavaType javaType) {
        try {
            final JsonNode jsonNode = objectMapperSensitivity.readTree(json);
            if (jsonNode instanceof ArrayNode) {
                ArrayNode arrayNode = (ArrayNode) jsonNode;
                arrayNode.forEach(node -> {
                    ObjectNode tempNode = (ObjectNode) node;
                    tempNode.fields().forEachRemaining(x -> {
                        ObjectNode decodeJson = null;
                        try {
                            decodeJson = parseObjectNode(objectMapperSensitivity.writeValueAsString(x));
                        } catch (JsonProcessingException e) {
                            e.printStackTrace();
                        }
                        tempNode.put(x.getKey(), Objects.isNull(decodeJson) ? "" : decodeJson.get(x.getKey()).asText());
                    });
                });
                return objectMapperSensitivity.readValue(arrayNode.toString(), javaType);
            } else {
                ObjectNode decodeValue = parseObjectNode(json);
                return objectMapperSensitivity.readValue(decodeValue.toString(), javaType);
            }
        } catch (Exception e) {
            log.error("json序列化对象失败", e);
            return null;
        }
    }

    // {"obj":{"phone":"DIJOLJSDIJFOEEF","accountName":"DFJOJFEOWFEWFEWF"}}
    // {"key": "KKKOJOJOJOJ"}
    public static ObjectNode parseObjectNode(String json) {
        try {
            ObjectNode jsonNode = (ObjectNode) objectMapperSensitivity.readTree(json);
            jsonNode.fields().forEachRemaining(node -> {
                JsonNode nodeValue = node.getValue();
                // 正则判断
                String jsonRegexp = "\\{[^\\}].*\\}";
                Pattern jsonPattern = Pattern.compile(jsonRegexp);
                boolean condition1 = jsonPattern.matcher(nodeValue.toString()).matches();
                boolean condition2 = jsonPattern.matcher(nodeValue.asText()).matches();

                if (condition1 || condition2) {
                    String jsonObjStr = condition1 ? nodeValue.toString() : nodeValue.asText();
                    ObjectNode decodeValue = parseObjectNode(jsonObjStr);
                    jsonNode.put(node.getKey(), Objects.isNull(decodeValue) ? "" : decodeValue.toString());

                } else if (nodeValue instanceof ArrayNode) {
                    ArrayNode arrayNode = (ArrayNode) nodeValue;
                    arrayNode.forEach(arrNode -> {
                        ObjectNode tempNode = (ObjectNode) arrNode;
                        tempNode.fields().forEachRemaining(x -> {
                            ObjectNode decodeJson = null;
                            try {
                                decodeJson = parseObjectNode(objectMapperSensitivity.writeValueAsString(x));
                            } catch (JsonProcessingException e) {
                                e.printStackTrace();
                            }
                            tempNode.put(x.getKey(), Objects.isNull(decodeJson) ? "" : decodeJson.get(x.getKey()).asText());
                        });
                    });
                } else {
                    for (Map.Entry<String, SensitiveTypeEnum> entry :
                            SensitivityConstants.SENSITIVITY_RULES.entrySet()) {
                        if (SensitiveSerializerModifier.isSensitiveField(entry.getKey(),
                                node.getKey())) {
                            // asText()方法会去除多余的“号 比如：”“5A266E41CC7525D192EB9E21A2479ADA”“
                            String value = nodeValue.asText();
                            String valueDecode = EncryptUtil.getInstance().AESdecode(value,
                                    ENCRYPT_KEY);
                            jsonNode.put(node.getKey(), valueDecode);
                        }
                    }
                }
            });
            return jsonNode;
        } catch (Exception e) {
            return null;
        }
    }


    private static String dealSensitivity(String mapKey, Object mapValue, String encrypt) {
        if (Objects.isNull(mapValue) || StringUtils.isBlank(mapValue.toString())) {
            return "";
        }
        String valueType = mapValue.getClass().getSimpleName();
        boolean isJavaType = JAVA_BASIC_TYPE.contains(valueType) || JAVA_PACKAGE_TYPE.contains(valueType);
        String value = isJavaType ? mapValue.toString() : toJson(mapValue);

        if (isJavaType) {
            for (Map.Entry<String, SensitiveTypeEnum> entry :
                    SensitivityConstants.SENSITIVITY_RULES.entrySet()) {
                if (SensitiveSerializerModifier.isSensitiveField(entry.getKey(), mapKey)) {
                    return SensitiveSerializerModifier.writerSensitiveValue(null,
                            () -> SensitiveInfoUtils.sensitiveValue(entry.getValue(), value,
                                    encrypt));
                }
            }
        }
        return value;
    }
}
