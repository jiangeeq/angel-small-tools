package com.jpsite.rateLimit2;

import com.jpsite.rateLimit2.annotation.LimitProcessorFactory;
import com.jpsite.rateLimit2.annotation.RateLimitFilter;
import com.jpsite.rateLimit2.redis.RedisRateLimiterConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * 自动配置对象
 * 使用方式：在META-INF/spring.factories下加如下内容
 * org.springframework.boot.autoconfigure.EnableAutoConfiguration=com.ppmoney.ppmon.rotom.celebi.rate.limiter.RateLimiterAutoConfiguration
 * @author jiangpeng
 * @date 2021/5/2817:46
 */
@Configuration
@ConditionalOnProperty(value = {"rate.limiter.enabled"}, matchIfMissing = true)
@Import(RedisRateLimiterConfiguration.class)
public class RateLimiterAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public LimitProcessorFactory limitProcessorFactory(RateLimiter rateLimiter) {
        return new LimitProcessorFactory(rateLimiter);
    }

    @Bean
    @ConditionalOnMissingBean
    public RateLimitFilter rateLimitFilter(LimitProcessorFactory limitProcessorFactory) {
        return new RateLimitFilter(limitProcessorFactory);
    }
}
