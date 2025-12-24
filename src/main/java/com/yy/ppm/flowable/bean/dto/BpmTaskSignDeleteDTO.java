package com.yy.ppm.flowable.bean.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

/**
 * 加签任务的删除（减签）
 */
@Data
public class BpmTaskSignDeleteDTO {

    /**
     * 被减签的任务编号
     */
    private String id;
    /**
     * 加签原因
     */
    private String reason;

}
