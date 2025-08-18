package com.yy.ppm.dispatch.bean.dto;

import com.yy.ppm.dispatch.bean.po.TDayNightPlanPO;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 昼夜计划DTO
 * @author yangcl
 * */
@Getter
@Setter
public class TDayNightPlanDTO extends TDayNightPlanPO {

    private String createName;

    /**
     * 机械配置
     * */
    private List<TDaynightPlanMacDTO> macList;
    /**
     * 工人配置
     * */
    private List<TDayNightPlanWorkerDTO> workerList;
    /**
     * 工属具
     * */
    private List<TDaynightPlanWorkwareDTO> workwareList;
}
