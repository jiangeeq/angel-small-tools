package com.jpsite.rateLimit2.annotation;


import com.jpsite.rateLimit2.RateLimiter;
import com.jpsite.rateLimit2.filter.ServerInvocationContext;

/**
 * 限流处理器工厂
 */
public class LimitProcessorFactory {

    /**
     * 限流器工作对象
     */
    private RateLimiter rateLimiter;

    /**
     * @param rateLimiter 限流器工作对象
     */
    public LimitProcessorFactory(RateLimiter rateLimiter) {
        this.rateLimiter = rateLimiter;
    }

    /**
     * 获取限流处理器实例
     *
     * @param context   celebi拦截器上下文
     * @param rateLimit 限流器工作对象
     * @return 限流处理器实例
     */
    public LimitProcessor getInstance(ServerInvocationContext context, RateLimit rateLimit) {
        IncrType incrType = rateLimit.incrType();
        switch (incrType) {
            case PRE_INCR:
                return new PreIncrLimitProcessor(context, rateLimit, rateLimiter);
            case POST_INCR:
                return new PostIncrLimitProcessor(context, rateLimit, rateLimiter);
            default:
                throw new IllegalArgumentException(String.format("非法的incrType: %s", incrType));
        }
    }
}
