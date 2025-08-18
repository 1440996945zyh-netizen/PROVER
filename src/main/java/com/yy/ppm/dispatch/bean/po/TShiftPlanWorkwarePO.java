package com.yy.ppm.dispatch.bean.po;

import lombok.Data;

import java.io.Serializable;

/**
 * 工班计划-工属具配置信息
 * @author yangcl*/
@Data
public class TShiftPlanWorkwarePO implements Serializable {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 工班计划ID
     */
    private Long shiftPlanId;

    /**
     * 工属具code
     */
    private String workwareCode;

    /**
     * 规格code
     */
    private String specCode;

    /**
     * 数量
     */
    private Integer num;

    /**
     * 数量单位 字典UNITS
     */
    private String numUnit;

    private static final long serialVersionUID = 1L;
}

