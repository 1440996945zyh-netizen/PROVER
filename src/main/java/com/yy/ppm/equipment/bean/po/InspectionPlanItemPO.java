package com.yy.ppm.equipment.bean.po;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.yy.ppm.common.bean.po.BasePO;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
public class InspectionPlanItemPO extends BasePO {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    /**
     * 点检标准ID
     */
    private Long standardId;
    /**
     * 点检计划ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long equipPlanId;
    /**
     * 设备小类
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long equipSmallCategoryId;
    /**
     * 设备小类
     */
    private String equipSmallCategoryName;
    /**
     * 设备机构ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long equipInstitutionId;
    /**
     * 设备机构
     */
    private String equipInstitutionName;
    /**
     * 设备部件
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long equipUnitId;
    /**
     * 设备部件
     */
    private String equipUnitName;
    /**
     * 点检内容
     */
    private String content;
    /**
     * 点检标准
     */
    private String standard;
    private String equipType;

}
