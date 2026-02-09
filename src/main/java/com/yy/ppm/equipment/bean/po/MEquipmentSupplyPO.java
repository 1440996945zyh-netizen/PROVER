package com.yy.ppm.equipment.bean.po;

import com.yy.ppm.common.bean.po.BasePO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 设备供货信息PO
 * @author system
 * @version 1.0.0
 * @Description
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class MEquipmentSupplyPO extends BasePO {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 设备ID
     */
    private Long equipId;

    /**
     * 出厂编号
     */
    private String productionCode;

    /**
     * 设备自重
     */
    private String equipWeight;

    /**
     * 发动机功率
     */
    private String enginePower;

    /**
     * 设备购置时间
     */
    private Date equipBuyDate;

    /**
     * 设备使用时间
     */
    private Date equipUseDate;

    /**
     * 供应单位
     */
    private String supplyCompany;

    /**
     * 制造厂家
     */
    private String manufacturer;

    /**
     * 排放标准
     */
    private String emissionStandard;
}

