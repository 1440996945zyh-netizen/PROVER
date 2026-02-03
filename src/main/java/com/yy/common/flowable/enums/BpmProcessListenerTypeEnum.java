package com.yy.common.flowable.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
@Getter
@AllArgsConstructor
public enum BpmProcessListenerTypeEnum {
    EXECUTION("execution", "执行监听器"),
    TASK("task", "任务监听器");

    private final String type;
    private final String name;
}
