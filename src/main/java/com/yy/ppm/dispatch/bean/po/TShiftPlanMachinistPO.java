package com.yy.ppm.dispatch.bean.po;

import com.yy.ppm.common.bean.po.BasePO;
import lombok.Data;

import java.io.Serializable;

/**
 * 工班计划-机械编号*/
@Data
public class TShiftPlanMachinistPO extends BasePO implements Serializable {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 计划ID
     */
    private Long shiftPlanId;

    /**
     * 机械编号
     */
    private String macCode;

    /**
     * 机械名称
     */
    private String macName;

    /**
     * 机械类型
     */
    private String macType;

    /**
     * 机械型号
     */
    private String macModel;

    /**
     * 自有机械 字典OWNER_TYPE
     */
    private String ownerType;


    private static final long serialVersionUID = 1L;
}

