package com.yy.ppm.flowable.bean.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 委派流程任务
 */
@Data
public class BpmTaskDelegateDTO {
    /**
     * 任务编号
     */
    private String id;
    /**
     * 被委派人 ID
     */
    private Long delegateUserId;
    /**
     * 委派原因
     */
    private String reason;

}
