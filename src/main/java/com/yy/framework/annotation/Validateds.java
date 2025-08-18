package com.yy.framework.annotation;

import com.yy.framework.annotation.valid.ValidatedsValid;
import org.apache.commons.lang3.StringUtils;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * DTO校验注解
 *
 * @author
 **/
@Retention(RUNTIME)
@Target(FIELD)
@Constraint(validatedBy = {ValidatedsValid.class})
public @interface Validateds {

    String value() default StringUtils.EMPTY;

    String message() default "校验不通过~";

    boolean required() default false;

    /**
     * 忽略模式: true-传递的忽略,false=未传递的忽略
     **/
    boolean ignoreMode() default true;

    /**
     * 忽略的数组
     **/
    String[] ignore() default {};

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
