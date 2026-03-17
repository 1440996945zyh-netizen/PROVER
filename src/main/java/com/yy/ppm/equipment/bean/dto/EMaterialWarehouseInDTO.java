package com.yy.ppm.equipment.bean.dto;

import com.yy.ppm.equipment.bean.po.EMaterialWarehouseInPO;
import lombok.Data;

import java.util.List;

/**
 * 物资入库DTO
 * @author system
 * @version 1.0.0
 * @Description
 */
@Data
public class EMaterialWarehouseInDTO extends EMaterialWarehouseInPO {

    private static final long serialVersionUID = 1L;

    /**
     * 明细列表
     */
    private List<EMaterialWarehouseInDetailDTO> detailList;

    /**
     * 附件ID列表
     */
    private List<Long> fileIds;
}

