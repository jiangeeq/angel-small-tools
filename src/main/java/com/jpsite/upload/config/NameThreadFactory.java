package com.jpsite.upload.config;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author jiangpeng
 * @date 2019/11/1617:32
 */
public class NameThreadFactory implements ThreadFactory {
    /**
     * pool池的个数
     */
    private static final AtomicInteger POOL_NUMBER = new AtomicInteger(1);
    /**
     * 线程个数
     */
    private static final AtomicInteger THREAD_NUMBER = new AtomicInteger(1);
    /**
     * 线程名字前缀
     */
    private final String threadNamePrefix;

    public NameThreadFactory(String threadName) {
        // pool 初始化一个池子
        this.threadNamePrefix = threadName + "-pool-" + POOL_NUMBER.getAndIncrement() + "-thread-";
    }

    @Override
    public Thread newThread(Runnable r) {
        // 真正获取线程准备执行
        return new Thread(r, threadNamePrefix + THREAD_NUMBER.getAndIncrement());
    }
}
