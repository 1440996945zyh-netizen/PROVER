package com.yy.ppm.master.bean.po;

import com.yy.ppm.common.bean.po.BasePO;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class MOperationProcessPO extends BasePO implements Serializable {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 编码
     */
    private String processCode;

    /**
     * 名称
     */
    private String processName;

    /**
     * 过程种类 字典PROCESS_TYPE
     */
    private String processType;

    /**
     * 源 字典WORK_ORIGN
     */
    private String source;

    /**
     * 目标 字典WORK_ORIGN
     */
    private String destination;

    private static final long serialVersionUID = 1L;
}

