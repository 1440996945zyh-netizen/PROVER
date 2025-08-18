package com.yy.framework.aspect;

import com.yy.common.util.SecurityUtils;
import com.yy.ppm.common.bean.po.BasePO;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

@Component
@Aspect
public class DbUpdateAspect {

    @Resource
    private SecurityUtils securityUtils;


    @Before("@annotation(com.yy.framework.annotation.Edit) && execution(* com.yy.ppm..*.*Mapper.*(..))")
    public void beforeSave(JoinPoint jp) {
        try {

            Date modifyDate = new Date();
             for (Object po : jp.getArgs())      {
                 if (po instanceof BasePO) {
                     po.getClass().getMethod("setNow", Date.class)
                             .invoke(po, modifyDate);
                     po.getClass().getMethod("setLoginUserId", Long.class)
                             .invoke(po, securityUtils.getLoginUserId());
                     po.getClass().getMethod("setLoginUserName", String.class)
                             .invoke(po, securityUtils.getUserInfo().getUserName());
                 }
                 if (po instanceof Map) {
                     @SuppressWarnings("unchecked")
                     Map<Object, Object> map = (Map<Object, Object>) po;
                     map.put("now", modifyDate);
                     map.put("loginUserId", securityUtils.getLoginUserId());
                     map.put("loginUserName", securityUtils.getUserInfo().getUserName());
                 }
                 if (po instanceof Collection) {
                     // 针对批量插入的情况进行处理
                     Collection<?> elements = (Collection<?>) po;
                     elements.forEach(element -> {
                         if (element instanceof BasePO) {
                             try {
                                 element.getClass().getMethod("setNow", Date.class)
                                         .invoke(element, modifyDate);
                                 element.getClass().getMethod("setLoginUserId", Long.class)
                                         .invoke(element, securityUtils.getLoginUserId());
                                 element.getClass().getMethod("setLoginUserName", String.class)
                                         .invoke(element, securityUtils.getUserInfo().getUserName());
                             } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                                 throw new RuntimeException(e);
                             }
                         }
                         if (element instanceof Map) {
                             @SuppressWarnings("unchecked")
                             Map<Object, Object> map = (Map<Object, Object>) element;
                             map.put("now", modifyDate);
                             map.put("loginUserId", securityUtils.getLoginUserId());
                             map.put("loginUserName", securityUtils.getUserInfo().getUserName());
                         }
                     });
                 }
             }

        } catch (Exception ex) {
            // ex.printStackTrace();
        }
    }

}
