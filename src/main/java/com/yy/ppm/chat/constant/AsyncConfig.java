package com.yy.ppm.chat.constant;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {

    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 配置线程池参数，避免任务排队超时
        executor.setCorePoolSize(10); // 核心线程数
        executor.setMaxPoolSize(20);  // 最大线程数
        executor.setQueueCapacity(50); // 队列容量
        executor.setKeepAliveSeconds(60); // 空闲线程存活时间
        executor.setThreadNamePrefix("ChatAsync-"); // 线程名前缀，方便排查

        // 关键：设置线程装饰器，传递Security上下文
        executor.setTaskDecorator(runnable -> {
            // 获取当前线程的Security上下文
            org.springframework.security.core.context.SecurityContext context = SecurityContextHolder.getContext();
            return () -> {
                try {
                    // 将主线程的上下文设置到异步线程中
                    SecurityContextHolder.setContext(context);
                    runnable.run();
                } finally {
                    // 执行完成后清除上下文，避免内存泄漏
                    SecurityContextHolder.clearContext();
                }
            };
        });

        executor.initialize();
        return executor;
    }
}
