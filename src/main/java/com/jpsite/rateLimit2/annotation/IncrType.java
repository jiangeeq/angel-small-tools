package com.jpsite.rateLimit2.annotation;

/**
 * 计数类型
 */
public enum IncrType {

    /**
     * 前置计数，在业务代码执行前计数
     */
    PRE_INCR,

    /**
     * 后置计数，在业务代码执行后计数
     */
    POST_INCR
}
