package com.yy.ppm.statement.bean.po;

import java.io.Serializable;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class TStorageFeeMiddlePO implements Serializable {
    /**
     * 主键id
     */
    private Long id;

    /**
     * 历史结算主表id
     */
    private Long hisGid;

    /**
     * 票货id
     */
    private Long cargoInfoId;

    /**
     * 数量
     */
    private BigDecimal amount;

    /**
     * 类型 1作业指令剩余堆存量  2作业指令已堆存天数  3已过免堆期作业指令
     */
    private Integer type;

    private static final long serialVersionUID = 1L;
}

