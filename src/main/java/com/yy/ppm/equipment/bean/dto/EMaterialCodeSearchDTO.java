package com.yy.ppm.equipment.bean.dto;

import com.yy.common.page.PageParameter;
import lombok.Data;

import java.io.Serializable;

/**
 * 物资代码查询DTO
 * @author system
 * @version 1.0.0
 * @Description
 */
@Data
public class EMaterialCodeSearchDTO extends PageParameter implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 类别ID
     */
    private Long categoryId;

    /**
     * 物资代码
     */
    private String materialCode;

    /**
     * 物资名称
     */
    private String materialName;

    /**
     * 物资类别级别
     */
    private Integer categoryLevel;
}

