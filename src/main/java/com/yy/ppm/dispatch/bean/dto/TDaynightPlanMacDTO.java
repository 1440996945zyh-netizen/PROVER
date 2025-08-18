package com.yy.ppm.dispatch.bean.dto;

import com.yy.ppm.dispatch.bean.po.TDaynightPlanMacPO;
import lombok.Getter;
import lombok.Setter;

/**
 * 昼夜计划机械配置DTO
 * */
@Getter
@Setter
public class TDaynightPlanMacDTO extends TDaynightPlanMacPO {
    //机械类型名称
    private String macTypeName;
    //机械型号名称
    private String modelName;
}
