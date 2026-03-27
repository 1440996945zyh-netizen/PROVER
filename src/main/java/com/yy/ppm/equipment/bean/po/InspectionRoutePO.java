package com.yy.ppm.equipment.bean.po;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.yy.ppm.common.bean.po.BasePO;
import lombok.Data;

@Data
public class InspectionRoutePO extends BasePO {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 路线名称
     */
    private String routeName;

    /**
     * 路线描述
     */
    private String description;

    /**
     * 状态（0禁用，1启用）
     */
    private Integer status;
}