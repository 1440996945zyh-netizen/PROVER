package com.yy.ppm.equipment.bean.po;

import com.yy.ppm.common.bean.po.BasePO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 设备调拨PO
 * @author system
 */
@Getter
@Setter
@ToString
public class EMaterialWasteDisposalPO extends BasePO implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 主键ID
     */
    private Long id;

    private String disposalNum;

    private String disposalTitle;

    private String disposalType;

    private BigDecimal disposalAmount;

    /**
     * 创建人
     */
    private Long createdUser;

    /**
     * 创建时间
     */
    private Date createdTime;

    /**
     * 修改人
     */
    private Long updateUser;

    /**
     * 修改时间
     */
    private Date updateTime;
    private String isSettlement;

}
