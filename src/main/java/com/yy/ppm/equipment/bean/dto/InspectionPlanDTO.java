package com.yy.ppm.equipment.bean.dto;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

@Data
public class InspectionPlanDTO {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
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
    private String EQUIP_UNIT_NAME;
    /**
     * 设备ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long equipId;
    /**
     * 设备NAME
     */
    private String equipName;
    /**
     * 类型 1：天，2：周，3：月，4：年
     */
    private String equipType;
    /**
     * 初始日期
     */
    private String initialDate;
    /**
     * 设置日期
     */
    private String setDate;
    /**
     * 是否单次 1：是，2：否
     */
    private String isSingle;
    /**
     * 周期（天数）
     */
    private String cycle;
    /**
     * 点检员ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long inspectorId;
    /**
     * 点检员NAME
     */
    private String inspectorName;
    /**
     * 点检标准ID
     */
    private String standardId;
    /**
     * 点检内容
     */
    private String content;
    /**
     * 点检标准
     */
    private String standard;
}
