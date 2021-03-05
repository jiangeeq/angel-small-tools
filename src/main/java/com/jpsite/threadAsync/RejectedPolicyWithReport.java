package com.jpsite.threadAsync;

import lombok.extern.slf4j.Slf4j;

import java.lang.management.LockInfo;
import java.lang.management.ManagementFactory;
import java.lang.management.MonitorInfo;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.text.MessageFormat;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 *  * 自定义线程池拒绝策略:<p>
 *  * 1.记录线程池的核心线程数,活跃数,已完成数等信息,以及调用线程的堆栈信息,便于排查<p>
 *  * 2.抛出异常中断执行<p>
 * @author jiangpeng
 * @date 2021/3/516:05
 */
@Slf4j
public class RejectedPolicyWithReport implements RejectedExecutionHandler {
    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
        try {
            String title = "thread pool execute reject policy!!";
            String msg = MessageFormat.format(
                    "{0}{1}core pool size:{2}, current pool size:{3}, queue wait size:{4}, active count:{5}, completed task count:{6}, " +
                            "task count:{7}, largest pool size:{8}, max pool size:{9}, keep alive time:{10}, is shutdown:{11}, is terminated:{12}, " +
                            "thread name:{13}{14}",
                    System.lineSeparator(), title, e.getCorePoolSize(), e.getPoolSize(), e.getQueue().size(), e.getActiveCount(),
                    e.getCompletedTaskCount(), e.getTaskCount(), e.getLargestPoolSize(), e.getMaximumPoolSize(), e.getKeepAliveTime(TimeUnit.SECONDS),
                    e.isShutdown(), e.isTerminated(), Thread.currentThread().getName(), System.lineSeparator());
            log.info(msg);
            // jstack(); // 记录线程堆栈信息包括锁争用信息, 测试环境需要调试时可以放开
        } catch (Exception ex) {
            log.error("RejectedPolicyWithReport rejectedExecution error", ex);
        }
        throw new RejectedExecutionException("thread pool execute reject policy!!");
    }

    /**
     * 获取线程dump信息<p>
     * 注意: 该方法默认会记录所有线程和锁信息,方便debug, 但是可能会中断服务响应.增加latency,生产环境使用请慎重!!<p>
     * 1.当前线程的基本信息:id,name,state<p>
     * 2.堆栈信息<p>
     * 3.锁相关信息(可以设置不记录)<p>
     *  默认在log记录<p>
     * @return
     */
    private void jstack() {
        ThreadMXBean threadMxBean = ManagementFactory.getThreadMXBean();
        for (ThreadInfo threadInfo : threadMxBean.dumpAllThreads(true, true)) {
            log.info(getThreadDumpString(threadInfo));
        }
    }

    private String getThreadDumpString(ThreadInfo threadInfo) {
        StringBuilder sb = new StringBuilder("\"" + threadInfo.getThreadName() + "\"" +
                " Id=" + threadInfo.getThreadId() + " " +
                threadInfo.getThreadState());
        if (threadInfo.getLockName() != null) {
            sb.append(" on " + threadInfo.getLockName());
        }
        if (threadInfo.getLockOwnerName() != null) {
            sb.append(" owned by \"" + threadInfo.getLockOwnerName() +
                    "\" Id=" + threadInfo.getLockOwnerId());
        }
        if (threadInfo.isSuspended()) {
            sb.append(" (suspended)");
        }
        if (threadInfo.isInNative()) {
            sb.append(" (in native)");
        }
        sb.append('\n');
        int i = 0;

        StackTraceElement[] stackTrace = threadInfo.getStackTrace();
        MonitorInfo[] lockedMonitors = threadInfo.getLockedMonitors();
        for (; i < stackTrace.length && i < 32; i++) {
            StackTraceElement ste = stackTrace[i];
            sb.append("\tat " + ste.toString());
            sb.append('\n');
            if (i == 0 && threadInfo.getLockInfo() != null) {
                Thread.State ts = threadInfo.getThreadState();
                switch (ts) {
                    case BLOCKED:
                        sb.append("\t-  blocked on " + threadInfo.getLockInfo());
                        sb.append('\n');
                        break;
                    case WAITING:
                        sb.append("\t-  waiting on " + threadInfo.getLockInfo());
                        sb.append('\n');
                        break;
                    case TIMED_WAITING:
                        sb.append("\t-  waiting on " + threadInfo.getLockInfo());
                        sb.append('\n');
                        break;
                    default:
                }
            }

            for (MonitorInfo mi : lockedMonitors) {
                if (mi.getLockedStackDepth() == i) {
                    sb.append("\t-  locked " + mi);
                    sb.append('\n');
                }
            }
        }
        if (i < stackTrace.length) {
            sb.append("\t...");
            sb.append('\n');
        }

        LockInfo[] locks = threadInfo.getLockedSynchronizers();
        if (locks.length > 0) {
            sb.append("\n\tNumber of locked synchronizers = " + locks.length);
            sb.append('\n');
            for (LockInfo li : locks) {
                sb.append("\t- " + li);
                sb.append('\n');
            }
        }
        sb.append('\n');
        return sb.toString();
    }
}
