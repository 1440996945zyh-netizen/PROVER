package com.yy.ppm.business.bean.po;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import com.yy.ppm.common.bean.po.BasePO;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author makejava
 * @version 1.0.0
 * @ClassName 委托单子表(TBusOrderItem)PO
 * @Description
 * @createTime 2024年10月23日 15:36:00
 */
@Data
public class TBusOrderItemPO extends BasePO implements Serializable {

    private static final long serialVersionUID = 605280311609265451L;

    /**
     * 主键
     */
    private BigDecimal id;
    /**
     * 票货ID
     */
    private BigDecimal cargoInfoId;
    /**
     * 提单号
     */
    private String billNo;
    /**
     * 件数
     */
    private Long quantity;
    /**
     * 吨数
     */
    private BigDecimal ton;
    /**
     * 主表ID
     */
    private BigDecimal orderId;

}

