package com.yy.ppm.common.enums;

public enum ScheduleTaskEnum {
    /** 定时任务验证（防止恶意调用） */
    SCHEDULE_TASK_KEY("scheduleTaskKey", "Rever168");

    private String key;
    private String value;

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    ScheduleTaskEnum(String key, String value) {
        this.key = key;
        this.value = value;
    }
}
