package com.yy.ppm.equipment.bean.po;


import com.yy.ppm.common.bean.po.BasePO;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author FanQi
 * @data 2026/3/20 10:23
 * @version 1.0
 * @Description 物资预警配置PO
 */


@Data
public class EMaterialWarningConfigPO extends BasePO {
    private static final long serialVersionUID = 1L;
    private Long id;

    /**
     * 物资ID
     */
    private Long materialId;
    /**
     * 预警阈值
     */
    private BigDecimal warningThreshold;
    /**
     * 预警接收人ID
     */
    private String receivers;
    /**
     * 状态 1-启用 0-禁用
     */
    private String status;

}
