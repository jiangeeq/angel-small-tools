//package com.jpsite.rateLimit2.filter;
//
//public interface Filter {
//
//    /**
//     * 判断是否被当前拦截器拦截
//     *
//     * @param context 拦截器链中的上下文
//     * @return 返回true，表示当前执行方法将被当前拦截器拦截；反之，返回false
//     */
//    boolean supports(final InvocationContext context);
//
//    /**
//     * 实现拦截器的业务逻辑，当执行完拦截器的业务逻辑后，判断是否直接返回结果。
//     * 如果直接返回结果:{@link Response}或{@link PagedResponse}
//     * 否则进入拦截器链中下一个拦截器<pre>next.doFilter(context);</pre>
//     *
//     * @param next    拦截器链中的下一个Filter
//     * @param context 拦截器链中的上下文
//     * @return 返回当前拦截器的执行结果，或者返回下一个拦截器的执行结果
//     */
//    Object filter(final FilterChainItem next, final InvocationContext context) throws Throwable;
//}
