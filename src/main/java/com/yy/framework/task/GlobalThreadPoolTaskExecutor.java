package com.yy.framework.task;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 全局任务异步处理
 *
 * @author
 **/
public final class GlobalThreadPoolTaskExecutor {

    private GlobalThreadPoolTaskExecutor() {
    }

    private static final int CORE_SIZE = Runtime.getRuntime().availableProcessors();

    private static final ThreadPoolTaskExecutor POOLTASKEXECUTOR = new ThreadPoolTaskExecutor();

    static {
        POOLTASKEXECUTOR.setQueueCapacity(Integer.MAX_VALUE);
        POOLTASKEXECUTOR.setCorePoolSize(CORE_SIZE);
        POOLTASKEXECUTOR.setMaxPoolSize(CORE_SIZE);
        POOLTASKEXECUTOR.setThreadNamePrefix("BUSINESS_TASK_");
        POOLTASKEXECUTOR.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardPolicy());
        POOLTASKEXECUTOR.initialize();

        POOLTASKEXECUTOR.getThreadPoolExecutor().prestartAllCoreThreads();
    }

    private static final GlobalThreadPoolTaskExecutor INSTANCE = new GlobalThreadPoolTaskExecutor();

    public static GlobalThreadPoolTaskExecutor getInstance() {
        return INSTANCE;
    }

    public void execute(AbstractTaskBean taskBean) {
        POOLTASKEXECUTOR.execute(taskBean);
    }

    public void execute(AbstractTaskBeanDelayed taskBean) {
        POOLTASKEXECUTOR.execute(taskBean);
    }

    public void execute(Runnable runTask) {
        POOLTASKEXECUTOR.execute(runTask);
    }

    public <T> Future<T> execute(Callable<T> runTask) {
        Future<T> future = POOLTASKEXECUTOR.submit(runTask);
        return future;
    }

    public ListenableFuture<?> submitListenable(Runnable runTask) {
        return POOLTASKEXECUTOR.submitListenable(runTask);
    }

    public <T> ListenableFuture<T> submitListenable(Callable<T> runTask) {
        return POOLTASKEXECUTOR.submitListenable(runTask);
    }
}
