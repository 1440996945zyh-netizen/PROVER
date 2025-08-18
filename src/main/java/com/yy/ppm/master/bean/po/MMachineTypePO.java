package com.yy.ppm.master.bean.po;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.yy.ppm.common.bean.po.BasePO;
import lombok.Data;

@Data
public class MMachineTypePO extends BasePO implements Serializable {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 机械类型code
     */
    private String macTypeCode;

    /**
     * 机械类型名称
     */
    private String macTypeName;


    private static final long serialVersionUID = 1L;
}

