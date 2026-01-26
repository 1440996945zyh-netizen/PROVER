package com.yy.ppm.flowable.bean.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yy.common.flowable.common.KeyValue;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * 管理后台 - 流程任务 Response VO
 */
@Data
@Accessors(chain = true)
public class BpmTaskDTO {
    /** 任务编号 */
    private String id;

    /** 任务名字 */
    private String name;

    /** 创建时间 */
    private Date createTime;

    /** 结束时间 */
    private Date endTime;

    /** 持续时间 */
    private Long durationInMillis;

    /** 任务状态，参见 BpmTaskStatusEnum 枚举 */
    private Integer status;

    /** 审批理由 */
    private String reason;

    /** 任务负责人编号，不返回，只是方便后续读取，赋值给 ownerUser */
    @JsonIgnore
    private Long owner;
    /** 负责人的用户信息 */
    private UserSimpleBaseDTO ownerUser;

    /** 任务分配人编号，不返回，只是方便后续读取，赋值给 assigneeUser */
    @JsonIgnore
    private Long assignee;
    /** 审核的用户信息 */
    private UserSimpleBaseDTO assigneeUser;

    /** 任务定义的标识 */
    private String taskDefinitionKey;

    /** 所属流程实例编号 */
    private String processInstanceId;
    /** 所属流程实例 */
    private ProcessInstance processInstance;

    /** 父任务编号 */
    private String parentTaskId;
    /** 子任务列表（由加签生成），包含多层子任务 */
    private List<BpmTaskDTO> children;

    /** 表单编号 */
    private Long formId;
    /** 表单名字 */
    private String formName;
    /** 表单的配置，JSON 字符串 */
    private String formConf;
    /** 表单项的数组 */
    private List<String> formFields;
    /** 提交的表单值 */
    private Map<String, Object> formVariables;
    /** 操作按钮设置值 */
    private Map<Integer, OperationButtonSetting> buttonsSetting;

    /** 是否需要签名 */
    private Boolean signEnable;

    /** 是否填写审批意见 */
    private Boolean reasonRequire;

    /** 节点类型，参见 BpmSimpleModelNodeTypeEnum 枚举 */
    private Integer nodeType;

    /**
     * 流程实例
     */
    @Data
    public static class ProcessInstance {

        /** 流程实例编号 */
        private String id;

        /** 流程实例名称 */
        private String name;

        /** 提交时间 */
        private LocalDateTime createTime;

        /** 流程定义的编号 */
        private String processDefinitionId;

        /** 流程摘要，只有流程表单，才有摘要 */
        private List<KeyValue<String, String>> summary;

        /** 发起人的用户信息 */
        private UserSimpleBaseDTO startUser;

    }

    /**
     * 操作按钮设置
     */
    @Data
    public static class OperationButtonSetting {

        /** 显示名称 */
        private String displayName;

        /** 是否启用 */
        private Boolean enable;
    }
}
