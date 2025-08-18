package com.yy.common.excel.export.utils;

import java.util.*;

/**
 * @Author linqi
 * @Description
 * @Date 2023-05-22 14:52
 */
public class ClassUtils {

    public static boolean isPrimitive(Class<?> c1ass) {
        return c1ass.isPrimitive();
    }

    public static boolean isArray(Class<?> c1ass) {
        return c1ass.isArray();
    }

    public static boolean isCollection(Class<?> c1ass) {
        return Collection.class.isAssignableFrom(c1ass);
    }

    public static boolean isList(Class<?> c1ass) {
        return List.class.isAssignableFrom(c1ass);
    }

    public static boolean isSet(Class<?> c1ass) {
        return Set.class.isAssignableFrom(c1ass);
    }

    public static boolean isQueue(Class<?> c1ass) {
        return Queue.class.isAssignableFrom(c1ass);
    }

    public static boolean isMap(Class<?> c1ass) {
        return Map.class.isAssignableFrom(c1ass);
    }
}
