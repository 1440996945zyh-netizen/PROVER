package com.yy.framework.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * 修改标志，有此标志Dao.xxx参数自动设置登录信息
 */
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
public @interface Edit {
}
