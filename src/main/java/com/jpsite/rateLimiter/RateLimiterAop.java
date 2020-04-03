package com.jpsite.rateLimiter;

import com.google.common.util.concurrent.RateLimiter;
import com.jpsite.rateLimiter.ExtRateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 注解处理的sprig aop切面类
 * @author jiangpeng
 * @date 2019/11/1617:53
 */
@Aspect
@Component
@Slf4j
public class RateLimiterAop {

    private static ConcurrentHashMap<String, RateLimiter> rateLimiterMap = new ConcurrentHashMap<>();
    // 拦截范围 com.jpsite.upload.controller 下的所有
    @Pointcut("execution(public * com.jpsite.upload.controller.*.*(..))")
    public void rlAop() {

    }

    @Around("rlAop()")
    public Object doBefore(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        // 获取目标方法信息
        MethodSignature signature = (MethodSignature) proceedingJoinPoint.getSignature();
        // 反射技术判断方法上是否有@ExtRateLimiter注解
        ExtRateLimiter extRateLimiter = signature.getMethod().getDeclaredAnnotation(ExtRateLimiter.class);
        // 没有则放行
        if (extRateLimiter == null) {
            return proceedingJoinPoint.proceed();
        }
        // ############获取注解上的参数 配置固定速率 ###############
        // 获取配置的速率
        int speed = extRateLimiter.speed();
        // 获取等待令牌等待时间
        long timeOut = extRateLimiter.timeOut();
        // 获取对应的令牌桶限流生成器
        RateLimiter rateLimiter = getRateLimiter(speed);
        /** 等待timeout 毫秒后获取令牌，超时未获取到则返回false**/
        boolean tryAcquire = rateLimiter.tryAcquire(timeOut, TimeUnit.MILLISECONDS);
        if (!tryAcquire) {
            serviceDown();
            log.info("获取令牌桶超时！");
            return null;
        }
        // 获取到令牌,直接执行..
        return proceedingJoinPoint.proceed();
    }

    /**
     * 获取RateLimiter对象
     */
    private RateLimiter getRateLimiter(int speed) {
        // 获取当前请求的 uri
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = Objects.requireNonNull(attributes).getRequest();
        String requestUri = request.getRequestURI();

        RateLimiter rateLimiter;

        // 判断令牌桶生成器Map 是否有当前uri 的令牌桶生成器
        if (!rateLimiterMap.containsKey(requestUri)) {
            log.info("为[{}]添加令牌桶生成器，速率为[{}]/个", requestUri, speed);
            // 添加令牌通限流生成器，独立线程, 未指定单位为每秒生成speed个令牌
            rateLimiter = RateLimiter.create(speed);
            rateLimiterMap.put(requestUri, rateLimiter);
        } else {
            // 这个uri有令牌桶限流生成器
            rateLimiter = rateLimiterMap.get(requestUri);
        }
        return rateLimiter;
    }

    /**
     * 服务降级
     */
    private void serviceDown() {
        log.info("执行降级方法,亲,服务器忙！请稍后重试!");

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletResponse response = Objects.requireNonNull(attributes).getResponse();
        Objects.requireNonNull(response).setHeader("Content-type", "text/html;charset=UTF-8");

        try (PrintWriter writer = response.getWriter()) {
            writer.println("亲，您的手速太快了！请稍后重试！");
        } catch (Exception e) {
            log.error("降级通知失败", e);
        }
    }
}
