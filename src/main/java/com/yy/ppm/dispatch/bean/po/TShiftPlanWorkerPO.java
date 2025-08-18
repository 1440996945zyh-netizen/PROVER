package com.yy.ppm.dispatch.bean.po;

import lombok.Data;

import java.io.Serializable;

/**
 * 工班计划-作业工艺-工人配置
 * @author yangcl
 * */
@Data
public class TShiftPlanWorkerPO implements Serializable {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 计划ID
     */
    private Long shiftPlanId;

    /**
     * 岗位CODE 字典WORKER_POST
     */
    private String workerPostCode;

    /**
     * 数量
     */
    private String num;

    private static final long serialVersionUID = 1L;
}

