//package com.jpsite.rateLimit2.filter;
//
//public class FilterChainItem {
//
//    private final Filter filter;
//
//    private final FilterChainItem next;
//
//    public FilterChainItem(Filter filter, FilterChainItem next) {
//        this.filter = filter;
//        this.next = next;
//    }
//
//    public Object doFilter(InvocationContext context) throws Throwable {
//        return filter.filter(next, context);
//    }
//
//    public Filter getFilter() {
//        return filter;
//    }
//
//    public FilterChainItem getNext() {
//        return next;
//    }
//}
