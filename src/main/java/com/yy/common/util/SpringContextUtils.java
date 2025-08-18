package com.yy.common.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;


@Component
public class SpringContextUtils implements ApplicationContextAware {
    public static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext)
            throws BeansException {

        SpringContextUtils.applicationContext = applicationContext;
        //获取环境
        Environment environment = applicationContext.getEnvironment();
        //这个获取的是配置的运行环境，如开发，测试，以及生产
        String[] activeProfiles = environment.getActiveProfiles();
        //这个获取的是当前项目默认的运行环境，一般设置为开发
        String[] defaultProfiles = environment.getDefaultProfiles();
    }

    public static Object getBean(String name) {
        return applicationContext.getBean(name);
    }

    public static <T> T getBean(String name, Class<T> requiredType) {
        return applicationContext.getBean(name, requiredType);
    }

    public static boolean containsBean(String name) {
        return applicationContext.containsBean(name);
    }

    public static boolean isSingleton(String name) {
        return applicationContext.isSingleton(name);
    }

    public static Class<? extends Object> getType(String name) {
        return applicationContext.getType(name);
    }

    /**
     * 获取当前环境
     */
    public static String getActiveProfile() {
        String[] activeProfiles = applicationContext.getEnvironment().getActiveProfiles();
        if (activeProfiles.length == 2) {
            return applicationContext.getEnvironment().getActiveProfiles()[1];
        }
        return "dev";
    }

}

