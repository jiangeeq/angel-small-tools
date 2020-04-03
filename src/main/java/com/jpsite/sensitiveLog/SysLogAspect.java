package com.jpsite.sensitiveLog;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 系统日志切面
 *
 * @author jiangpeng
 * 切面注解得到请求数据
 */
@Slf4j
@Aspect
@Component
public class SysLogAspect {
    private ThreadLocal<OperatorLogBO> sysLogThreadLocal = new ThreadLocal<>();

    /***
     * 定义controller切入点拦截规则，拦截SysLog注解的方法
     */
    @Pointcut("@annotation(com.jpsite.sensitiveLog.LogSensitive)")
    public void sysLogAspect() {
        // Pointcut
    }

    /***
     * 拦截控制层的操作日志
     * @param joinPoint 切入点
     */
    @Before(value = "sysLogAspect()")
    public void recordLog(JoinPoint joinPoint) {
        OperatorLogBO sysLog = new OperatorLogBO();
        //将当前实体保存到threadLocal
        sysLogThreadLocal.set(sysLog);
        // 开始时间
        long beginTime = Instant.now().toEpochMilli();
        HttpServletRequest request =
                ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        sysLog.setActionUrl(request.getRequestURI());
        sysLog.setStartTime(LocalDateTime.now());
        sysLog.setRequestMethod(request.getMethod());
        //获取执行的方法名
        sysLog.setActionMethod(joinPoint.getSignature().getName());
        // 类名
        sysLog.setClassPath(joinPoint.getTarget().getClass().getName());
        sysLog.setFinishTime(LocalDateTime.now());
        //访问目标方法的参数 可动态改变参数值
        sysLog.setParams(SysLogUtil.getControllerMethodSensitiveParam(joinPoint));
        sysLog.setDescription(SysLogUtil.getControllerMethodDescription(joinPoint));
        sysLog.setConsumingTime(Instant.now().toEpochMilli() - beginTime);

        log.info("执行：{}开始，sysLog: {}", sysLog.getDescription(), sysLog);
    }

    /**
     * 返回通知
     *
     * @param returnValue 方法的返回对象
     */
    @AfterReturning(returning = "returnValue", pointcut = "sysLogAspect()")
    public void doAfterReturning(Object returnValue) {
        //得到当前线程的log对象
        OperatorLogBO sysLog = sysLogThreadLocal.get();
        //移除当前log实体
        sysLogThreadLocal.remove();

        log.info("执行：{}结束，返回对象为：{}", sysLog.getDescription(), returnValue.toString());
    }
}
