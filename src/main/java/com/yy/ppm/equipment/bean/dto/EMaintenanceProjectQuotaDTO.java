package com.yy.ppm.equipment.bean.dto;

import com.yy.ppm.common.bean.po.BasePO;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 维修定额项目 DTO
 */
public class EMaintenanceProjectQuotaDTO extends BasePO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键ID（对应表字段 ID） */
    private Long id;

    /** 定额编号（系统生成：DE-YYYY-MM-DD-0001，对应表字段 QUOTA_CODE） */
    private String quotaCode;

    /** 维修项目名称（对应表字段 PROJECT_NAME） */
    private String projectName;

    /** 维修项目内容（对应表字段 PROJECT_CONTENT） */
    private String projectContent;

    /** 计量单位（对应表字段 UNIT） */
    private String unit;

    /** 不含税金额（对应表字段 AMOUNT_EXCLUDING_TAX） */
    private BigDecimal amountExcludingTax;

    /** 获取主键ID */
    public Long getId() { return id; }

    /** 设置主键ID */
    public void setId(Long id) { this.id = id; }

    /** 获取定额编号 */
    public String getQuotaCode() { return quotaCode; }

    /** 设置定额编号 */
    public void setQuotaCode(String quotaCode) { this.quotaCode = quotaCode; }

    /** 获取维修项目名称 */
    public String getProjectName() { return projectName; }

    /** 设置维修项目名称 */
    public void setProjectName(String projectName) { this.projectName = projectName; }

    /** 获取维修项目内容 */
    public String getProjectContent() { return projectContent; }

    /** 设置维修项目内容 */
    public void setProjectContent(String projectContent) { this.projectContent = projectContent; }

    /** 获取计量单位 */
    public String getUnit() { return unit; }

    /** 设置计量单位 */
    public void setUnit(String unit) { this.unit = unit; }

    /** 获取不含税金额 */
    public BigDecimal getAmountExcludingTax() { return amountExcludingTax; }

    /** 设置不含税金额 */
    public void setAmountExcludingTax(BigDecimal amountExcludingTax) { this.amountExcludingTax = amountExcludingTax; }
}