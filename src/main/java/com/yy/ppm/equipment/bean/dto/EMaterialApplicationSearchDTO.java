package com.yy.ppm.equipment.bean.dto;

import com.yy.common.page.PageParameter;
import lombok.Data;

import java.io.Serializable;

/**
 * 物资申报查询DTO
 * @author system
 * @version 1.0.0
 * @Description
 */
@Data
public class EMaterialApplicationSearchDTO extends PageParameter implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 申请单号
     */
    private String applicationNo;

    /**
     * 申报主题
     */
    private String applicationTitle;

    /**
     * 申报类型编码
     */
    private String applicationTypeCode;

    /**
     * 部门ID
     */
    private Long deptId;

    /**
     * 状态
     */
    private String status;

    /**
     * 仓库ID（用于查询库存数量）
     */
    private Long warehouseId;

    /**
     * 创建人ID（用于权限控制）
     */
    private Long createBy;
}

