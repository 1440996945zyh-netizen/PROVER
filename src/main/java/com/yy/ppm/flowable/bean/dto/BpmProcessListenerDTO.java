package com.yy.ppm.flowable.bean.dto;

import com.yy.ppm.flowable.bean.po.BpmBusinessConfigPO;
import com.yy.ppm.flowable.bean.po.BpmProcessListenerPO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.io.Serializable;

/**
 * BPM 流程监听器 DTO
 *
 */
@Data // 3. 添加 @Data 注解
@EqualsAndHashCode(callSuper = true)
public class BpmProcessListenerDTO extends BpmProcessListenerPO implements Serializable {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "13089")
    private Long id;

    @Schema(description = "监听器名字", requiredMode = Schema.RequiredMode.REQUIRED, example = "赵六")
    @NotEmpty(message = "监听器名字不能为空")
    private String listenerName;

    @Schema(description = "监听器类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "execution")
    @NotEmpty(message = "监听器类型不能为空")
    private String listenerTypeCode;
    private String listenerTypeName;



    @Schema(description = "监听器状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "监听器状态不能为空")
    private Integer listenerStatus;

    @Schema(description = "监听事件", requiredMode = Schema.RequiredMode.REQUIRED, example = "start")
    @NotEmpty(message = "监听事件不能为空")
    private String listenerEventCode;
    private String listenerEventName;

    @Schema(description = "监听器值类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "class")
    @NotEmpty(message = "监听器值类型不能为空")
    private String listenerValueTypeCode;
    private String listenerValueTypeName;

    @Schema(description = "监听器值", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "监听器值不能为空")
    private String listenerValue;
}
