//package com.jpsite.rateLimit2.filter;
//
//import com.google.common.base.Preconditions;
//import com.jpsite.utils.GlobalEnvironment;
//import org.springframework.core.annotation.AnnotatedElementUtils;
//import org.springframework.core.env.Environment;
//
//import java.lang.annotation.Annotation;
//import java.lang.reflect.Method;
//
///**
// * 抽象的Filter, 提供一些基础方法
// */
//public abstract class AbstractFilter implements Filter {
//
//    /**
//     * 通过Environment, 判断拦截的beanType是否是服务端
//     *
//     * @param environment spring环境
//     * @param beanType    拦截的beanType
//     * @return 当拦截的beanType是服务端时，返回true; 否认返回false
//     * @see GlobalEnvironment
//     */
//    protected boolean isServer(Environment environment, Class<?> beanType) {
//        Preconditions.checkNotNull(beanType);
//        Preconditions.checkState(environment != null);
//        return GlobalEnvironment.checkBeanIsServerImpl(environment, beanType);
//    }
//
//    /**
//     * 通过Environment, 判断拦截的beanType是否是客户端
//     *
//     * @param environment spring环境
//     * @param beanType    拦截的beanType
//     * @return 当拦截的beanType是客户端时，返回true; 否认返回false
//     * @see GlobalEnvironment
//     */
//    protected boolean isClient(Environment environment, Class<?> beanType) {
//        return !isServer(environment, beanType);
//    }
//
//    /**
//     * 判断某一个方法上是否有annotation注解，当method是否契约实现类的契约接口方法时，会同时查看其接口方法是否有annotation注解
//     *
//     * @param method     方法
//     * @param annotation 注解
//     * @return 当方法上有annotation注解时返回true；否则返回false
//     */
//    protected boolean hasAnnotation(Method method, Class<? extends Annotation> annotation) {
//        return AnnotatedElementUtils.hasAnnotation(method, annotation);
//    }
//}
