package com.jpsite.utils;

import org.springframework.http.HttpHeaders;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.Enumeration;

/**
 * @author jiangpeng
 * @date 2021/5/2817:45
 */
public class ServletUtils {

    private ServletUtils() {

    }

    /**
     * 从给定的HttpServletRequest对象中获取请求头部
     *
     * @param request 请求对象
     * @return 请求头部
     */
    public static HttpHeaders getHttpHeaders(HttpServletRequest request) {
        HttpHeaders httpHeaders = new HttpHeaders();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            Enumeration<String> headerValues = request.getHeaders(headerName);
            while (headerValues.hasMoreElements()) {
                String headerValue = headerValues.nextElement();
                httpHeaders.add(headerName, headerValue);
            }
        }
        return httpHeaders;
    }

    /**
     * 从给定的HttpServletResponse对象中获取响应头部
     *
     * @param response 响应对象
     * @return 响应头部
     */
    public static HttpHeaders getHttpHeaders(HttpServletResponse response) {
        HttpHeaders httpHeaders = new HttpHeaders();
        Collection<String> headerNames = response.getHeaderNames();
        for (String headerName : headerNames) {
            for (String headerValue : response.getHeaders(headerName)) {
                httpHeaders.add(headerName, headerValue);
            }
        }
        return httpHeaders;
    }
}
