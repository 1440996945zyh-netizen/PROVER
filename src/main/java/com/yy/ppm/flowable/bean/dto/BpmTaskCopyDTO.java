package com.yy.ppm.flowable.bean.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.Collection;

/**
 * 抄送流程任务
 */
@Data
public class BpmTaskCopyDTO {
    /**
     * 任务编号
     */
    private String id;
    /**
     * 抄送的用户编号数组
     */
    private Collection<Long> copyUserIds;
    /**
     * 抄送意见
     */
    private String reason;
}
