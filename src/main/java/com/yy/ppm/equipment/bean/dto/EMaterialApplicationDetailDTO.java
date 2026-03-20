package com.yy.ppm.equipment.bean.dto;

import com.yy.ppm.equipment.bean.po.EMaterialApplicationDetailPO;
import lombok.Data;

/**
 * 物资申报明细DTO
 * @author system
 * @version 1.0.0
 * @Description
 */
@Data
public class EMaterialApplicationDetailDTO extends EMaterialApplicationDetailPO {

    private static final long serialVersionUID = 1L;

    /**
     * 申请单号（关联物资申报表）
     */
    private String applicationNo;

    /**
     * 申报主题（关联物资申报表）
     */
    private String applicationTitle;

    /**
     * 申报类型名称（关联物资申报表）
     */
    private String applicationTypeName;

    /**
     * 物资类别名称（关联物资代码表和物资类别表）
     */
    private String categoryName;

    /**
     * 库存数量（根据物资ID和仓库ID查询）
     */
    private java.math.BigDecimal stockQuantity;

    /**
     * 可用库存数量（根据物资ID和仓库ID查询）
     */
    private java.math.BigDecimal availableInventory;
}

