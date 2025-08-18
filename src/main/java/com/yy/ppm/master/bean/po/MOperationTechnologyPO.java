package com.yy.ppm.master.bean.po;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.yy.ppm.common.bean.po.BasePO;
import lombok.Data;

/**
 * 作业工艺po
 * @author yangcl*/
@Data
public class MOperationTechnologyPO extends BasePO implements Serializable {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 作业过程ID
     */
    private String processCodes;

    /**
     * 作业过程名称
     */
    private String processName;

    /**
     * 工艺名称
     */
    private String techniqueName;

    /**
     * 工艺code
     */
    private String techniqueCode;

    /**
     * 货名代码集合多个,分割
     */
    private String cargoCodes;

    /**
     * 货名集合多个,分割
     */
    private String cargoNames;

    /**
     * 状态 1在用 0停用
     */
    private Integer status;

    private static final long serialVersionUID = 1L;
}

