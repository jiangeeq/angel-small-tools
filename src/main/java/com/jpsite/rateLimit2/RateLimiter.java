package com.jpsite.rateLimit2;

import java.util.concurrent.TimeUnit;

/**
 * 限流工作对象接口
 */
public interface RateLimiter {

    /**
     * 校验给定的key是否还在限制值内
     *
     * @param key   唯一标识
     * @param limit 限制值
     * @return 是否还是有效的（在限制值内）
     */
    boolean validate(String key, long limit);

    /**
     * 递增给定的key并校验是否还在限制值内
     *
     * @param key          唯一标识
     * @param limit        限制值
     * @param interval     限制时间范围值
     * @param intervalUnit 限制时间范围单位
     * @return 是否还是有效的（在限制值内）
     */
    boolean incrAndValidate(String key, long limit, long interval, TimeUnit intervalUnit);
}
