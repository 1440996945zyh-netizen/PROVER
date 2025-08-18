package com.yy.ppm.master.bean.po;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.yy.ppm.common.bean.po.BasePO;
import lombok.Data;

/**
 * 工属具类型PO
 * */
@Data
public class MWorkwareTypePO extends BasePO implements Serializable {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 工属具CODE
     */
    private String workwareTypeCode;

    /**
     * 工属具名称
     */
    private String workwareTypeName;

    private static final long serialVersionUID = 1L;
}

