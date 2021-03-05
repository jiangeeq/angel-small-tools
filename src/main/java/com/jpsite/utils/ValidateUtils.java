//package com.jpsite.utils;
//
//import com.ppmoney.ppmon.celebi.contract.BusinessException;
//import com.ppmoney.ppmon.celebi.contract.Response;
//
//import java.util.function.Consumer;
//import java.util.function.Supplier;
//
///**
// * 规则校验工具类
// *
// * @author jiangpeng
// * @date 2019/12/2015:01
// */
//public class ValidateUtils {
//    /**
//     * 该方法抛出的异常会被 celebi 捕捉到并包装成Response返回
//     */
//    public static void validateForResponse(boolean condition, String messageFormat, Object... args) {
//        if (condition) {
//            throw new BusinessException(Response.BAD_REQUEST, String.format(messageFormat, args));
//        }
//    }
//
//    public static void validateForResponse(boolean condition, String message) {
//        if (condition) {
//            throw new BusinessException(Response.BAD_REQUEST, message);
//        }
//    }
//    public static <T> T validateExecute(boolean condition, Supplier<T> supplier, Supplier<T> supplier2) {
//        if (condition) {
//            return supplier.get();
//        } else{
//            return supplier2.get();
//        }
//    }
//    public static <T> T validateExecute(boolean condition, Supplier<T> supplier) {
//        if (condition) {
//            return supplier.get();
//        }
//        return null;
//    }
//
//    /**
//     * 无返回值的函数判断
//     *
//     * @param condition 条件表达式
//     * @param consumer  无返回值的函数
//     * @param <T>       对象类型
//     */
//    public static <T> void validateExecute(boolean condition, Consumer<T> consumer) {
//        if (condition) {
//            consumer.accept(null);
//        }
//    }
//
//}
