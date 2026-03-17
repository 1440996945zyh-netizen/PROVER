package com.yy.ppm.equipment.bean.dto;

import com.yy.common.page.PageParameter;
import lombok.Data;

/**
 * 物资出库申请查询DTO
 * @author system
 * @version 1.0.0
 * @Description
 */
@Data
public class EMaterialOutApplicationSearchDTO extends PageParameter {

    private static final long serialVersionUID = 1L;

    /**
     * 出库单号
     */
    private String warehouseOutNo;

    /**
     * 出库主题
     */
    private String warehouseOutTitle;

    /**
     * 仓库ID
     */
    private Long warehouseId;

    /**
     * 状态
     */
    private String status;

    /**
     * 创建人ID（用于权限控制）
     */
    private Long createBy;
}

