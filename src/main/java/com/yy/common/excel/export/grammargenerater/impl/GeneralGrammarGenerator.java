package com.yy.common.excel.export.grammargenerater.impl;

import com.yy.common.excel.export.bean.Property;
import com.yy.common.excel.export.grammargenerater.GrammarGenerator;
import com.yy.common.excel.export.utils.PropertyUtils;

import java.util.Collections;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.EMPTY;

/**
 * @Author linqi
 * @Description
 * @Date 2023-05-23 11:47
 */
public class GeneralGrammarGenerator implements GrammarGenerator {

    @Override
    public boolean supports(Property property) {
        return true;
    }

    @Override
    public Map<String, String> generate(Property property) {
        String fullyQualifiedName = PropertyUtils.getFullyQualifiedName(property);
        return Collections.singletonMap(fullyQualifiedName, property.getValue() == null ? EMPTY : String.valueOf(property.getValue()));
    }
}
