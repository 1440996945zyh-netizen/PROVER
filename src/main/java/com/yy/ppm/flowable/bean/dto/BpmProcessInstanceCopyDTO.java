package com.yy.ppm.flowable.bean.dto;

import com.yy.common.flowable.common.KeyValue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 流程实例抄送
 */
@Data
public class BpmProcessInstanceCopyDTO {
    /**
     * 抄送主键
     */
    private Long id;
    /**
     * 发起人
     */
    private UserSimpleBaseDTO startUser;
    /**
     * 流程实例编号
     */
    private String processInstanceId;
    /**
     * 流程实例的名称
     */
    private String processInstanceName;
    /**
     * 流程实例的发起时间
     */
    private LocalDateTime processInstanceStartTime;
    /**
     * 流程活动的编号
     */
    private String activityId;
    /**
     * 流程活动的名字
     */
    private String activityName;
    /**
     * 流程活动的编号
     */
    private String taskId;
    /**
     * 抄送人意见
     */
    private String reason;
    /**
     * 创建人
     */
    private UserSimpleBaseDTO createUser;
    /**
     * 抄送时间
     */
    private LocalDateTime createTime;
    /**
     * 流程摘要
     */
    private List<KeyValue<String, String>> summary;

}
