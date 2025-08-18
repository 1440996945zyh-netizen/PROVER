package com.yy.ppm.master.bean.dto;
import java.lang.annotation.*;

/**
 * 字段备注注解
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface FieldRemark {
    String value() default "";      // 字段简要备注
    String description() default ""; // 字段详细描述
}
