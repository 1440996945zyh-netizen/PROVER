package com.yy.ppm.produce.bean.dto;


import com.yy.common.page.PageParameter;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.math.BigDecimal;

/**
 * @author lizx
 * @version 1.0.0
 * @ClassName (MWeightRules)SearchDTO
 * @Description TODO
 * @createTime 2023年11月30日 17:20:00
 */
@Data
public class MWeightRulesSearchDTO extends PageParameter implements Serializable {

    private static final long serialVersionUID = 136882069807825880L;

    /***/
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

