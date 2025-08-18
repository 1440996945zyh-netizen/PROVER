package com.yy.ppm.dispatch.bean.dto;

import com.yy.ppm.dispatch.bean.po.TDaynightPlanWorkerPO;
import lombok.Getter;
import lombok.Setter;

/**
 * 昼夜计划工人岗位配置DTO
 * */
@Getter
@Setter
public class TDayNightPlanWorkerDTO extends TDaynightPlanWorkerPO {
    //工人岗位名称
    private String workPostName;
}
