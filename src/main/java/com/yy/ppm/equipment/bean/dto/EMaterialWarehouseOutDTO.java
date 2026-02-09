package com.yy.ppm.equipment.bean.dto;

import com.yy.ppm.equipment.bean.po.EMaterialWarehouseOutPO;
import lombok.Data;

import java.util.List;

/**
 * 物资出库DTO
 * @author system
 * @version 1.0.0
 * @Description
 */
@Data
public class EMaterialWarehouseOutDTO extends EMaterialWarehouseOutPO {

    private static final long serialVersionUID = 1L;

    /**
     * 明细列表
     */
    private List<EMaterialWarehouseOutDetailDTO> detailList;
}

