package com.yy.ppm.equipment.bean.dto;

import com.yy.ppm.equipment.bean.po.ECostSettlementApplyPO;
import com.yy.ppm.equipment.bean.po.EMaterialWarningConfigPO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author FanQi
 * @data 2026/3/20 10:34
 * @version 1.0
 * @Description 物资预警配置DTO
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class EMaterialWarningConfigDTO extends EMaterialWarningConfigPO {

    private static final long serialVersionUID = 1L;


    /**
     * 物资名称
     */
    private String materialName;

    /**
     * 规格型号
     */
    private String specificationModel;

    /**
     * 计量单位
     */
    private String unitName;

    /**
     * 品牌
     */
    private String brand;

    /**
     * 状态 1-启用 0-禁用
     */
    private String statusLabel;

    /**
     * 预警接收人
     */
    private String receiverNames;
}
