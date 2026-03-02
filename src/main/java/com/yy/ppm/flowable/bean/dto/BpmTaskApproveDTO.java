package com.yy.ppm.flowable.bean.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Map;

/**
 * 通过流程任务
 */
@Data
@Accessors(chain = true)
public class BpmTaskApproveDTO {

    /**
     * 任务编号
     */
    private String id;

    /**
     * 审批意见
     */
    private String reason;

    /**
     * 签名
     */
    private String signPicUrl;

    /**
     * 变量实例（动态表单）
     */
    private Map<String, Object> variables;

    /**
     * 下一个节点审批人
     */
    private Map<String, List<Long>> nextAssignees; // 为什么是 Map，而不是 List 呢？因为下一个节点可能是多个，例如说并行网关的情况

    /**文件id*/
    private List<Long> fileIds;
}
