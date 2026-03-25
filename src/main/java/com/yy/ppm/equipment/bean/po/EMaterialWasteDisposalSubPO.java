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
public class EMaterialWasteDisposalSubPO extends BasePO implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 主键ID
     */
    private Long id;


    private Long parentId;

    private  Long materialId;
    private String materialName;

    private BigDecimal disposalAmount;

    private  BigDecimal disposalQuantity;

}
