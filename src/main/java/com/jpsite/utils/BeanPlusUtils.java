package com.jpsite.utils;

import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * 只做增强的BeanUtils
 *
 * @author jiangpeng
 * @date 2020/1/818:12
 */
public class BeanPlusUtils extends BeanUtils {
    public static <S, T> List<T> copyListProperties(List<S> sources, Supplier<T> target) {
        return copyListProperties(sources, target, null);
    }

    public static <S, T> T copySingleProperties(S source, Supplier<T> target) {
        return copySingleProperties(source, target, null);
    }

    /**
     * 使用场景：Entity、Bo、Vo层数据的复制，因为BeanUtils.copyProperties只能给目标对象的属性赋值，却不能在List集合下循环赋值，因此添加该方法
     * 如：List<AdminEntity> 赋值到 List<AdminVo> ，List<AdminVo>中的 AdminVo 属性都会被赋予到值
     * S: 数据源类 ，T: 目标类::new(eg: AdminVo::new)
     */
    public static <S, T> List<T> copyListProperties(List<S> sources, Supplier<T> target,
                                                    ColaBeanUtilsCallBack<S, T> callBack) {
        List<T> list = new ArrayList<>(sources.size());
        for (S source : sources) {
            T t = target.get();
            copyProperties(source, t);
            list.add(t);
            if (callBack != null) {
                // 回调
                callBack.callBack(source, t);
            }
        }
        return list;
    }

    public static <S, T> T copySingleProperties(S source, Supplier<T> target, ColaBeanUtilsCallBack<S, T> callBack) {
        T t = target.get();
        copyProperties(source, t);
        if (callBack != null) {
            // 回调
            callBack.callBack(source, t);
        }
        return t;
    }

    @FunctionalInterface
    public interface ColaBeanUtilsCallBack<S, T> {

        void callBack(S t, T s);
    }

    public static <T> T getValueWithDefault(T value, T defaultValue) {
        return value == null ? defaultValue : value;
    }
}
