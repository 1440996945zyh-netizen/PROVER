package com.yy.ppm.equipment.bean.dto;

import com.yy.common.page.PageParameter;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 物资调拨查询 DTO。
 * 用于调拨列表分页查询和条件筛选。
 *
 * @author system
 */
@Getter
@Setter
@ToString
public class EMaterialAllocateSearchDTO extends PageParameter implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 调拨编号。
     */
    private String allocateCode;

    /**
     * 调拨标题。
     */
    private String title;

    /**
     * 调出单位ID。
     */
    private Long fromCompanyId;

    /**
     * 调出仓库ID。
     */
    private Long fromWarehouseId;

    /**
     * 调入单位ID。
     */
    private Long toCompanyId;

    /**
     * 调入仓库ID。
     */
    private Long toWarehouseId;

    /**
     * 执行状态。
     */
    private Integer executeStatus;
}
