package com.yy.ppm.master.bean.po;

import java.io.Serializable;

import com.yy.ppm.common.bean.po.BasePO;
import lombok.Data;

/**
 * 工属具型号
 * */
@Data
public class MWorkwareTypeModelPO extends BasePO implements Serializable {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 工属具类型code
     */
    private String typeCode;

    /**
     * 型号编号
     */
    private String modelCode;

    /**
     * 型号名称
     */
    private String modelName;

    private static final long serialVersionUID = 1L;
}

