package com.yy.ppm.equipment.bean.dto;

import com.yy.ppm.common.bean.po.BasePO;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 维修定额项目 DTO
 * 1) 对应表：E_IC_TENANCE_PROJECT_QUOTA
 * 2) 兼容前端字段命名差异：
 *    - 定额编号：quotaCode / quotaNo
 *    - 不含税金额：amountExcludingTax / amount
 *    setter 内做同步，避免出现“入库有值但返回字段为空”的问题
 */
public class EMaintenanceProjectQuotaDTO extends BasePO implements Serializable {
    private static final long serialVersionUID = 1L;
    /** 主键ID */
    private Long id;
    /** 定额编号（推荐字段，对应 QUOTA_CODE） */
    private String quotaCode;
    /** 定额编号（兼容字段：部分前端表格列可能绑定 quotaNo） */
    private String quotaNo;
    /** 维修项目名称 */
    private String projectName;
    /** 维修项目内容 */
    private String projectContent;
    /** 计量单位 */
    private String unit;
    /** 不含税金额（推荐字段，对应 AMOUNT_EXCLUDING_TAX） */
    private BigDecimal amountExcludingTax;
    /** 不含税金额（兼容字段：部分前端表格列可能绑定 amount） */
    private BigDecimal amount;
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getQuotaCode() { return quotaCode; }
    public void setQuotaCode(String quotaCode) {
        this.quotaCode = quotaCode;
        if (this.quotaNo == null || this.quotaNo.isBlank()) {
            this.quotaNo = quotaCode;
        }
    }
    public String getQuotaNo() { return quotaNo; }
    public void setQuotaNo(String quotaNo) {
        this.quotaNo = quotaNo;
        if (this.quotaCode == null || this.quotaCode.isBlank()) {
            this.quotaCode = quotaNo;
        }
    }
    public String getProjectName() { return projectName; }
    public void setProjectName(String projectName) { this.projectName = projectName; }
    public String getProjectContent() { return projectContent; }
    public void setProjectContent(String projectContent) { this.projectContent = projectContent; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    public BigDecimal getAmountExcludingTax() { return amountExcludingTax; }
    public void setAmountExcludingTax(BigDecimal amountExcludingTax) {
        this.amountExcludingTax = amountExcludingTax;
        if (this.amount == null) {
            this.amount = amountExcludingTax;
        }
    }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
        if (this.amountExcludingTax == null) {
            this.amountExcludingTax = amount;
        }
    }
}