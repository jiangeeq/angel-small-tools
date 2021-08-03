package com.jpsite.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author jiangpeng
 * @date 2020/10/1317:05
 */
@Slf4j
public class SpringUtil {
    /**
     * Stores all used bean names so we can enforce uniqueness on a per
     * beans-element basis. Duplicate bean ids/names may not exist within the
     * same level of beans element nesting, but may be duplicated across levels.
     */
    private final Set<String> usedNames = new HashSet<>();

    private static ApplicationContext context;

    public static void setApplicationContext(ApplicationContext applicationContext) {
        context = applicationContext;
    }

    public static ApplicationContext applicationContext() {
        return context;
    }

    public static <T> T getBean(Class<T> requiredType) {
        return context.getBean(requiredType);
    }

    public static Environment environment() {
        return context.getEnvironment();
    }

    public static String environment(String propertyName) {
        return environment().getProperty(propertyName);
    }

    public static boolean registerBeanDefinitions(Class<?> clazz) {
        AnnotationConfigServletWebServerApplicationContext annotationContext = (AnnotationConfigServletWebServerApplicationContext)context;

        String[] beanDefinitionNames = annotationContext.getBeanDefinitionNames();
        if (Arrays.asList(beanDefinitionNames).contains(clazz.getSimpleName())){
            log.info("已存在的beanDefinition[{}]，删除并重新部署", clazz.getSimpleName());
            annotationContext.removeBeanDefinition(clazz.getSimpleName());
        }
        // 获得容器中已经注册的BeanDefinition数量
        int countBefore = annotationContext.getBeanDefinitionCount();
        RootBeanDefinition classBean = new RootBeanDefinition(clazz);
        //bean的定义注册到spring环境
        annotationContext.registerBeanDefinition(clazz.getSimpleName(), classBean);
        // 统计新的的BeanDefinition注册数量
        log.info("统计新的的BeanDefinition注册数量为[{}], 原注册数为：[{}]", SpringUtil.context.getBeanDefinitionCount(), countBefore);
        return annotationContext.getBeanDefinitionCount() - countBefore > 0;
    }

}
