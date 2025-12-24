package com.yy.ppm.flowable.bean.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.Set;

/**
 * 加签任务
 */
@Data
public class BpmTaskSignCreateDTO {
    /**
     * 需要加签的任务编号
     */
    private String id;
    /**
     * 加签的用户编号
     */
    private Set<Long> userIds;
    /**
     * 加签类型
     */
    private String type; // 参见 BpmTaskSignTypeEnum 枚举
    /**
     * 加签原因
     */
    private String reason;

}
