package com.yy.ppm.flowable.bean.dto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 审批详情dto
 */
@Data
@Accessors(chain = true)
public class BpmApprovalDetailDTO implements Serializable {

    /**
     * 流程实例的状态
     */
    private Integer status; // 参见 BpmProcessInstanceStatusEnum 枚举

    /**
     * 活动节点列表
     */
    private List<ActivityNode> activityNodes;

    /**
     * 表单字段权限
     */
    private Map<String, String> formFieldsPermission;

    /**
     * 待办任务
     */
    private BpmTaskDTO todoTask;

    /**
     * 所属流程定义信息
     */
    private BpmProcessDefinitionDTO processDefinition;

    /**
     * 所属流程实例信息
     */
    private BpmProcessInstanceDTO processInstance;

    /**
     * 活动节点信息
     */
    @Data
    @Accessors(chain = true)
    public static class ActivityNode {

        /**
         * 节点编号
         */
        private String id;

        /**
         * 节点名称
         */
        private String name;

        /**
         * 节点类型
         */
        private Integer nodeType; // 参见 BpmSimpleModelNodeType 枚举

        /**
         * 节点状态
         */
        private Integer status; // 参见 BpmTaskStatusEnum 枚举

        /**
         * 节点的开始时间
         */
        private LocalDateTime startTime;

        /**
         * 节点的结束时间
         */
        private LocalDateTime endTime;

        /**
         * 审批节点的任务信息
         */
        private List<ActivityNodeTask> tasks;

        /**
         * 候选人策略
         */
        private Integer candidateStrategy; // 参见 BpmTaskCandidateStrategyEnum 枚举。主要用于发起时，审批节点、抄送节点自选

        /**
         * 候选人用户 ID 列表
         */
        @JsonIgnore // 不返回，只是方便后续读取，赋值给 candidateUsers
        private List<Long> candidateUserIds;

        /**
         * 候选人用户列表
         */
        private List<UserSimpleBaseDTO> candidateUsers; // 只包含未生成 ApprovalTaskInfo 的用户列表

        /**
         * 流程编号
         */
        private String processInstanceId; // 当且仅当，该节点是子流程节点时，才会有值（CallActivity 的 calledProcessInstanceId 字段）

    }

    /**
     * 活动节点的任务信息
     */
    @Data
    @Accessors(chain = true)
    public static class ActivityNodeTask {

        /**
         * 任务编号
         */
        private String id;

        /**
         * 任务所属人编号
         */
        @JsonIgnore // 不返回，只是方便后续读取，赋值给 ownerUser
        private Long owner;

        /**
         * 任务所属人
         */
        private UserSimpleBaseDTO ownerUser;

        /**
         * 任务分配人编号
         */
        @JsonIgnore // 不返回，只是方便后续读取，赋值给 assigneeUser
        private Long assignee;

        /**
         * 任务分配人
         */
        private UserSimpleBaseDTO assigneeUser;

        /**
         * 任务状态
         */
        private Integer status;  // 参见 BpmTaskStatusEnum 枚举

        /**
         * 审批意见
         */
        private String reason;

        /**
         * 签名
         */
        private String signPicUrl;

    }

}
