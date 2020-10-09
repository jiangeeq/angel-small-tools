package com.jpsite.utils;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.util.DigestUtils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 多种签名方式的工具类
 */
@Slf4j
public class SignatureUtils {
    // java 基础类型
    private static final List<String> JAVA_BASIC_TYPE = ImmutableList.of("String", "byte", "int", "short", "long", "float", "double", "char",
            "boolean");
    // java 包装类型
    private static final List<String> JAVA_PACKAGE_TYPE = ImmutableList.of("Byte", "Short", "Integer", "Long", "Float", "Double", "Character",
            "Boolean");

    /**
     * 对参数进行签名
     * @param object 要操作的内容对象
     * @param salt 秘钥/加盐
     * @return sign result
     */
    public static String md5SignatureParamUpperCase(Object object, String salt) {
        return md5SignatureParam(object, salt, true);
    }

    /**
     * 签名参数拼接
     * 把各个参数按param=value拼起来，后面加key=salt值，然后进行md5后UpperCase，
     * sign = MD5(p1=v1&p2=v2&p3=v3&key=SALT_VAL)
     *
     * @param object    参数对象
     * @param salt      加密盐值
     * @param upperCase 是否转大写
     * @return String
     */
    private static String md5SignatureParam(Object object, String salt, boolean upperCase) {
        val map = objectToMap(object);
        if (map == null) {
            return "";
        }
        val md5DigestAsHex = DigestUtils.md5DigestAsHex(paramEqValue(map, salt).getBytes());
        return upperCase ? md5DigestAsHex.toUpperCase() : md5DigestAsHex;
    }

    /**
     * 把map转化为param=value&param=value的格式
     *
     * @return
     */
    private static String paramEqValue(Map<String, Object> map, String salt) {
        StringBuilder builder = new StringBuilder();
        map.keySet().forEach(key -> {
            if (!Objects.isNull(map.get(key)) && !Strings.isNullOrEmpty(map.get(key).toString())) {
                builder.append(key).append("=").append(map.get(key)).append("&");
            }
        });
        if (!Strings.isNullOrEmpty(salt)) {
            builder.deleteCharAt(builder.length() - 1).append(salt).append("&");
        }
        val result = builder.substring(0, builder.length() - 1);
        log.debug("生成的paramValue为: [{}]", result);
        return result;
    }

    /**
     * 把map转化为param+value+param+value的格式
     * +表示字符串连接运算
     *
     * @return
     */
    public static String paramAppendValue(Map<String, Object> map, String salt) {
        StringBuilder builder = new StringBuilder();
        map.keySet().forEach(key -> {
            if (!Objects.isNull(map.get(key)) && !Strings.isNullOrEmpty(map.get(key).toString())) {
                builder.append(key).append(map.get(key));
            }
        });
        if (!Strings.isNullOrEmpty(salt)) {
            builder.append(salt);
        }
        val result = builder.toString();
        log.debug("生成的paramValue为: [{}]", result);
        return result;
    }

    /**
     * 将Object对象里面的属性和值转化成Map对象，并按字段升序排序
     * 如果字段是自定义对象类型，则直接把值转 json 串
     *
     * @param obj
     * @return
     */
    public static Map<String, Object> objectToMap(Object obj) {
        return Arrays.stream(obj.getClass().getDeclaredFields()).sorted(Comparator.comparing(Field::getName))
                .peek(field -> field.setAccessible(true)).collect(
                        Collectors.toMap(Field::getName, field -> {
                            try {
                                val valueType = field.getType().getSimpleName();
                                val isJavaType = JAVA_BASIC_TYPE.contains(valueType) || JAVA_PACKAGE_TYPE.contains(valueType);
                                return isJavaType ? Optional.ofNullable(field.get(obj)).orElse("") : JsonUtils.encode(field.get(obj));
                            } catch (IllegalAccessException e) {
                                return "";
                            }
                        }, (e1, e2) -> e1, LinkedHashMap::new)
                );
    }
}
