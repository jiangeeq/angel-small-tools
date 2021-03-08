package com.jpsite.threadAsync;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author jiangpeng
 * @date 2021/3/810:51
 */
@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class AsyncThreadUtilsTest {

    @Test
    public void testQueue() {
        final Future<String> future = AsyncThreadUtils.queue(() -> task(), "baseCpuDenseThreadPool");
        final String s;
        try {
            s = future.get(1, TimeUnit.SECONDS);
            System.out.println(s);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            System.out.println("在指定时间内没有获取到数据");
        }
    }

    @Test
    public void testQueue2() {
        AsyncThreadUtils.queue(x->task(), "baseCpuDenseThreadPool");
        System.out.println("无需等待结果返回");
    }


    private String task() {
        System.out.println("开始执行任务"+ LocalDateTime.now());
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "result";
    }
}
