package com.jpsite.utils;

import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StreamUtils {

    private StreamUtils() {}

    /**
     * 从一个集合中，获取集合中对象的属性值，并返回list
     *
     * @param list
     * @param mapper
     * @return
     */
    public static <T, R> List<R> getObjectFieldValueList(Collection<T> list, Function<? super T, ? extends R> mapper) {
        return list.stream().map(mapper).collect(Collectors.toList());
    }

    /**
     * 从一个集合中，获取集合中对象的属性值，并返回list
     *
     * @param list
     * @param filter
     * @param mapper
     * @return
     */
    public static <T, R> List<R> getObjectFieldValueList(Collection<T> list,
                                                         Predicate<? super T> filter,
                                                         Function<? super T, ? extends R> mapper) {
        return list.stream().filter(filter).map(mapper).collect(Collectors.toList());
    }

    /**
     * 从一个集合中，获取集合中对象的属性值，并返回set
     *
     * @param list
     * @param mapper
     * @return
     */
    public static <T, R> Set<R> getObjectFieldValueSet(Collection<T> list, Function<? super T, ? extends R> mapper) {
        return list.stream().map(mapper).collect(Collectors.toSet());
    }

    /**
     * 从一个集合中，获取集合中对象的属性值，并返回set
     *
     * @param list
     * @param filter
     * @param mapper
     * @return
     */
    public static <T, R> Set<R> getObjectFieldValueSet(Collection<T> list,
                                                       Predicate<? super T> filter,
                                                       Function<? super T, ? extends R> mapper) {
        return list.stream().filter(filter).map(mapper).collect(Collectors.toSet());
    }

    /**
     * 分组，对某个对象的某个字段进行分组
     *
     * @param list
     * @param classifier
     * @return
     */
    public static <R, K> Map<K, List<R>> group(Collection<R> list, Function<? super R, ? extends K> classifier) {
        return list.stream().collect(Collectors.groupingBy(classifier));
    }

    /**
     * 把集合中的一个BigDecimal属性分别相加得到一个BigDecimal的值
     *
     * @param list
     * @param classifier
     * @return
     */
    public static <R> BigDecimal sumObjectNumberValue(Collection<R> list, Function<? super R, BigDecimal> classifier) {
        return list.stream().map(classifier).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * 把集合中的一个BigDecimal属性分别相加得到一个BigDecimal的值
     *
     * @param list
     * @param filter
     * @param classifier
     * @param <R>
     * @return
     */
    public static <R> BigDecimal sumObjectNumberValue(Collection<R> list, Predicate<? super R> filter, Function<? super R, BigDecimal> classifier) {
        return list.stream().filter(filter).map(classifier).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * 把一个集合对象转换成Map，其中Map的value是集合的元素，Map的key是元素的某个属性
     *
     * @param list
     * @param keyMapper
     * @return
     */
    public static <K, R> Map<K, R> listToMap(Collection<R> list, Function<? super R, ? extends K> keyMapper) {
        return list.stream().collect(Collectors.toMap(keyMapper, obj -> obj));
    }

    /**
     * 把一个集合对象转换成Map，其中Map的value是集合的元素，Map的key是元素的某个属性
     *
     * @param list
     * @param keyMapper
     * @param valueMapper
     * @param <T>
     * @param <K>
     * @param <V>
     * @return
     */
    public static <T, K, V> Map<K, V> listToMap(Collection<T> list, Function<? super T, ? extends K> keyMapper, Function<? super T, ? extends V> valueMapper) {
        return list.stream().collect(Collectors.toMap(keyMapper, valueMapper));
    }

    public static <R> List<R> filter(Collection<R> list, Predicate<? super R> predicate) {
        return list.stream().filter(predicate).collect(Collectors.toList());
    }

    public static <T, R> List<R> filter(Collection<T> list, Predicate<? super T> predicate, Function<? super T, ? extends R> mapper) {
        return list.stream().filter(predicate).map(mapper).collect(Collectors.toList());
    }

    /**
     * 自定义排序，把一个集合对象转换成Map，其中Map的value是集合的元素，Map的key是元素的某个属性
     *
     * @param list
     * @param keyMapper
     * @param valueMapper
     * @param <T>
     * @param <K>
     * @param <V>
     * @return
     */
    public static <T, K, V> Map<K, V> listToMap(Collection<T> list, Function<? super T, ? extends K> keyMapper,
                                                Function<? super T, ? extends V> valueMapper, Comparator<? super T> comparator) {
        return list.stream().sorted(comparator).collect(Collectors.toMap(keyMapper, valueMapper, (oldVal, newVal) -> newVal, LinkedHashMap::new));
    }

    /**
     * 把一个由逗号分隔的字符串转换成int集合
     * 注意：是去重的
     *
     * @param value
     * @return
     */
    public static List<Integer> convertToIntList(String value) {
        return convertToList(value, Integer::valueOf);
    }

    public static <T> List<T> convertToList(String value, Function<String, ? extends T> mapper) {
        if (StringUtils.isBlank(value)) {
            return Collections.emptyList();
        }
        return Stream.of(value.split(",")).map(mapper).distinct().collect(Collectors.toList());
    }

    /**
     * 获取最大值
     * @param list
     * @param decimalMapper
     * @param <T>
     * @return 最大值是BigDecimal类型
     */
    public static <T> BigDecimal max(List<T> list, Function<T,BigDecimal> decimalMapper){
        return list.stream().map(decimalMapper).reduce(BigDecimal :: max)
                .orElseThrow(() -> new RuntimeException("未获取到最大值"));
    }
}
