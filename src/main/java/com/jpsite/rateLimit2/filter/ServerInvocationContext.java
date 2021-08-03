package com.jpsite.rateLimit2.filter;

import com.jpsite.rateLimit2.filter.InvocationContext;
import org.springframework.core.env.Environment;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Objects;

public class ServerInvocationContext extends InvocationContext {

    protected final HttpServletRequest request;

    protected final HttpServletResponse response;

    @Deprecated
    public ServerInvocationContext(HttpServletRequest request, HttpServletResponse response, Class<?> beanType, Method
            method, Object[] args, Environment environment) {
        super(beanType, method, args, environment);
        this.request = request;
        this.response = response;
    }

    public ServerInvocationContext(HttpServletRequest request, HttpServletResponse response, Object target,
                                   Class<?> beanType, Method method, Object[] args, Environment environment) {
        super(target, beanType, method, args, environment);
        this.request = request;
        this.response = response;
    }

    public ServerInvocationContext(HttpServletRequest request, HttpServletResponse response, InvocationContext context) {
        super(context.getTarget(), context.getBeanType(), context.getMethod(), context.getArgs(), context.getEnvironment());
        this.request = request;
        this.response = response;
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public HttpServletResponse getResponse() {
        return response;
    }

    @Override
    public int hashCode() {
        return 31 * super.hashCode() + Objects.hash(request, response);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }
        final ServerInvocationContext other = (ServerInvocationContext) obj;
        return Objects.equals(this.request, other.request)
                && Objects.equals(this.response, other.response);
    }
}
