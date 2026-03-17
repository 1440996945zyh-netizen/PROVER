package com.yy.ppm.equipment.bean.dto;

import com.yy.ppm.equipment.bean.po.EMaterialCategoryPO;
import lombok.Data;

import java.util.List;

/**
 * 物资类别DTO
 * @author system
 * @version 1.0.0
 * @Description
 */
@Data
public class EMaterialCategoryDTO extends EMaterialCategoryPO {

    private static final long serialVersionUID = 1L;

    /**
     * 子级列表（用于树形结构）
     */
    private List<EMaterialCategoryDTO> children;

    /**
     * 父级名称（用于显示）
     */
    private String parentName;
}

