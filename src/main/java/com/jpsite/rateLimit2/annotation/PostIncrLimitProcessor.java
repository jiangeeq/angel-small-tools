package com.jpsite.rateLimit2.annotation;

import com.jpsite.rateLimit2.RateLimiter;
import com.jpsite.rateLimit2.filter.ServerInvocationContext;
import org.springframework.expression.spel.support.StandardEvaluationContext;

/**
 * 后置计数类型的限流处理器实现
 */
public class PostIncrLimitProcessor extends AbstractLimitProcessor {

    /**
     * @param context     celebi拦截器上下文
     * @param rateLimit   当前处理的{@link RateLimit}注解对象
     * @param rateLimiter 限流器工作对象
     */
    public PostIncrLimitProcessor(ServerInvocationContext context, RateLimit rateLimit, RateLimiter rateLimiter) {
        super(context, rateLimit, rateLimiter);
    }

    @Override
    public boolean preProcess() {
        StandardEvaluationContext preEvaluationContext = new StandardEvaluationContext();
        preEvaluationContext.setVariable(REQUEST, getArg());
        preEvaluationContext.setVariable(REQUEST_HEADERS, getRequestHeaders());
        String key = spelExpressionParser.parseExpression(getKeyConfig())
                .getValue(preEvaluationContext, String.class);
        return rateLimiter.validate(key, getLimit());
    }

    @Override
    public boolean postProcess(Object response) {
        StandardEvaluationContext postEvaluationContext = new StandardEvaluationContext();
        postEvaluationContext.setVariable(REQUEST, getArg());
        postEvaluationContext.setVariable(REQUEST_HEADERS, getRequestHeaders());
        postEvaluationContext.setVariable(RESPONSE, response);
        postEvaluationContext.setVariable(RESPONSE_HEADERS, getResponseHeaders());

        Boolean matchIncrCondition = spelExpressionParser.parseExpression(getIncrConditionConfig())
                .getValue(postEvaluationContext, Boolean.class);
        if (matchIncrCondition) {
            String key = spelExpressionParser.parseExpression(getKeyConfig())
                    .getValue(postEvaluationContext, String.class);
            return rateLimiter.incrAndValidate(key, getLimit(), getInterval(), getIntervalUnit());
        }
        return true;
    }
}
