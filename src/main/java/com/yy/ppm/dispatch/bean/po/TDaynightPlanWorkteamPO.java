package com.yy.ppm.dispatch.bean.po;

import com.yy.common.validate.AddGroup;
import com.yy.common.validate.EditGroup;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 昼夜计划工作组业务对象 t_daynight_plan_workteam
 *
 */

@Data
public class TDaynightPlanWorkteamPO implements Serializable {

    /**
     * 主键ID
     */
    @NotNull(message = "主键ID不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long ID;

    /**
     * 计划ID
     */
    @NotNull(message = "计划ID不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long planId;

    /**
     * 作业队code
     */
    @NotBlank(message = "作业队code不能为空", groups = { AddGroup.class, EditGroup.class })
    private String workTeamCode;

    /**
     * 作业队名称
     */
    @NotBlank(message = "作业队名称不能为空", groups = { AddGroup.class, EditGroup.class })
    private String workTeamName;

    /**
     * 人数
     */
    @NotNull(message = "人数不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long NUM;


}
