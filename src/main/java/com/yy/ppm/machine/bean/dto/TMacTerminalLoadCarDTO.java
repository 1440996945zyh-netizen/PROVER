package com.yy.ppm.machine.bean.dto;

import com.yy.ppm.common.bean.po.BasePO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TMacTerminalLoadCarDTO extends BasePO implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = -4292983992705760507L;

    /**
     * 船名
     */
    private String shipName;

    /**
     * scn
     */
    private String scn;

    /**
     * 合同号
     */
    private String planNo;

    /**
     * 磅单备注
     */
    private String invNem;

    /**
     * 净装吨位
     */
    private BigDecimal NetPackTon;

    /**
     * 皮重
     */
    private BigDecimal weightSelf;

    /**
     * 货名
     */
    private String goodsName;
}
