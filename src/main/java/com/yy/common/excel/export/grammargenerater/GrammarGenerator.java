package com.yy.common.excel.export.grammargenerater;

import com.yy.common.excel.export.bean.Property;

import java.util.Map;

/**
 * @Author linqi
 * @Description
 * @Date 2023-05-23 09:34
 */
public interface GrammarGenerator {

    boolean supports(Property property);

    Map<String, String> generate(Property property);
}
