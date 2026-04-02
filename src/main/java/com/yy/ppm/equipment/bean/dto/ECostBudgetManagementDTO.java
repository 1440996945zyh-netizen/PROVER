package com.yy.ppm.equipment.bean.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.yy.ppm.common.bean.po.BasePO;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 预算管理 DTO
 *
 * 对应数据表：E_COST_BUDGET_MANAGEMENT
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ECostBudgetManagementDTO extends BasePO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 年份
     * 例如：2026
     */
    private String year;

    /**
     * 维修单位ID
     * 这里存维修单位表的 externalCompanyId
     */
    private Long maintenanceUnitId;

    /**
     * 维修单位名称
     */
    private String maintenanceUnitName;

    /**
     * 费用类型
     * 1：维修费
     * 2：材料费
     * 3：其他
     */
    private String costType;

    /**
     * 费用类型名称
     * 用于列表展示，通常由字典表关联查询得到
     */
    private String costTypeName;

    /**
     * 预算金额
     */
    private BigDecimal amount;

    /**
     * 创建人
     */
    private Long createBy;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改人
     */
    private Long updateBy;

    /**
     * 修改时间
     */
    private Date updateTime;
}
