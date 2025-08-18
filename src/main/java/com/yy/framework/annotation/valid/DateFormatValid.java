package com.yy.framework.annotation.valid;

import com.yy.common.util.DateUtils;
import com.yy.framework.annotation.DateFormat;
import org.apache.commons.lang3.StringUtils;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * 注解验证器
 *
 * @author
 **/
public class DateFormatValid implements ConstraintValidator<DateFormat, String> {

    private DateFormat
            date;

    @Override
    public void initialize(DateFormat date) {
        this.date = date;
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (date.required() && StringUtils.isBlank(value)) {
            return false;
        }
        if (StringUtils.isNotBlank(value)) {
            return DateUtils.validDate(value, date.value());
        }
        return true;
    }
}
