package com.jpsite.rateLimit2.filter;

import org.springframework.core.env.Environment;

import java.lang.reflect.Method;
import java.util.Objects;

public class InvocationContext {

    private Object target;

    private final Class<?> beanType;

    private final Method method;

    private final Object[] args;

    private final Environment environment;

    @Deprecated
    public InvocationContext(Class<?> beanType, Method method, Object[] args, Environment environment) {
        this.beanType = beanType;
        this.method = method;
        this.args = args;
        this.environment = environment;
    }

    public InvocationContext(Object target, Class<?> beanType, Method method, Object[] args, Environment environment) {
        this.target = target;
        this.beanType = beanType;
        this.method = method;
        this.args = args;
        this.environment = environment;
    }

    public Object getTarget() {
        return target;
    }

    public Class<?> getBeanType() {
        return beanType;
    }

    public Method getMethod() {
        return method;
    }

    public Object[] getArgs() {
        return args;
    }

    public Environment getEnvironment() {
        return environment;
    }

    @Override
    public int hashCode() {
        return Objects.hash(target, beanType, method, args, environment);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final InvocationContext other = (InvocationContext) obj;
        return Objects.equals(this.target, other.target)
                && Objects.equals(this.beanType, other.beanType)
                && Objects.equals(this.method, other.method)
                && Objects.deepEquals(this.args, other.args)
                && Objects.equals(this.environment, other.environment);
    }
}


