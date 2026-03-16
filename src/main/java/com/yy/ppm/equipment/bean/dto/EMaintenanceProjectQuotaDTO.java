package com.yy.ppm.equipment.bean.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.yy.ppm.common.bean.po.BasePO;
import lombok.Data;

import java.util.Date;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 维修定额项目 DTO
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class EMaintenanceProjectQuotaDTO extends BasePO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;

    /** 定额编号（前端字段：quotaNo，对应数据库：QUOTA_CODE） */
    private String quotaCode;

    /** 维修项目名称 */
    private String projectName;

    /** 维修项目内容 */
    private String projectContent;

    /** 计量单位 */
    private String unit;

    /** 不含税金额（前端字段：amount，对应数据库：AMOUNT_EXCLUDING_TAX） */
    private BigDecimal amountExcludingTax;

    /** 创建人 */
    private Long createBy;
    /** 创建时间 */
    private Date createTime;
    /** 更新人 */
    private Long updateBy;
    /** 更新时间 */
    private Date updateTime;

    /** 状态：1-生效，0-失效 */
    private String status;

    /** 批量修改状态时使用的主键ID集合 */
    private List<Long> ids;
}