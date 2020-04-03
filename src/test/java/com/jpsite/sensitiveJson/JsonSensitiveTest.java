package com.jpsite.sensitiveJson;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;

/**
 * @author jiangpeng
 * @date 2020/2/2817:22
 */
@Slf4j
public class JsonSensitiveTest {
    private final static ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    static {
        OBJECT_MAPPER.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        OBJECT_MAPPER.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        OBJECT_MAPPER.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        OBJECT_MAPPER.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
        OBJECT_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    @Test
    public void toJsonTest() throws JsonProcessingException {
        BusinessOrderReq obj = new BusinessOrderReq().setAccountName("彭于晏").setBankCardNo("1111111111111");
        val map = new HashMap<>(5);
        map.put("obj", obj);

        val baseMap = new HashMap<>(5);
        baseMap.put("smsCode", "12321321");
        baseMap.put("seqNo", "123123213");
        baseMap.put("phone", "12312312312312");

        log.info("map普通对象{} 原内容{}", JsonSensitive.toJson(baseMap), OBJECT_MAPPER.writeValueAsString(baseMap));
        log.info("map复杂对象{} 原内容{}", JsonSensitive.toJson(map), OBJECT_MAPPER.writeValueAsString(map));
        log.info("普通对象{} 原内容{}", JsonSensitive.toJson(obj), OBJECT_MAPPER.writeValueAsString(obj));
        log.info("集合数组对象{} 原内容{}", JsonSensitive.toJson(OBJECT_MAPPER.writeValueAsString(ImmutableList.of(obj, obj))), OBJECT_MAPPER.writeValueAsString(ImmutableList.of(obj, obj)));

        BusinessOrderReq obj2 = new BusinessOrderReq().setAccountName("刘德华").setBankCardNo("222222222222");
        val map2 = new HashMap<>(5);
        map2.put("obj2", obj2);

        Result<BusinessOrderReq> resultObj = Result.buildSuccessResult(obj2);
        Result<HashMap<Object, Object>> resultMap = Result.buildSuccessResult(map2);
        Result<List> resultList = Result.buildSuccessResult(ImmutableList.of(obj2, obj2));

        log.info("复杂对象obj{} 原内容{}", JsonSensitive.toJson(resultObj), OBJECT_MAPPER.writeValueAsString(resultObj));
        log.info("复杂对象map{} 原内容{}", JsonSensitive.toJson(resultMap), OBJECT_MAPPER.writeValueAsString(resultMap));
        log.info("复杂对象list{} 原内容{}", JsonSensitive.toJson(resultList), OBJECT_MAPPER.writeValueAsString(resultList));
    }
}
