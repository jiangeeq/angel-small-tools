package com.jpsite.rateLimit2.annotation;

import com.jpsite.rateLimit2.RateLimiter;

import com.jpsite.rateLimit2.filter.ServerInvocationContext;
import com.jpsite.utils.ServletUtils;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.http.HttpHeaders;
import org.springframework.util.Assert;

import java.util.concurrent.TimeUnit;

/**
 * 抽象限流处理器
 */
public abstract class AbstractLimitProcessor implements LimitProcessor, EnvironmentAware {

    /**
     * 请求参数变量名，用于SpEL表达式中引用契约接口请求参数
     */
    public static final String REQUEST = "request";

    /**
     * 请求头部变量名，用于SpEL表达式中引用契约接口请求头部
     */
    public static final String REQUEST_HEADERS = "requestHeaders";

    /**
     * 响应参数变量名，用于SpEL表达式中引用契约接口返回值
     */
    public static final String RESPONSE = "response";

    /**
     * 响应头部变量名，用于SpEL表达式中引用契约接口响应头部（这个时机响应头部可能还未写入完成）
     */
    public static final String RESPONSE_HEADERS = "responseHeaders";

    /**
     * SpEL表达式解析器
     */
    protected SpelExpressionParser spelExpressionParser = new SpelExpressionParser();

    /**
     * celebi拦截器上下文
     */
    protected ServerInvocationContext context;

    /**
     * 当前处理的{@link RateLimit}注解对象
     */
    protected RateLimit rateLimit;

    /**
     * 限流器工作对象
     */
    protected RateLimiter rateLimiter;

    /**
     * 环境上下文
     */
    protected Environment environment;

    /**
     * @param context     celebi拦截器上下文
     * @param rateLimit   当前处理的{@link RateLimit}注解对象
     * @param rateLimiter 限流器工作对象
     */
    public AbstractLimitProcessor(ServerInvocationContext context, RateLimit rateLimit, RateLimiter rateLimiter) {
        this.context = context;
        this.rateLimit = rateLimit;
        this.rateLimiter = rateLimiter;
        this.environment = context.getEnvironment();
    }

    /**
     * 获取契约接口请求参数，如果是无参接口，返回null
     *
     * @return 契约接口请求参数
     */
    protected Object getArg() {
        Object[] args = context.getArgs();
        if (args != null && args.length > 0) {
            return args[0];
        }
        return null;
    }

    /**
     * 获取契约接口请求头部
     *
     * @return 契约接口请求头部
     */
    protected HttpHeaders getRequestHeaders() {
        return ServletUtils.getHttpHeaders(context.getRequest());
    }

    /**
     * 获取契约接口响应头部
     *
     * @return 契约接口响应头部
     */
    protected HttpHeaders getResponseHeaders() {
        return ServletUtils.getHttpHeaders(context.getResponse());
    }

    /**
     * 获取当前处理的key配置
     *
     * @return 当前处理的key配置
     */
    protected String getKeyConfig() {
        String keyConfig = rateLimit.key();
        Assert.hasText(keyConfig, "key配置不能为空");
        return environment.resolvePlaceholders(keyConfig);
    }

    /**
     * 获取当前处理的计数条件配置
     *
     * @return 当前处理的计数条件配置
     */
    protected String getIncrConditionConfig() {
        String incrConditionConfig = rateLimit.incrCondition();
        Assert.hasText(incrConditionConfig, "incrCondition配置不能为空");
        return environment.resolvePlaceholders(incrConditionConfig);
    }

    /**
     * 获取当前处理的限流最大计数
     *
     * @return 当前处理的限流最大计数
     */
    protected long getLimit() {
        String limitConfig = rateLimit.limit();
        Assert.hasText(limitConfig, "limit配置不能为空");
        String limit = environment.resolvePlaceholders(limitConfig);
        return Long.valueOf(limit);
    }

    /**
     * 获取当前处理的限流时间范围
     *
     * @return 当前处理的限流时间范围
     */
    protected long getInterval() {
        String intervalConfig = rateLimit.interval();
        Assert.hasText(intervalConfig, "interval配置不能为空");
        String interval = environment.resolvePlaceholders(intervalConfig);
        return Long.valueOf(interval);
    }

    /**
     * 获取当前处理的限流时间单位
     *
     * @return 当前处理的限流时间单位
     */
    protected TimeUnit getIntervalUnit() {
        String intervalUnitConfig = rateLimit.intervalUnit();
        Assert.hasText(intervalUnitConfig, "intervalUnit配置不能为空");
        String intervalUnit = environment.resolvePlaceholders(intervalUnitConfig);
        return TimeUnit.valueOf(intervalUnit);
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
