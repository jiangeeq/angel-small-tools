package com.jpsite.rateLimit2.redis;

import com.jpsite.rateLimit2.RateLimiter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

@Configuration
@ConditionalOnProperty(value = {"rate.limiter.redis.enabled"}, matchIfMissing = true)
public class RedisRateLimiterConfiguration {

    @Bean
    @ConditionalOnMissingBean(RateLimiter.class)
    public RedisRateLimiter redisRateLimiter(StringRedisTemplate stringRedisTemplate) {
        return new RedisRateLimiter(stringRedisTemplate);
    }
}
