package com.yy.ppm.equipment.bean.po;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.yy.ppm.common.bean.po.BasePO;
import lombok.Data;

@Data
public class MaintainStandardPO extends BasePO {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long equipUnitId;
    private String equipUnitName;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long equipSmallCategoryId;
    private String equipSmallCategoryName;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long equipInstitutionId;
    private String equipInstitutionName;
    private String content;
    private String standard;
    private String equipType;
}
