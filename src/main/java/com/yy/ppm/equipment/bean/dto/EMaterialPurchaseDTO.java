package com.yy.ppm.equipment.bean.dto;

import com.yy.ppm.equipment.bean.po.EMaterialPurchasePO;
import lombok.Data;

import java.util.List;

/**
 * 物资采购DTO
 * @author system
 * @version 1.0.0
 * @Description
 */
@Data
public class EMaterialPurchaseDTO extends EMaterialPurchasePO {

    private static final long serialVersionUID = 1L;

    /**
     * 明细列表
     */
    private List<EMaterialPurchaseDetailDTO> detailList;

    /**
     * 比价信息列表
     */
    private List<EMaterialPurchaseComparisonDTO> comparisonList;


    private String processStatus;
    private String processStatusLabel;
    private String procInstId;
}

