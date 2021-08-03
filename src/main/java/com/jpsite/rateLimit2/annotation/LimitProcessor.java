package com.jpsite.rateLimit2.annotation;

/**
 * 限流处理器接口
 */
public interface LimitProcessor {

    /**
     * 在业务代码执行前进行处理
     *
     * @return 是否通过校验，即是否还未超出限流
     */
    boolean preProcess();

    /**
     * 在业务代码执行后进行处理
     *
     * @param response 业务代码返回值
     * @return 是否通过校验，即是否还未超出限流
     */
    boolean postProcess(Object response);
}
