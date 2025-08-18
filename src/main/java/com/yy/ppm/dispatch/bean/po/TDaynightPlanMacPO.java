package com.yy.ppm.dispatch.bean.po;

import com.yy.common.validate.AddGroup;
import com.yy.common.validate.EditGroup;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 昼夜计划机械业务对象 t_daynight_plan_mac
 *
 */

@Data
public class TDaynightPlanMacPO implements Serializable {

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
     * 机械类型
     */
    @NotBlank(message = "机械类型不能为空", groups = { AddGroup.class, EditGroup.class })
    private String macTypeCode;

    /**
     * 机械型号
     */
    @NotBlank(message = "机械型号不能为空", groups = { AddGroup.class, EditGroup.class })
    private String macModelCode;

    /**
     * 数量
     */
    @NotBlank(message = "数量不能为空", groups = { AddGroup.class, EditGroup.class })
    private String num;

    /**
     * 机械编号 只有门机编号 多个,分割
     */
    @NotBlank(message = "机械编号 只有门机编号 多个,分割不能为空", groups = { AddGroup.class, EditGroup.class })
    private String macCodes;

    /**
     * 机械名称 只有门机编号 多个,分割
     */
    @NotBlank(message = "机械名称 只有门机编号 多个,分割不能为空", groups = { AddGroup.class, EditGroup.class })
    private String macNames;


}
