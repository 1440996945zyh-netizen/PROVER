package com.yy.ppm.dispatch.bean.po;

import lombok.Data;

import java.io.Serializable;

/**
 * 工班计划-作业工艺-机械配置
 * @author yangcl
 * */
@Data
public class TShiftPlanMacPO implements Serializable {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 计划ID
     */
    private Long shiftPlanId;

    /**
     * 机械类型
     */
    private String macTypeCode;

    /**
     * 机械型号
     */
    private String macModelCode;

    /**
     * 数量
     */
    private String num;

    /**
     * 机械编号 只有门机编号 多个,分割
     */
    private String macCodes;

    /**
     * 机械名称 只有门机编号 多个,分割
     */
    private String macNames;

    private static final long serialVersionUID = 1L;
}

