package com.yy.ppm.flowable.bean.dto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yy.common.flowable.common.KeyValue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 流程实例dto
 */
@Data
@Accessors(chain = true)
public class BpmProcessInstanceDTO implements Serializable {

    /** 流程实例的编号 */
    private String id;

    /** 流程名称 */
    private String name;

    /** 流程摘要 只有流程表单，才有摘要！ */
    private List<KeyValue<String, String>> summary;

    /** 流程分类 */
    private String category;
    /** 流程分类名称 */
    private String categoryName;

    /** 流程实例的状态 */
    private Integer status; // 参见 BpmProcessInstanceStatusEnum 枚举

    /** 发起时间 */
    private Date startTime;

    /** 结束时间 */
    private Date endTime;

    /** 持续时间 */
    private Long durationInMillis;

    /** 提交的表单值 */
    private Map<String, Object> formVariables;

    /** 业务的唯一标识-例如说，请假申请的编号 */
    private String businessKey;

    /**
     * 发起流程的用户
     */
    private UserSimpleBaseDTO startUser;

    /** 流程定义的编号 */
    private String processDefinitionId;
    /**
     * 流程定义
     */
    private BpmProcessDefinitionDTO processDefinition;

    /**
     * 当前审批中的任务
     */
    private List<Task> tasks; // 仅在流程实例分页才返回

    /**
     * 变量实例（动态表单）
     */
    private Map<String, Object> variables;

    /**
     * 发起人自选审批人 Map
     */
    private Map<String, List<Long>> startUserSelectAssignees;

    /** 流程任务 */
    @Data
    public static class Task {

        /** 流程任务的编号 */
        private String id;

        /** 任务名称 */
        private String name;

        /** 任务分配人编号 */
        @JsonIgnore // 不返回，只是方便后续读取，赋值给 assigneeUser
        private Long assignee;

        /** 任务分配人 */
        private UserSimpleBaseDTO assigneeUser;

    }

}
