package com.yy.ppm.produce.bean.dto;


import com.yy.ppm.common.bean.po.BasePO;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @ClassName (TBusPlanEntrust)DTO
 * @author makejava
 * @version 1.0.0
 * @Description
 * @createTime 2024年01月05日 14:47:00
 */
@Data
public class TPrdPlanEntrustCargoDTO extends BasePO {

    private static final long serialVersionUID = -57972243886403254L;

    /**
     * 件数
     */
    private Integer quantity;
    /**
     * 重量
     */
    private BigDecimal ton;

}
