package com.jpsite.threadAsync;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author jiangpeng
 * @date 2021/3/516:30
 */
@Component
public class AsyncThreadUtils {
    private static ConcurrentHashMap<String, ExecutorService> threadPoolMap = new ConcurrentHashMap<>();

    @AllArgsConstructor
    @Getter
    public enum ThreadPoolTypeEnum {
        baseIoDenseThreadPool("baseIoDenseThreadPool", "简单io密集型"),
        baseCpuDenseThreadPool("baseCpuDenseThreadPool", "简单cpu密集型");

        private String type;
        private String desc;
    }

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    public void setThreadPoolMap(List<ExecutorService> executorServices) {
        final Map<String, ExecutorService> beansOfType = applicationContext.getBeansOfType(ExecutorService.class);
        beansOfType.forEach((k, v) -> threadPoolMap.putIfAbsent(k, v));

    }


    //异步非阻塞方式  AsyncThreadUtils.queue(()-> xxservice.cost(100), "baseIoDenseThreadPRool")
    public static <T, S> Future<T> queue(Supplier<T> supplier, S threadPoolName) {
        final ExecutorService executorService = threadPoolMap.get(threadPoolName);
        final Future<T> future = executorService.submit(() -> supplier.get());
        return future;
    }


    public static <T> void queue(Consumer<T> consumer, String threadPoolName) {
        final ExecutorService executorService = threadPoolMap.get(threadPoolName);
        executorService.execute(() -> consumer.accept(null));
    }

    /**
     * 简单io密集型
     *
     * @return
     */
    @Bean(value = "baseIoDenseThreadPool")
    public ExecutorService baseIoDenseThreadPool() {
        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder()
                .setNameFormat("baseIo-queue-thread-%d").build();

        ExecutorService pool = new ThreadPoolExecutor(5, 5, 0L, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<Runnable>(5), namedThreadFactory, new ThreadPoolExecutor.AbortPolicy());

        return pool;
    }

    /**
     * 简单cpu密集型
     *
     * @return
     */
    @Bean(value = "baseCpuDenseThreadPool")
    public ExecutorService baseCpuDenseThreadPool() {
        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder()
                .setNameFormat("baseCpu-queue-thread-%d").build();

        ExecutorService pool = new ThreadPoolExt(5, 5, 0L, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<Runnable>(5), namedThreadFactory, new ThreadPoolExecutor.AbortPolicy());

        return pool;
    }
}
