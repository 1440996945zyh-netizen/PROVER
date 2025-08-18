package com.yy.ppm.produce.bean.dto.workTicket;

import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * @Auther chenfs
 * @Description
 * @Date 2023-10-16 14:08
 */
@Setter
@Getter
public class TicketMeasureDTO {


    /**
     * 计划ID
     */
    @NotNull(message = "计划id不能为空")
    private Long workPlanId;
    /**
     * 票货ID
     */
    @NotNull(message = "票货id不能为空")
    private Long cargoInfoId;
    /**
     * 货物代码
     */
    @NotNull(message = "货物代码不能为空")
    private String cargoCode;
    /**
     * 作业子过程
     */
    @NotNull(message = "作业子过程不能为空")
    private String processDetailCode;

}
