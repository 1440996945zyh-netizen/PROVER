package com.yy.ppm.produce.bean.po;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import com.yy.ppm.common.bean.po.BasePO;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author lizx
 * @version 1.0.0
 * @ClassName (MWeightRules)PO
 * @Description
 * @createTime 2023年11月30日 17:20:00
 */
@Data
public class MWeightRulesPO extends BasePO implements Serializable {

    private static final long serialVersionUID = -96809707159085391L;

    /**
     *
     */
    private Long id;
    /**
     * 规则名称
     */
    private String ruleName;
    /**
     * 空车重量不高于
     */
    private BigDecimal emptyWeight;
    /**
     * 重车重量不低于
     */
    private BigDecimal heavyWeight;
    /**
     * 是否默认，0否1是
     */
    private Long isDefault;
    /**
     * 倒运类型，1先空后重2先重后空
     */
    private Long transportType;
    /**
     * 货物代码
     */
    private String cargoCode;
    /**
     * 货物名称
     */
    private String cargoName;

}

