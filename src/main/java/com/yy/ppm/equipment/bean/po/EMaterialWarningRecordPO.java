package com.yy.ppm.equipment.bean.po;

import com.yy.ppm.common.bean.po.BasePO;
import lombok.Data;
import java.math.BigDecimal;


/**
 * 预警消息
 */
@Data
public class EMaterialWarningRecordPO extends BasePO {

    private static final long serialVersionUID = 1L;

    private Long id;

    /**
     * 物资ID
     */
    private Long materialId;

    /**
     * 物资名称
     */
    private String materialName;

    /**
     * 触发预警时库存
     */
    private BigDecimal currentStock;

    /**
     * 预警阈值
     */
    private BigDecimal warningThreshold;

    /**
     * 接收人ID，逗号分隔
     */
    private String receivers;

    /**
     * 处理状态
     * 0-未处理
     * 1-已处理
     */
    private Integer handleStatus;

    /**
     * 处理时间
     */
    private java.util.Date handleTime;

    /**
     * 处理人ID
     */
    private Long handleUserId;
}