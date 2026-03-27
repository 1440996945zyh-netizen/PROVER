package com.yy.ppm.equipment.bean.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.yy.ppm.common.bean.po.BasePO;
import com.yy.ppm.equipment.bean.po.InspectionStandardPO;

import java.io.Serializable;
import lombok.Data;

import java.util.List;

@Data
public class InspectionRouteDTO extends BasePO {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long  id;

    /**
     * 路线编号
     */
    private String routeCode;

    /**
     * 路线名称
     */
    private String routeName;
    /**
     * 路线级别
     */
    private String routeLevel;
    /**
     * 子表设备列表
     */
    private List<InspectionRouteSubDTO> subList;
}