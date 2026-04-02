package com.yy.framework.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ScheduledExecutorFactoryBean;
import org.springframework.scheduling.concurrent.ScheduledExecutorTask;

import java.util.concurrent.DelayQueue;

/**
 * 全局延迟任务异步处理
 *
 * @author gewx
 **/
public final class GlobalDelayQueueTask {

    private static final Logger log = LoggerFactory.getLogger(GlobalDelayQueueTask.class);


    private GlobalDelayQueueTask() {
    }

    private static final int CORE_SIZE = Runtime.getRuntime().availableProcessors();

    private static final GlobalDelayQueueTask INSTANCE = new GlobalDelayQueueTask();

    private static final GlobalThreadPoolTaskExecutor TASK_POOL = GlobalThreadPoolTaskExecutor.getInstance();

    private static final DelayQueue<AbstractTaskBeanDelayed> DELAY_QUEUE = new DelayQueue<>();

    private static final ScheduledExecutorFactoryBean FACTORY = new ScheduledExecutorFactoryBean();

    static {
        ScheduledExecutorTask task = new ScheduledExecutorTask();
        task.setDelay(0);
        task.setFixedRate(false);
        task.setPeriod(15000);
        task.setRunnable(new Runnable() {
            @Override
            public void run() {
                AbstractTaskBeanDelayed taskBean = null;
                do {
                    taskBean = DELAY_QUEUE.poll();
                    if (taskBean != null) {
                        TASK_POOL.execute(taskBean);
                    }
                } while (taskBean != null);
            }
        });

        FACTORY.setScheduledExecutorTasks(task);
        // 调度遇到异常后,调度计划继续执行
        FACTORY.setContinueScheduledExecutionAfterException(true);
        FACTORY.setThreadNamePrefix("YOGA_TASK_DELAY");
        FACTORY.setPoolSize(CORE_SIZE);
        FACTORY.initialize();
    }

    /**
     * 获取单例对象
     *
     * @author gewx
     **/
    public static GlobalDelayQueueTask getInstance() {
        return INSTANCE;
    }

    /**
     * 覆盖任务执行
     *
     * @author gewx
     **/
    public void compareAndSet(AbstractTaskBeanDelayed taskBean) {
        if (DELAY_QUEUE.contains(taskBean)) {
            if(!DELAY_QUEUE.remove(taskBean)){
                log.warn("Failed to remove: {}",taskBean);
            }
            DELAY_QUEUE.add(taskBean);
        } else {
            DELAY_QUEUE.add(taskBean);
        }
    }
}
