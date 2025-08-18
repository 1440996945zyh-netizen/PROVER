package com.yy.ppm.dispatch.bean.dto;

import com.yy.ppm.dispatch.bean.po.TDaynightPlanWorkwarePO;
import lombok.Getter;
import lombok.Setter;

/**
 * 昼夜计划工属具DTO
 * */
@Getter
@Setter
public class TDaynightPlanWorkwareDTO extends TDaynightPlanWorkwarePO {
    //型号名称
    private String modelName;

    //工属具名称
    private String workwareTypeName;
}
