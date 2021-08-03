package com.jpsite.rateLimit2.annotation;

import com.jpsite.rateLimit2.RateLimiter;
import com.jpsite.rateLimit2.annotation.RateLimit;

import com.jpsite.rateLimit2.filter.ServerInvocationContext;
import org.springframework.expression.spel.support.StandardEvaluationContext;

/**
 * 前置计数类型的限流处理器实现
 */
public class PreIncrLimitProcessor extends AbstractLimitProcessor {

    /**
     * @param context     celebi拦截器上下文
     * @param rateLimit   当前处理的{@link RateLimit}注解对象
     * @param rateLimiter 限流器工作对象
     */
    public PreIncrLimitProcessor(ServerInvocationContext context, RateLimit rateLimit, RateLimiter rateLimiter) {
        super(context, rateLimit, rateLimiter);
    }

    @Override
    public boolean preProcess() {
        StandardEvaluationContext preEvaluationContext = new StandardEvaluationContext();
        preEvaluationContext.setVariable(REQUEST, getArg());
        preEvaluationContext.setVariable(REQUEST_HEADERS, getRequestHeaders());
        Boolean matchIncrCondition = spelExpressionParser.parseExpression(getIncrConditionConfig())
                .getValue(preEvaluationContext, Boolean.class);
        if (matchIncrCondition) {
            String key = spelExpressionParser.parseExpression(getKeyConfig())
                    .getValue(preEvaluationContext, String.class);
            return rateLimiter.incrAndValidate(key, getLimit(), getInterval(), getIntervalUnit());
        }
        return true;
    }

    @Override
    public boolean postProcess(Object response) {
        return true;
    }
}
