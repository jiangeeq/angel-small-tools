package com.jpsite.rateLimit2.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 多重{@link RateLimit}注解的存储器
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimits {

    /**
     * {@link RateLimit}注解列表
     *
     * @return RateLimit注解列表
     */
    RateLimit[] value() default {};
}
