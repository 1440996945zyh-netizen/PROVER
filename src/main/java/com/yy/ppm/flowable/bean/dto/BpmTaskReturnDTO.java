package com.yy.ppm.flowable.bean.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 退回流程任务
 */
@Data
@Accessors(chain = true)
public class BpmTaskReturnDTO {
    /**
     * 任务编号
     */
    private String id;
    /**
     * 退回到的任务 Key
     */
    private String targetTaskDefinitionKey;
    /**
     * 退回意见
     */
    private String reason;

}
