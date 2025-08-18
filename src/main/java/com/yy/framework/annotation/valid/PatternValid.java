package com.yy.framework.annotation.valid;

import com.yy.framework.annotation.Pattern;
import org.apache.commons.collections4.map.LazyMap;
import org.apache.commons.lang3.StringUtils;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 正则验证器
 *
 * @author
 **/
public class PatternValid implements ConstraintValidator<Pattern, String> {

    private static final Map<String, java.util.regex.Pattern> lazyPattern = new ConcurrentHashMap<>(32);

    private Pattern pattern;

    @Override
    public void initialize(Pattern pattern) {
        this.pattern = pattern;
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (pattern.required() && StringUtils.isBlank(value)) {
            return false;
        }

        if (StringUtils.isNotBlank(value)) {
            java.util.regex.Pattern regex = LazyMap.lazyMap(lazyPattern, () -> {
                return java.util.regex.Pattern.compile(pattern.regexp());
            }).get(pattern.regexp());

            return regex.matcher(value).matches();
        }
        return true;
    }
}
