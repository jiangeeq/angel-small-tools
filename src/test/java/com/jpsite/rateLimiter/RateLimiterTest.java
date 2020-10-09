package com.jpsite.rateLimiter;

import com.google.common.util.concurrent.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.junit.Test;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author jiangpeng
 * @date 2019/11/1614:11
 */
@Slf4j
public class RateLimiterTest {
    /**
     * 简单计数器的限流算法
     *
     * @return boolean
     */
    private boolean acquire(AtomicInteger atomicInteger) {
        // 限制最大访问容量
        int limitCount = 3;
        // 原子性的 Integer对象
        long startTime = System.currentTimeMillis();
        // 间隔时间（毫秒）
        int interval = 30;
        long newTime = System.currentTimeMillis();
        // 是一个新的周期时间则重置计数器
        if (newTime > (startTime + interval)) {
            startTime = newTime;
            atomicInteger.set(0);
            return true;
        }
        atomicInteger.incrementAndGet();
        return atomicInteger.get() <= limitCount;
    }


    /**
     * 简单计算器限流测试
     */
    @Test
    public void simpleLimitTest() {
        AtomicInteger atomicInteger = new AtomicInteger(0);

        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(2, Integer.MAX_VALUE,
                0L, TimeUnit.SECONDS,
                new SynchronousQueue<>(), new NameThreadFactory("counterLimit"));

        for (int i = 1; i < 100; i++) {
            final int temp = i;
            threadPoolExecutor.execute(() -> {
                if (this.acquire(atomicInteger)) {
                    log.info("你没有被限流,可以正常访问逻辑 i:" + temp);
                } else {
                    log.info("你已经被限流呢  i:" + temp);
                }
            });
        }
    }

    /**
     * 令牌桶限流算法
     */
    @Test
    public void tokenBucketTest() {
        // 1.0 表示 每秒中生成1个令牌存放在桶中
        RateLimiter rateLimiter = RateLimiter.create(1.0);

        for (int i = 1; i < 10; i++) {
            boolean acquire = rateLimiter.tryAcquire(RandomUtils.nextInt(8, 15) * 100, TimeUnit.MILLISECONDS);
            log.info("获取令牌等待时间 [{}]毫秒:", rateLimiter.acquire());

            if (!acquire) {
                log.info("你在怎么抢，也抢不到，因为会一直等待的，你先放弃吧！{}", i);
                continue;
            }
            log.info("恭喜您,抢到了! {}", i);
        }
    }
}
