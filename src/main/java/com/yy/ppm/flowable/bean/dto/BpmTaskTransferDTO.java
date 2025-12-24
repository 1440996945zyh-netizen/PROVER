package com.yy.ppm.flowable.bean.dto;
import lombok.Data;

/**
 * 流程任务的转办
 */
@Data
public class BpmTaskTransferDTO {
    /**
     * 任务编号
     */
    private String id;
    /**
     * 新审批人的用户编号
     */
    private Long assigneeUserId;
    /**
     * 转办原因
     */
    private String reason;

}
