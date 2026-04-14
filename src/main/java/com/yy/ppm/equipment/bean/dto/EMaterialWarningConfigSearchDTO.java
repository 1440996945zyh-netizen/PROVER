package com.yy.ppm.equipment.bean.dto;

import com.yy.common.page.PageParameter;
import lombok.Data;

import java.io.Serializable;

/**
 * @author FanQi
 * @data 2026/3/20 10:35
 * @version 1.0
 * @Description 物资预警配置SearchDTO
 */

@Data
public class EMaterialWarningConfigSearchDTO extends PageParameter implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 物资名称
     */
    private String materialName;

    /**
     * 状态 1-启用 0-禁用
     */
    private String status;

    /**
     * 物资类别ID（三级类别）
     */
    private Long categoryId;
}
