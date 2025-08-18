package com.yy.ppm.dispatch.bean.po;

import com.yy.common.validate.AddGroup;
import com.yy.common.validate.EditGroup;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 昼夜计划工具属业务对象 t_daynight_plan_workware
 *
 */

@Data
public class TDaynightPlanWorkwarePO implements Serializable {

    /**
     * 主键ID
     */
    @NotNull(message = "主键ID不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long ID;

    /**
     * 昼夜计划ID
     */
    @NotNull(message = "昼夜计划ID不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long planId;

    /**
     * 工属具code
     */
    @NotBlank(message = "工属具code不能为空", groups = { AddGroup.class, EditGroup.class })
    private String workwareCode;

    /**
     * 规格code
     */
    @NotBlank(message = "规格code不能为空", groups = { AddGroup.class, EditGroup.class })
    private String specCode;

    /**
     * 数量
     */
    @NotNull(message = "数量不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long NUM;

    /**
     * 数量单位 字典UNITS
     */
    @NotBlank(message = "数量单位 字典UNITS不能为空", groups = { AddGroup.class, EditGroup.class })
    private String numUnit;

}
