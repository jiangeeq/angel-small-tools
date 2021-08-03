package com.jpsite.rateLimit2.filter;

import com.jpsite.rateLimit2.annotation.RateLimitFilter;
import com.jpsite.utils.SpringUtil;
import org.apache.catalina.core.ApplicationFilterChain;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Locale;

/**
 * 定义契约接口的全局拦截Aspect
 */
@Aspect
public class GlobalFilterAspect extends HttpServlet implements Ordered {

    private static final String ORDER_KEY = "celebi.filter.globalFilterAspect.order";

    private static final int DEFAULT_ORDER = Ordered.HIGHEST_PRECEDENCE + 99;

//    private final List<Filter> filterList;
//
//    private final Environment environment;
//
//    public GlobalFilterAspect(List<Filter> filterList, Environment environment) {
//        this.filterList = filterList;
//        this.environment = environment;
//    }

    /**
     * 拦截celebi契约接口，该方法将拦截所有返回类型为
     *
     * @param joinPoint aop连接点
     * @throws Throwable aop处理过程异常
     */
    @Around("execution(public com.jpsite.rateLimit2.contract.Response * (..))")
    Object filter(final ProceedingJoinPoint joinPoint) throws Throwable {
//        // 无filter
//        if (filterList == null || filterList.isEmpty()) {
//            return joinPoint.proceed();
//        }

        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Object target = joinPoint.getTarget();
        Class beanType = methodSignature.getDeclaringType();
        Method method = methodSignature.getMethod();
        InvocationContext context = new InvocationContext(target, beanType, method, joinPoint.getArgs(), SpringUtil.environment());
//        return FilterChain.buildFilterChain(filterList, context, joinPoint)
//                .doFilter(context);
        final RateLimitFilter rateLimitFilter = SpringUtil.getBean(RateLimitFilter.class);
        HttpServletRequest request =((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        request.setAttribute("invocationContext",context);
        HttpServletResponse response =((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getResponse();
        final Locale locale = LocaleContextHolder.getLocaleContext().getLocale();
        final ApplicationFilterChain filterChain = SpringUtil.getBean(ApplicationFilterChain.class);
        rateLimitFilter.doFilter(request, response, filterChain);
        return null;
    }

    @Override
    public int getOrder() {
        return SpringUtil.environment().getProperty(ORDER_KEY, Integer.class, DEFAULT_ORDER);
    }
}

