package com.yy.framework.task;

/**
 * 任务Bean
 *
 * @author
 **/
public abstract class AbstractTaskBean implements Runnable {

    /**
     * 任务Id
     **/
    private String taskId;

    /**
     * 任务名称
     **/
    private String taskName;

    /**
     * 执行任务
     **/
    private Runnable task;

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public Runnable getTask() {
        return task;
    }

    public void setTask(Runnable task) {
        this.task = task;
    }
}
