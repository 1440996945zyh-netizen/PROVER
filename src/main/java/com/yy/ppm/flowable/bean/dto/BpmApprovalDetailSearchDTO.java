package com.yy.ppm.flowable.bean.dto;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yy.common.page.PageParameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Map;

/**
 * 审批详情SearchDto
 */
@Data
@Accessors(chain = true)
public class BpmApprovalDetailSearchDTO extends PageParameter implements Serializable {

    /**
     * 流程定义的编号
     */
    private String processDefinitionId; // 使用场景：发起流程时，传流程定义 ID

    /**
     * 流程变量
     */
    private Map<String, Object> processVariables; // 使用场景：同 processDefinitionId，用于流程预测

    /**
     * 流程变量
     */
    private String processVariablesStr; // 解决 GET 无法传递对象的问题，最终转换成 processVariables 变量

    /**
     * 流程实例的编号
     */
    private String processInstanceId;  // 使用场景：流程已发起时候传流程实例 ID

    // TODO @芋艿：如果未来 BPMN 增加流程图，它没有发起人节点，会有问题。
    /**
     * 流程活动编号
     */
    private String activityId; // 用于获取表单权限。1）发起流程时，传“发起人节点” activityId 可获取发起人的表单权限；2）从抄送列表界面进来时，传抄送的 activityId 可获取抄送人的表单权限；

    /**
     * 流程任务编号
     */
    private String taskId; // 用于获取表单权限。1）从待审批/已审批界面进来时，传递 taskId 任务编号，可获取任务节点的变得权限

    /**
     * 流程定义的编号和流程实例的编号不能同时为空
     * @return
     */
    @AssertTrue(message = "")
    @JsonIgnore
    public boolean isValidProcessParam() {
        return StrUtil.isNotEmpty(processDefinitionId) || StrUtil.isNotEmpty(processInstanceId);
    }

}
