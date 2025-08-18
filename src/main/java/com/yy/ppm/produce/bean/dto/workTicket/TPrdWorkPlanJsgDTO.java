package com.yy.ppm.produce.bean.dto.workTicket;

import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @Auther chenfs
 * @Description
 * @Date 2023-10-16 14:08
 */
@Setter
@Getter
public class TPrdWorkPlanJsgDTO {

    /**
     * 详情列表
     */
    private List<TPrdWorkPlanJsgDetailDTO> workPlanList;
}
