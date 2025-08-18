package com.yy.ppm.business.bean.dto;


import com.yy.common.page.PageParameter;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.math.BigDecimal;

/**
 * @author makejava
 * @version 1.0.0
 * @ClassName 委托单主表(TBusOrder)SearchDTO
 * @Description TODO
 * @createTime 2024年10月23日 09:01:00
 */
@Data
public class TBusOrderSearchDTO extends PageParameter implements Serializable {

    private static final long serialVersionUID = 197464515879994303L;

    /**
     * 主键
     */
    private BigDecimal id;
    /**
     * 委托单类型,1疏港3装船
     */
    private BigDecimal orderType;
    /**
     * 客户ID
     */
    private BigDecimal customerId;
    /**
     * 客户名称
     */
    private String customerName;
    /**
     * 状态,0待审1已审
     */
    private BigDecimal status;
    /**
     * 创建人姓名
     */
    private String createByName;
    /**
     * 修改人姓名
     */
    private String updateByName;
    /**
     * 审核人ID
     */
    private BigDecimal approvedBy;
    /**
     * 审核人姓名
     */
    private String approvedByName;
    /**
     * 审核时间
     */
    private Date approvedTime;
    /**
     * 航次ID
     */
    private BigDecimal shipvoyageId;
    /**
     * 航次子表ID
     */
    private BigDecimal shipvoyageItemId;
    /**
     * 船名
     */
    private String shipName;
    /**
     * 航次
     */
    private String voyage;
}

