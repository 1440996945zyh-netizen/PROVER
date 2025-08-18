package com.yy.common.excel.export.utils;

import com.yy.common.excel.export.bean.Property;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

import static com.yy.common.excel.export.enums.Keyword.*;
import static java.lang.String.format;

/**
 * @Author linqi
 * @Description
 * @Date 2023-05-18 13:55
 */
public class PropertyUtils {

    private static final String[] ANYONE_IGNORE_PROPERTYNAME = new String[]{"class"};

    private static final Map<String, String[]> IGNORE_PROPERTYNAME_MAP = new HashMap<String, String[]>() {{
        put("java.util.Collection", new String[]{"empty"});
        put("java.util.Map", new String[]{"empty"});
        put("java.lang.String", new String[]{"empty", "bytes"});
    }};

    /**
     * 从对象中获取所有属性
     *
     * @param object
     * @return
     */
    public static List<Property> getProperties(Object object) {
        BeanInfo beanInfo;
        try {
            beanInfo = Introspector.getBeanInfo(object.getClass());
        } catch (IntrospectionException e) {
            throw new RuntimeException(e);
        }

        PropertyDescriptor[] descriptors = beanInfo.getPropertyDescriptors();
        List<Property> properties = new ArrayList<>();
        for (PropertyDescriptor descriptor : descriptors) {
            Property property = new Property();
            String propertyName = descriptor.getName();
            property.setName(propertyName);

            if (Arrays.asList(ANYONE_IGNORE_PROPERTYNAME).contains(propertyName)) {
                continue;
            }

            List<String> ignorePropertynames = IGNORE_PROPERTYNAME_MAP.keySet().stream()
                    .filter(className -> {
                        Class<?> c1ass;
                        try {
                            c1ass = Class.forName(className);
                        } catch (ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                        return c1ass.isAssignableFrom(object.getClass());
                    })
                    .flatMap(className -> Arrays.stream(IGNORE_PROPERTYNAME_MAP.get(className)))
                    .collect(Collectors.toList());
            if (ignorePropertynames.contains(propertyName)) {
                continue;
            }

            Method readMethod = descriptor.getReadMethod();
            try {
                property.setValue(readMethod.invoke(object));
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }

            Class<?> type = readMethod.getReturnType();
            Type genericType = readMethod.getGenericReturnType();
            String genericTypeName = genericType.getTypeName();
            if (ClassUtils.isArray(type)) {
                property.setGenericTypeNames(new String[]{genericTypeName.substring(0, genericTypeName.length() - 2)});
            } else if (genericTypeName.contains("<")) {
                property.setGenericTypeNames(genericTypeName.substring(genericTypeName.indexOf("<") + 1, genericTypeName.length() - 1).split(", "));
            }

            properties.add(property);
        }

        return properties;
    }

    /**
     * 从数组中获取所有元素作为属性
     *
     * @param object
     * @param genericTypeNames
     * @return
     */
    public static List<Property> getArrayElementProperties(Object object, String[] genericTypeNames) {
        List<Property> chidren = new ArrayList<>();
        if (ClassUtils.isArray(object.getClass())) {
            for (int i = 0; i < ((Object[]) object).length; i++) {
                Property chidrenProperty = new Property();
                chidrenProperty.setIndex(i);
                chidrenProperty.setValue(((Object[]) object)[i]);
                String genericTypeName = genericTypeNames[0];
                if (genericTypeName.contains("[")) {
                    chidrenProperty.setGenericTypeNames(new String[]{genericTypeName.substring(0, genericTypeName.length() - 2)});
                }
                chidren.add(chidrenProperty);
            }
        } else {
            for (int i = 0; i < ((List<?>) object).size(); i++) {
                Property chidrenProperty = new Property();
                chidrenProperty.setIndex(i);
                chidrenProperty.setValue(((List<?>) object).get(i));
                String genericTypeName = genericTypeNames[0];
                if (genericTypeName.contains("<")) {
                    chidrenProperty.setGenericTypeNames(genericTypeName.substring(genericTypeName.indexOf("<") + 1, genericTypeName.length() - 1).split(", "));
                }
                chidren.add(chidrenProperty);
            }
        }
        return chidren;
    }

    /**
     * 递归获取所有子属性
     *
     * @param property
     */
    public static void getChildrenRecursively(Property property) {
        if (property.getValue() == null) {
            return;
        }

        List<Property> children;
        if (ClassUtils.isArray(property.getValue().getClass()) || ClassUtils.isList(property.getValue().getClass())) {
            children = getArrayElementProperties(property.getValue(), property.getGenericTypeNames());
        } else {
            children = getProperties(property.getValue());
        }

        if (children.isEmpty()) {
            return;
        }

        property.setChildren(children);

        for (Property v1 : children) {
            v1.setParent(property);
            getChildrenRecursively(v1);
        }
    }

    /**
     * 获取属性的完全限定名
     *
     * @param property
     */
    public static String getFullyQualifiedName(Property property) {
        StringBuilder grammarBuilder = new StringBuilder();
        while (property != null) {
            if (property.getName() != null) {
                grammarBuilder.insert(0, format("%s%s%s", PROPERTY_START.getCode(), property.getName(), PROPERTY_END.getCode()));
            } else {
                grammarBuilder.insert(0, format("%s%d%s", ELEMENT_START.getCode(), property.getIndex(), ELEMENT_END.getCode()));
            }
            property = property.getParent();
        }

        String grammar = grammarBuilder.toString();
        grammar = grammar.substring(PROPERTY_START.getCode().length());

        return grammar;
    }
}
