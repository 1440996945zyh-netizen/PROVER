package com.yy.ppm.master.bean.po;

import java.io.Serializable;

import com.yy.ppm.common.bean.po.BasePO;
import lombok.Data;

@Data
public class MMachineTypeModelPO extends BasePO implements Serializable {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 机械类型主键
     */
    private String macTypeCode;

    /**
     * 型号code
     */
    private String modelCode;

    /**
     * 型号名称
     */
    private String modelName;

    private static final long serialVersionUID = 1L;
}

