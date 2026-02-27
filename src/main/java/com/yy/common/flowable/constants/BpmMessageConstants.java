package com.yy.common.flowable.constants;

import com.yy.common.enums.WebsocketEnum;
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
            "1_009_100_001", "待办任务提醒", "您有一个新的待办任务：【{}】，发起人：{}", WebsocketEnum.NOTICE_TYPE.getCode());

    /**
     * 转办
     */
    BpmMessageTemplateDTO TASK_TRANSFER = new BpmMessageTemplateDTO(
            "1_009_100_002", "任务转办提醒", "{} 已将任务【{}】转办给您，请及时处理", WebsocketEnum.NOTICE_TYPE.getCode());

    /**
     * 委派
     */
    BpmMessageTemplateDTO TASK_DELEGATE = new BpmMessageTemplateDTO(
            "1_009_100_003", "任务委派提醒", "{} 向您委派了任务：【{}】，处理后将回传给委托人", WebsocketEnum.NOTICE_TYPE.getCode());

    /**
     * 审批通过
     */
    BpmMessageTemplateDTO TASK_COMPLETED = new BpmMessageTemplateDTO(
            "1_009_100_004", "审批通过提醒", "{} 已同意您的【{}】申请（节点：{}）", WebsocketEnum.NOTICE_TYPE.getCode());

    /**
     * 驳回
     */
    BpmMessageTemplateDTO TASK_REJECTED = new BpmMessageTemplateDTO(
            "1_009_100_005", "任务驳回提醒", "您的任务【{}】已被驳回，原因：{}", WebsocketEnum.NOTICE_TYPE.getCode());

    /**
     * 抄送
     */
    BpmMessageTemplateDTO TASK_COPY = new BpmMessageTemplateDTO(
            "1_009_100_006", "任务抄送提醒", "{} 向您抄送了任务，请及时查看", WebsocketEnum.NOTICE_TYPE.getCode());

    /**
     * 退回
     */
    BpmMessageTemplateDTO TASK_RETURN = new BpmMessageTemplateDTO(
            "1_009_100_007", "任务退回提醒", "{} 已将任务【{}】退回给您，原因：{}", WebsocketEnum.NOTICE_TYPE.getCode());

    /**
     * 流程全员通过完结
     */
    BpmMessageTemplateDTO PROCESS_COMPLETED = new BpmMessageTemplateDTO(
            "1_009_100_008", "流程完结提醒", "您的【{}】已全部审批通过，请及时查看！", WebsocketEnum.NOTICE_TYPE.getCode());
}
