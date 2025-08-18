package com.yy.ppm.dispatch.bean.po;

import com.yy.common.validate.AddGroup;
import com.yy.common.validate.EditGroup;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 昼夜计划工人业务对象 t_daynight_plan_worker
 *
 */

@Data
public class TDaynightPlanWorkerPO implements Serializable {

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
     * 岗位CODE 字典WORKER_POST
     */
    @NotBlank(message = "岗位CODE 字典WORKER_POST不能为空", groups = { AddGroup.class, EditGroup.class })
    private String workerPostCode;

    /**
     * 数量
     */
    @NotBlank(message = "数量不能为空", groups = { AddGroup.class, EditGroup.class })
    private String NUM;
}
