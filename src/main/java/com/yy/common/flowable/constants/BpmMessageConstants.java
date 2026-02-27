package com.yy.common.flowable.constants;

import com.yy.ppm.flowable.bean.dto.BpmMessageTemplateDTO;

/**
 * BPM 消息模板常量枚举类
 * 使用 {} 作为参数占位符
 */
public interface BpmMessageConstants {
    /**
     * 待办
     */
    BpmMessageTemplateDTO TASK_CREATED = new BpmMessageTemplateDTO(
            "1_009_100_001", "待办任务提醒", "您有一个新的待办任务：【{}】，发起人：{}", "NOTIFICATION");

    /**
     * 转办
     */
    BpmMessageTemplateDTO TASK_TRANSFER = new BpmMessageTemplateDTO(
            "1_009_100_002", "任务转办提醒", "{} 已将任务【{}】转办给您，请及时处理", "NOTIFICATION");

    /**
     * 委派
     */
    BpmMessageTemplateDTO TASK_DELEGATE = new BpmMessageTemplateDTO(
            "1_009_100_003", "任务委派提醒", "{} 向您委派了任务：【{}】，处理后将回传给委托人", "NOTIFICATION");

    /**
     * 审批通过
     */
    BpmMessageTemplateDTO TASK_COMPLETED = new BpmMessageTemplateDTO(
            "1_009_100_004", "任务审批结果", "您的任务【{}】已审批通过", "MESSAGE");

    /**
     * 驳回
     */
    BpmMessageTemplateDTO TASK_REJECTED = new BpmMessageTemplateDTO(
            "1_009_100_005", "任务驳回提醒", "您的任务【{}】已被驳回，原因：{}", "NOTIFICATION");
}
