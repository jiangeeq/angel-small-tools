package com.jpsite.rateLimit2.redis;

import com.jpsite.rateLimit2.RateLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

/**
 * 基于Redis的限流工作对象
 */
public class RedisRateLimiter implements RateLimiter {

    private static final Logger logger = LoggerFactory.getLogger(RedisRateLimiter.class);

    private static final String INCR_AND_EXPIRE_SCRIPT = "" +
            "local old = redis.call('get', KEYS[1]);\n" +
            "local current;\n" +
            "if (not old) then\n" +
            "    redis.call('psetex', KEYS[1], ARGV[1], 1);\n" +
            "    current = 1;    \n" +
            "else\n" +
            "    current = redis.call('incr', KEYS[1]);\n" +
            "end\n" +
            "return current;";

    private StringRedisTemplate stringRedisTemplate;

    /**
     * @param stringRedisTemplate redis模板对象
     */
    public RedisRateLimiter(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public boolean validate(String key, long limit) {
        Object current = stringRedisTemplate.opsForValue().get(key);
        logger.debug("redis rate limiter validate, key: {}, limit: {}, current: {}", key, limit, current);
        if (current == null) {
            return true;
        }
        return Long.valueOf(current.toString()) < limit;
    }

    @Override
    public boolean incrAndValidate(String key, long limit, long interval, TimeUnit intervalUnit) {
        Long current = incrWithExpire(key, interval, intervalUnit);
        logger.debug("redis rate limiter incrAndValidate, key: {}, limit: {}, current: {}", key, limit, current);
        return current <= limit;
    }

    private long incrWithExpire(String key, long interval, TimeUnit intervalUnit) {
        long expire = TimeUnit.MILLISECONDS.convert(interval, intervalUnit);
        RedisScript<Long> script = new DefaultRedisScript<>(INCR_AND_EXPIRE_SCRIPT, Long.class);
        Long current = stringRedisTemplate.execute(script, Collections.singletonList(key), String.valueOf(expire));
        if (current == 1) {
            logger.debug("redis rate limiter incrWithExpire, key: {}, value: {}, expire: {}", key, current, expire);
        } else {
            logger.debug("redis rate limiter incrWithExpire, key: {}, value: {}", key, current);
        }
        return current;
    }
}
