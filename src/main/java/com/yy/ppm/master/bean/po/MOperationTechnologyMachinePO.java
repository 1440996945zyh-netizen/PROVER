package com.yy.ppm.master.bean.po;

import java.io.Serializable;
import java.math.BigDecimal;
import lombok.Data;

/**
 * 作业工艺机械配置
 * @author  yangcl
 * */
@Data
public class MOperationTechnologyMachinePO implements Serializable {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 工艺id
     */
    private Long technologyId;

    /**
     * 机械类型code
     */
    private String machineTypeCode;

    /**
     * 机械型号code
     */
    private String machineModelCode;

    /**
     * 数量
     */
    private Integer num;

    private static final long serialVersionUID = 1L;
}

