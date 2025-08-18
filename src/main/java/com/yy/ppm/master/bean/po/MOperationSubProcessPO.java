package com.yy.ppm.master.bean.po;

import com.yy.ppm.common.bean.po.BasePO;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class MOperationSubProcessPO extends BasePO implements Serializable {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 作业过程code
     */
    private String processCode;

    /**
     * 子过程code
     */
    private String subprocessCode;

    /**
     * 子过程名称
     */
    private String subprocessName;

    /**
     * 源 字典WORK_ORIGN
     */
    private String source;

    /**
     * 目标 字典WORK_ORIGN
     */
    private String description;

    /**
     * 是否计算堆存量 1是 0否
     */
    private Integer cargoVolume;

    private static final long serialVersionUID = 1L;
}

