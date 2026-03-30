package com.yy.ppm.equipment.bean.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import lombok.Data;

@Data

public class InspectionRouteSubDTO {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long parentId;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long equipId;

    private String sortNum;

    private String equipName;

    private String checkContent;
    private String qualifyCondition;
    private String checkMethod;

}