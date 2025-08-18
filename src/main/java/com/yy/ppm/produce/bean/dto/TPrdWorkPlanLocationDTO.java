package com.yy.ppm.produce.bean.dto;


import com.yy.ppm.produce.bean.po.TPrdWorkPlanLocationPO;
import lombok.Data;

/**
 * @ClassName 作业计划位置表(TPrdWorkPlanLocation)DTO
 * @author yy
 * @version 1.0.0
 * @Description
 * @createTime 2023年07月21日 16:22:00
 */
@Data
public class TPrdWorkPlanLocationDTO extends TPrdWorkPlanLocationPO {

    private static final long serialVersionUID = 572690686616045869L;

    /** 垛位场、区、垛拼接 */
    private String massNameFull;

}
