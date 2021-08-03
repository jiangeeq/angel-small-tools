package com.jpsite.rateLimit2.annotation;

import com.jpsite.rateLimit2.annotation.IncrType;
import com.jpsite.rateLimit2.annotation.RateLimits;

import java.lang.annotation.*;

/**
 * 限流注解，必须配置celebi v1.7.7或以上版本使用，标注在契约接口实现方法上，支持多重注解。
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Repeatable(RateLimits.class)
public @interface RateLimit {

    /**
     * key的别名
     *
     * @return key的别名
     */
    String value() default "";

    /**
     * 限流的唯一标志，支持SpEL表达式
     *
     * @return 唯一标志
     */
    String key() default "";

    /**
     * 限流的优先级，使用多重{@link RateLimit}注解时用于指定顺序。遵从{@link java.util.Comparator}的原则，
     * order值小的优先级更高，相同的order值则随机运行，不保证顺序。
     *
     * @return 限流器的优先级
     */
    int order() default 0;

    /**
     * 计数类型。PRE_INCR，在业务方法执行前计数；POST_INCR，在业务方法执行后计数
     *
     * @return 计数类型
     */
    IncrType incrType() default IncrType.PRE_INCR;

    /**
     * 执行计数的条件表达式，支持SpEL表达式，如果结果为真，则执行计数
     *
     * @return 执行计数的条件表达式
     */
    String incrCondition() default "true";

    /**
     * 限流的最大值，支持配置引用
     *
     * @return 限流的最大值
     */
    String limit() default "1";

    /**
     * 限流的时间范围值，支持配置引用
     *
     * @return 限流的时间范围值
     */
    String interval() default "1";

    /**
     * 限流的时间范围单位，支持配置引用
     *
     * @return 限流的时间范围单位
     */
    String intervalUnit() default "SECONDS";

    /**
     * 降级的方法名，降级方法的参数与原方法一致
     *
     * @return 降级的方法名
     */
    String fallbackMethod() default "";
}
