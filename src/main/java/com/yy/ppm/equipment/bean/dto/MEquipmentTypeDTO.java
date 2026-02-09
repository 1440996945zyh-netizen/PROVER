package com.yy.ppm.equipment.bean.dto;

import com.yy.ppm.equipment.bean.po.MEquipmentTypePO;
import lombok.Data;

import java.util.List;

/**
 * 设备类型分类DTO
 * @author system
 * @version 1.0.0
 * @Description
 */
@Data
public class MEquipmentTypeDTO extends MEquipmentTypePO {

    private static final long serialVersionUID = 1L;

    /**
     * 子级列表（用于树形结构）
     */
    private List<MEquipmentTypeDTO> children;

    /**
     * 父级名称（用于显示）
     */
    private String parentName;
}

