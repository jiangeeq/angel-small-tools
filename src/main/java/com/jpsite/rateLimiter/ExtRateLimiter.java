package com.jpsite.rateLimiter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 限流注解
 * @author jiangpeng
 * @date 2019/11/1617:51
 */
@Target(value = ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExtRateLimiter {
    /*
     * 速率
     */
    int speed() default 1;
    /*
    超时时间
     */
    long timeOut() default 500;
}
