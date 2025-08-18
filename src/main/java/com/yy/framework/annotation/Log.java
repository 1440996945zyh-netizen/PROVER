package com.yy.framework.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.yy.common.enums.BusinessType;
import com.yy.common.enums.OperateTypeEnum;

/**
 * 自定义日志注解
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface Log {
	
    /**
     * 模块 
     */
    public String title() default "";
    
    /**
     * 业务类型
     */
    public OperateTypeEnum value() default OperateTypeEnum.OTHER;

    /**
     * 功能
     */
    public BusinessType businessType() default BusinessType.OTHER;
}
