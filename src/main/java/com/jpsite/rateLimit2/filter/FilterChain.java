//package com.jpsite.rateLimit2.filter;
//
//import com.google.common.base.Preconditions;
//import org.aspectj.lang.ProceedingJoinPoint;
//
//import java.util.List;
//
///**
// * 拦截器链,由由系列的FilterChainItem（拦截器链项）组成，执行doFilter方法，将执行拦截器链中所有拦截器filter方法
// *
// * @see FilterChainItem
// */
//public class FilterChain {
//
//    private FilterChainItem header;
//
//    public FilterChain(FilterChainItem header) {
//        this.header = header;
//    }
//
//    /**
//     * 获取拦截器链的header
//     *
//     * @return 拦截器链的header
//     */
//    public FilterChainItem getHeader() {
//        return header;
//    }
//
//    /**
//     * 执行拦截器链中所有拦截器
//     *
//     * @param context 调用环境
//     * @return 结果
//     * @throws Throwable 拦截器执行异常
//     */
//    public Object doFilter(InvocationContext context) throws Throwable {
//        Preconditions.checkNotNull(context);
//        Preconditions.checkState(header != null);
//        return header.doFilter(context);
//    }
//
//    /**
//     * 构造拦截器链
//     *
//     * @param filterList 拦截器列表
//     * @param context    调用环境
//     * @param joinPoint  连接点
//     * @return 拦截器链
//     */
//    public static FilterChain buildFilterChain(final List<Filter> filterList, final InvocationContext context,
//                                                                               final ProceedingJoinPoint joinPoint) {
//        Preconditions.checkNotNull(filterList);
//        Preconditions.checkArgument(!filterList.isEmpty());
//
//        FilterChainItem last = buildLastFilterChainItem(joinPoint);
//        for (int i = filterList.size() - 1; i >= 0; i--) {
//            Filter filter = filterList.get(i);
//            if (filter.supports(context)) {
//                last = new FilterChainItem(filter, last);
//            }
//        }
//
//        final FilterChainItem header = last;
//        return new FilterChain(header);
//    }
//
//    static FilterChainItem buildLastFilterChainItem(final ProceedingJoinPoint joinPoint) {
//        Preconditions.checkArgument(joinPoint != null, "joinPoint != null");
//        Filter lastFilter = new Filter() {
//            @Override
//            public boolean supports(InvocationContext context) {
//                return true;
//            }
//            @Override
//            public Object filter(FilterChainItem next, InvocationContext context) throws Throwable {
//                return joinPoint.proceed();
//            }
//        };
//        return new FilterChainItem(lastFilter, null);
//    }
//}
