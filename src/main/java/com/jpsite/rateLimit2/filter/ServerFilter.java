//package com.jpsite.rateLimit2.filter;
//
//import org.jdom.filter.AbstractFilter;
//import org.springframework.web.context.request.RequestAttributes;
//import org.springframework.web.context.request.RequestContextHolder;
//import org.springframework.web.context.request.ServletRequestAttributes;
//
///**
// * 服务端拦截器，仅拦截服务端接口
// */
//public abstract class ServerFilter extends AbstractFilter {
//
//    @Override
//    public boolean supports(final InvocationContext context) {
//        return isServer(context.getEnvironment(), context.getBeanType());
//    }
//
//    protected final ServerInvocationContext getServerInvocationContext(InvocationContext context) {
//        RequestAttributes ra = RequestContextHolder.getRequestAttributes();
//        if (ra == null) {
//            return new ServerInvocationContext(null, null, context);
//        } else {
//            ServletRequestAttributes sra = (ServletRequestAttributes) ra;
//            return new ServerInvocationContext(sra.getRequest(), sra.getResponse(), context);
//        }
//    }
//}
