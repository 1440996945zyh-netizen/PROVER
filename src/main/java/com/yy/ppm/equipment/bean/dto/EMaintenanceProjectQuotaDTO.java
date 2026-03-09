package com.yy.ppm.equipment.bean.dto;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 维修定额项目实体类
 * 对应数据库表 e_maintenance_project_quota
 */
public class EMaintenanceProjectQuotaDTO {

    /** 主键ID */
    private Long id;

    /** 定额编号（系统自动生成） */
    private String quotaCode;

    /** 维修项目名称 */
    private String projectName;

    /** 维修项目内容 */
    private String projectContent;

    /** 计量单位 */
    private String unit;

    /** 不含税金额 */
    private BigDecimal amount;

    /** 创建时间 */
    private Date createTime;

    /** 更新时间 */
    private Date updateTime;

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getQuotaCode() { return quotaCode; }

    public void setQuotaCode(String quotaCode) { this.quotaCode = quotaCode; }

    public String getProjectName() { return projectName; }

    public void setProjectName(String projectName) { this.projectName = projectName; }

    public String getProjectContent() { return projectContent; }

    public void setProjectContent(String projectContent) { this.projectContent = projectContent; }

    public String getUnit() { return unit; }

    public void setUnit(String unit) { this.unit = unit; }

    public BigDecimal getAmount() { return amount; }

    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public Date getCreateTime() { return createTime; }

    public void setCreateTime(Date createTime) { this.createTime = createTime; }

    public Date getUpdateTime() { return updateTime; }

    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }
}