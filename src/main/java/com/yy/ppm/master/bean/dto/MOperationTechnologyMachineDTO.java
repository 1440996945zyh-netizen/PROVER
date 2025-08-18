package com.yy.ppm.master.bean.dto;

import com.yy.ppm.master.bean.po.MOperationTechnologyMachinePO;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 作业工艺机械配置
 * @author  yangcl
 * */
@Data
public class MOperationTechnologyMachineDTO extends MOperationTechnologyMachinePO implements Serializable {

    /**
     * 机械类型code
     */
    private String machineTypeName;

    /**
     * 机械型号code
     */
    private String machineModelName;

    private static final long serialVersionUID = 1L;
}

