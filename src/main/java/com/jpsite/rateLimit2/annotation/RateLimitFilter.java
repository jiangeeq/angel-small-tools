package com.jpsite.rateLimit2.annotation;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.jpsite.rateLimit2.filter.InvocationContext;
import com.jpsite.rateLimit2.filter.ServerInvocationContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.env.Environment;
import org.springframework.util.Assert;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 限流过滤器，通过对契约接口的检查，对持有{@link RateLimit}注解的接口进行限流处理
 */
public class RateLimitFilter implements EnvironmentAware, InitializingBean, DisposableBean, Ordered, Filter {

    private static final Logger logger = LoggerFactory.getLogger(RateLimitFilter.class);

    /**
     * 方法与注解的缓存大小
     */
    private static final int CACHE_SIZE = 4096;

    private Environment environment;

    private LimitProcessorFactory limitProcessorFactory;

    /**
     * 方法与注解的缓存，优化反射效率
     */
    private LoadingCache<Method, List<RateLimit>> rateLimitCache;

    /**
     * @param limitProcessorFactory 限流处理器工厂
     */
    public RateLimitFilter(LimitProcessorFactory limitProcessorFactory) {
        this.limitProcessorFactory = limitProcessorFactory;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        rateLimitCache = CacheBuilder.newBuilder().maximumSize(CACHE_SIZE).build(new RateLimitLoader());
    }
    /**
     * 判断是否被当前拦截器拦截
     *
     * @param context 拦截器链中的上下文
     * @return 返回true，表示当前执行方法将被当前拦截器拦截；反之，返回false
     */
    public boolean supports(InvocationContext context) {
        return  supports(context.getMethod());
    }

    /**
     * 是否支持给定的方法（方法上是否标注有{@link RateLimit}注解）
     *
     * @param method 方法对象
     * @return 是否支持给定的方法
     */
    private boolean supports(Method method) {
        List<RateLimit> rateLimits = rateLimitCache.getUnchecked(method);
        return !rateLimits.isEmpty();
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws ServletException, IOException {
        final InvocationContext context = (InvocationContext)req.getAttribute("invocationContext");
        ServerInvocationContext serverInvocationContext = getServerInvocationContext(context);
        List<RateLimit> rateLimits = rateLimitCache.getUnchecked(context.getMethod());

        Object fallbackTarget = context.getTarget();
        Object[] fallbackArgs = getFallbackArgs(context);
        for (RateLimit rateLimit : rateLimits) {
            LimitProcessor limitProcessor = limitProcessorFactory.getInstance(serverInvocationContext, rateLimit);
            boolean validated = limitProcessor.preProcess();
            if (!validated) {
                logger.debug("前置检查不通过, rateLimit: {}", rateLimit);
                Method fallbackMethod = getFallbackMethod(context, rateLimit);
//                return invokeFallbackMethod(fallbackMethod, fallbackTarget, fallbackArgs);
            }
        }

        for (RateLimit rateLimit : rateLimits) {
            LimitProcessor limitProcessor = limitProcessorFactory.getInstance(serverInvocationContext, rateLimit);
            boolean validated = limitProcessor.postProcess(null);
            if (!validated) {
                logger.debug("后置检查不通过, rateLimit: {}", rateLimit);
                Method fallbackMethod = getFallbackMethod(context, rateLimit);
//                return invokeFallbackMethod(fallbackMethod, fallbackTarget, fallbackArgs);
            }
        }
        chain.doFilter(req,resp);
//        return response;
    }

    private ServerInvocationContext getServerInvocationContext(InvocationContext context){
        RequestAttributes ra = RequestContextHolder.getRequestAttributes();
        if (ra == null) {
            return new ServerInvocationContext(null, null, context);
        } else {
            ServletRequestAttributes sra = (ServletRequestAttributes) ra;
            return new ServerInvocationContext(sra.getRequest(), sra.getResponse(), context);
        }
    }
    /**
     * 获取降级方法
     *
     * @param context   过滤器上下文
     * @param rateLimit 限流注解对象
     * @return 降级方法
     */
    private Method getFallbackMethod(InvocationContext context, RateLimit rateLimit) {
        String fallbackMethodConfig = rateLimit.fallbackMethod();
        Assert.hasText(fallbackMethodConfig, "fallbackMethod配置不能为空");
        String fallbackMethod = environment.resolvePlaceholders(fallbackMethodConfig);
        Object[] args = context.getArgs();
        Class<?>[] parameterTypes;
        if (args != null && args.length > 0) {
            parameterTypes = new Class<?>[]{args[0].getClass()};
        } else {
            parameterTypes = new Class<?>[]{};
        }
        try {
            return context.getBeanType().getMethod(fallbackMethod, parameterTypes);
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException(String.format("查找fallbackMethod失败, beanType: %s, fallbackMethod: %s",
                    context.getBeanType(), fallbackMethod), e);
        }
    }

    /**
     * 获取降级方法参数
     *
     * @param context 过滤器上下文
     * @return 降级方法参数
     */
    private Object[] getFallbackArgs(InvocationContext context) {
        Object[] args = context.getArgs();
        if (args != null && args.length > 0) {
            return new Object[]{args[0]};
        }
        return new Object[]{};
    }

    /**
     * 调用降级方法
     *
     * @param fallbackMethod 降级方法
     * @param fallbackTarget 降级方法调用者
     * @param fallbackArgs   降级方法参数
     * @return 降级方法返回结果
     * @throws Throwable 如果是降级方法抛出的异常，则从InvocationTargetException中获取cause；如果是invoke过程的异常，则
     *                   直接抛出
     */
    private Object invokeFallbackMethod(Method fallbackMethod, Object fallbackTarget, Object[] fallbackArgs) throws Throwable {
        try {
            return fallbackMethod.invoke(fallbackTarget, fallbackArgs);
        } catch (InvocationTargetException e) {
            if (e.getCause() != null) {
                throw e.getCause();
            }
            throw e;
        }
    }

    @Override
    public void destroy() {
        rateLimitCache.cleanUp();
    }

    @Override
    public int getOrder() {
        return environment.getProperty("rotom.celebi.rateLimiterFilter.order", Integer.class, Ordered.LOWEST_PRECEDENCE);
    }

    /**
     * 限流注解加载器，用于缓存的安全加载
     */
    static class RateLimitLoader extends CacheLoader<Method, List<RateLimit>> {

        @Override
        public List<RateLimit> load(Method key) throws Exception {
            Set<RateLimit> rateLimits = AnnotatedElementUtils.findMergedRepeatableAnnotations(key, RateLimit.class);
            return rateLimits.stream()
                    .sorted(Comparator.comparing(RateLimit::order))
                    .collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));
        }
    }
}
