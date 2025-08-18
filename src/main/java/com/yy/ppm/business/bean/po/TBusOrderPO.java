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
 * @ClassName 委托单主表(TBusOrder)PO
 * @Description
 * @createTime 2024年10月23日 09:01:00
 */
@Data
public class TBusOrderPO extends BasePO implements Serializable {

    private static final long serialVersionUID = 256382713322182145L;

    /**
     * 主键
     */
    private Long id;
    /**
     * 委托单类型,1疏港,3装船
     */
    private Integer orderType;
    /**
     * 委托单类型,1疏港3装船
     */
    private String orderTypeName;
    /**
     * 客户ID
     */
    private Long customerId;
    /**
     * 客户名称
     */
    private String customerName;
    /**
     * 状态,0待审1已审
     */
    private Long status;
    /**
     * 状态,0待审1已审
     */
    private String statusName;
    /**
     * 审核人ID
     */
    private Long approvedBy;
    /**
     * 审核人姓名
     */
    private String approvedByName;
    /**
     * 审核时间
     */
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date approvedTime;
    /**
     * 航次ID
     */
    private Long shipvoyageId;
    /**
     * 航次子表ID
     */
    private Long shipvoyageItemId;
    /**
     * 船名
     */
    private String shipName;
    /**
     * 航次
     */
    private String voyage;

}

