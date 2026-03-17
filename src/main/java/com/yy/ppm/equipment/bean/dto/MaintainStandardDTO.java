package com.yy.ppm.equipment.bean.dto;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.yy.ppm.common.bean.po.BasePO;
import com.yy.ppm.equipment.bean.po.InspectionStandardPO;
import com.yy.ppm.equipment.bean.po.MaintainStandardPO;
import lombok.Data;

import java.util.List;

@Data
public class MaintainStandardDTO extends BasePO {
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
    private String standard;
    private String content;
    private String equipType;
    private List<MaintainStandardPO> list;
}
