package com.yy.framework.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * 标记注解:线程安全
 *
 * @author
 **/
@Target({ElementType.TYPE})
public @interface ThreadSafe {

}
