package com.yy.ppm.equipment.bean.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.yy.ppm.common.bean.po.BasePO;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class EMaintenanceProjectQuotaDTO extends BasePO {

    /** 主键ID */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /** 定额编号：DE-YYYY-MM-DD-0001（系统自动生成） */
    private String quotaNo;

    /** 维修项目名称 */
    private String projectName;

    /** 维修项目内容 */
    private String projectContent;

    /** 计量单位 */
    private String unit;

    /** 不含税金额 */
    private BigDecimal amount;
}

