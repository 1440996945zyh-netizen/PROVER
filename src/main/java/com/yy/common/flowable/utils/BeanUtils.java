package com.yy.common.flowable.utils;

import cn.hutool.core.bean.BeanUtil;
import com.yy.common.page.Pages;
import com.yy.common.util.PageConverterUtils;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

/**
 * Bean 工具类
 *
 * 1. 默认使用 {@link cn.hutool.core.bean.BeanUtil}
 * 2. 针对复杂的对象转换，可以搜参考 AuthConvert 实现，通过 mapstruct + default 配合实现
 */
public class BeanUtils {
    public static <T> T toBean(Object source, Class<T> targetClass) {
        return BeanUtil.toBean(source, targetClass);
    }

    public static <T> T toBean(Object source, Class<T> targetClass, Consumer<T> peek) {
        T target = toBean(source, targetClass);
        if (target != null) {
            peek.accept(target);
        }
        return target;
    }

    public static <S, T> List<T> toBean(List<S> source, Class<T> targetType) {
        if (source == null) {
            return null;
        }
        return CollectionUtils.convertList(source, s -> toBean(s, targetType));
    }

    public static <S, T> List<T> toBean(List<S> source, Class<T> targetType, Consumer<T> peek) {
        List<T> list = toBean(source, targetType);
        if (list != null) {
            if(peek !=null){
                list.forEach(peek);
            }
        }
        return list;
    }

    public static <S, T> Pages<T> toBean(Pages<S> source, Class<T> targetType) {
        return toBean(source, targetType, null);
    }

    public static <S, T> Pages<T> toBean(Pages<S> source, Class<T> targetType, Consumer<T> peek) {
        if (source == null) {
            return null;
        }
        List<T> list = toBean(source.getPages(), targetType);
        if (peek != null) {
            list.forEach(peek);
        }
        return PageConverterUtils.convert(list, source.getPageNum(), source.getPageSize(), source.getTotalNum());
    }

    public static void copyProperties(Object source, Object target) {
        if (source == null || target == null) {
            return;
        }
        BeanUtil.copyProperties(source, target, false);
    }
}
