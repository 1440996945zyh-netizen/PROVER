package com.yy.ppm.produce.bean.dto.workTicket;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

/**
 * @Auther chenfs
 * @Description
 * @Date 2023-10-16 14:08
 */
@Setter
@Getter
public class TPrdWorkPlanJsgDetailDTO {

    /**
     * 计划ID
     */
    private Long planId;
    private List<TPrdWorkTiTckInfoDTO> infoDTOList;


}
