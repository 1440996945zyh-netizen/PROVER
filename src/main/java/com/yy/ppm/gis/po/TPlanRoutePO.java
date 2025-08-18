package com.yy.ppm.gis.po;

import com.yy.ppm.common.bean.po.BasePO;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * @Author linqi
 * @Description
 * @Date 2023-06-16 11:23
 */
@Setter
@Getter
public class TPlanRoutePO extends BasePO {

    /**
     * 主键id
     */
    private Long id;

    /**
     * 作业计划id
     */
    @NotNull(message = "计划id不能为空")
    private Long planId;

    /**
     * 路线名称集合
     */
    @NotBlank(message = "路线名称集合不能为空")
    private String routeNames;

    /**
     * 路线id集合
     */
    @NotBlank(message = "路线id集合不能为空")
    private String routesIds;
}
