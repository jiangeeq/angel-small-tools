package com.jpsite.utils;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @author jiangpeng
 * @date 2019/12/1819:09
 */
@Component
@Slf4j
public class RestTemplateUtils {
    private static RestTemplate restTemplate;
    private static HttpServletRequest request;

    @Bean
    public RestTemplate restTemplate() {
        val httpRequestFactory = new SimpleClientHttpRequestFactory();
        httpRequestFactory.setConnectTimeout(5000);
        httpRequestFactory.setReadTimeout(5000);
        return new RestTemplate(httpRequestFactory);
    }

    @Autowired
    public void setRestTemplate(RestTemplate restTemplate, HttpServletRequest request) {
        RestTemplateUtils.restTemplate = restTemplate;
        RestTemplateUtils.request = request;
    }


    public static String executeHttpPost(String url, Object req) {
        return executeHttpPost(url, req, String.class);
    }

    public static <T> T executeHttpPost(String url, Object req, Class<T> clazz) {
        val headers = new HttpHeaders();
        val type = MediaType.parseMediaType("application.yml/json; charset=UTF-8");
        headers.setContentType(type);
        headers.add("Accept", MediaType.APPLICATION_JSON.toString());
        val formEntity = new HttpEntity<>(JsonUtils.encode(req), headers);

        val result = execute("executeHttpPost", url, () -> restTemplate.postForObject(url, formEntity, String.class));
        if ("String".equals(clazz.getSimpleName())) {
            return (T) result;
        } else {
            return JsonUtils.decode(result, clazz);
        }
    }


    public static <T> T executeHttpPostFormData(String url, Object req, Class<T> clazz) {
        val headers = getAuthorizationHeader();
        val targetParam = req instanceof Map ? (Map<String, Object>) req : objectToMap(req);
        val map = new LinkedMultiValueMap<String, String>();

        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        targetParam.forEach((k, v) -> map.add(k, String.valueOf(v)));

        val formEntity = new HttpEntity<>(map, headers);
        val result = execute("executeHttpPostFormData", url, () -> restTemplate.postForObject(url, formEntity, String.class));
        if ("String".equals(clazz.getSimpleName())) {
            return (T) result;
        } else {
            return JsonUtils.decode(result, clazz);
        }
    }

    public static <T> T executeHttpGitLabHeaderGet(String url, Class<T> clazz) {
        return executeHttpGitLabHeader(url, clazz, HttpMethod.GET);
    }

    public static <T> T executeHttpGitLabHeader(String url, Class<T> clazz, HttpMethod method) {
        HttpEntity<String> requestEntity = new HttpEntity<>(null, getAuthorizationHeader());
        val result = execute("executeHttpGitLabHeaderGet", url,
                () -> restTemplate.exchange(url, method, requestEntity, String.class).getBody());
        if ("String".equals(clazz.getSimpleName())) {
            return (T) result;
        } else {
            return JsonUtils.decode(result, clazz);
        }
    }

    private static HttpHeaders getAuthorizationHeader() {
        String accessToken = request.getHeader("Authorization");
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("Authorization", accessToken);
        return requestHeaders;
    }

    public static <T> T executeHttpGet(String url, Class<T> clazz) {
        HttpEntity<String> requestEntity = new HttpEntity<>(null);
        val result = execute("executeHttpGet", url, () -> restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class).getBody());
        if ("String".equals(clazz.getSimpleName())) {
            return (T) result;
        } else {
            return JsonUtils.decode(result, clazz);
        }
    }

    public static String executeHttpGet(String url, Object req) {
        val targetParam = req instanceof Map ? (Map<String, Object>) req : objectToMap(req);
        val targetUrl = buildGetUrlByMap(url, targetParam);

        return execute("executeHttpGet", targetUrl, () -> restTemplate.getForObject(targetUrl, String.class, targetParam));
    }

    private static <T> T execute(String methodName, String url, Supplier<T> supplier) {
        log.debug("执行http [{}] 请求url: [{}]", methodName, url);
        final T t = supplier.get();
        log.debug("接收http [{}] 请求url: [{}] 响应成功", methodName, url);
        return t;
    }

    /**
     * 将Object对象里面的属性和值转化成Map对象
     *
     * @param obj
     * @return
     */
    private static Map<String, Object> objectToMap(Object obj) {
        return Arrays.stream(obj.getClass().getDeclaredFields()).peek(field -> field.setAccessible(true)).collect(
                Collectors.toMap(Field::getName, field -> {
                    try {
                        return Optional.ofNullable(field.get(obj)).orElse("");
                    } catch (IllegalAccessException e) {
                        return "";
                    }
                })
        );
    }

    private static String buildGetUrlByMap(final String baseUrl, final Map<?, ?> requestParams) {
        if (requestParams == null || requestParams.isEmpty()) {
            return baseUrl;
        }

        StringBuilder builder = new StringBuilder();
        builder.append(baseUrl);
        builder.append("?");

        requestParams.keySet().forEach(key -> builder.append(key).append("=").append("{").append(key).append("}").append("&"));

        return builder.substring(0, builder.length() - 1);
    }
}
