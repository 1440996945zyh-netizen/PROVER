package com.yy.ppm.flowable.bean.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 不通过流程任务的
 */
@Data
@Accessors(chain = true)
public class BpmTaskRejectDTO {

    /**
     * 任务编号
     */
    private String id;

    /**
     * 审批意见
     */
    private String reason;

}
