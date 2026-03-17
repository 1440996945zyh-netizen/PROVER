package com.yy.ppm.equipment.bean.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 物资代码树结构DTO
 * @author system
 */
@Data
public class EMaterialCodeTreeDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 类别ID
     */
    private Long categoryId;

    /**
     * 类别名称
     */
    private String categoryName;

    /**
     * 类别路径
     */
    private String categoryPath;

    /**
     * 子类别列表
     */
    private List<EMaterialCodeTreeDTO> children;

    /**
     * 物资代码列表
     */
    private List<EMaterialCodeDTO> materialCodeList;
}

