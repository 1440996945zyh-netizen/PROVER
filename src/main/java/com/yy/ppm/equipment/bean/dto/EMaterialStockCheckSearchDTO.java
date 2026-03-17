package com.yy.ppm.equipment.bean.dto;

import com.yy.common.page.PageParameter;
import lombok.Data;

import java.util.Date;

/**
 * 物资库存盘点查询DTO
 * @author system
 * @version 1.0.0
 * @Description
 */
@Data
public class EMaterialStockCheckSearchDTO extends PageParameter {

    /**
     * 盘点单号
     */
    private String checkNo;

    /**
     * 仓库ID
     */
    private Long warehouseId;

    /**
     * 仓库名称
     */
    private String warehouseName;

    /**
     * 盘点日期开始
     */
    private Date checkDateStart;

    /**
     * 盘点日期结束
     */
    private Date checkDateEnd;

    /**
     * 盘点状态：0-待盘点，1-盘点中，2-已完成，3-已调整
     */
    private Integer checkStatus;

    /**
     * 盘点人姓名
     */
    private String checkPersonName;

    /**
     * 盘点主题
     */
    private String checkTitle;
}

