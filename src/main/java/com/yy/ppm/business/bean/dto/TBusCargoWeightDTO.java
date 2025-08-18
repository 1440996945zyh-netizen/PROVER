package com.yy.ppm.business.bean.dto;


import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @ClassName 票货过磅数据
 * @author yy
 * @version 1.0.0
 * @Description
 */
@Data
public class TBusCargoWeightDTO implements Serializable {

    private static final long serialVersionUID = 216082502981674447L;
    /**
     * 二次磅日期
     */
    private Date weighOutDt;
    private BigDecimal weightGoods;


}
