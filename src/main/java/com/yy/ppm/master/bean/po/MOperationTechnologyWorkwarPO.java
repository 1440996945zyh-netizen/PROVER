package com.yy.ppm.master.bean.po;

import java.io.Serializable;
import java.math.BigDecimal;
import lombok.Data;

/**
 * 作业工艺工属具配置
 * @author yangcl
 * */
@Data
public class MOperationTechnologyWorkwarPO implements Serializable {
    /**
     * 主键id
     */
    private Long id;

    /**
     * 工属具类型code
     */
    private String typeCode;

    /**
     * 工属具型号code
     */
    private String modelCode;

    /**
     * 数量
     */
    private Long num;

    /**
     * 工艺ID
     */
    private Long technologyId;

    private static final long serialVersionUID = 1L;
}

