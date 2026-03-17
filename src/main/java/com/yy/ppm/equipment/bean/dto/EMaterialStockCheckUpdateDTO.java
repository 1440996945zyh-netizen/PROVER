package com.yy.ppm.equipment.bean.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 更新盘点数量DTO
 * @author system
 * @version 1.0.0
 * @Description
 */
@Data
public class EMaterialStockCheckUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 盘点单ID
     */
    private Long checkId;

    /**
     * 盘点明细列表
     */
    private List<EMaterialStockCheckDetailDTO> detailList;
}

