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
 * @ClassName 放行单表(TBusDispatchRelease)PO
 * @Description
 * @createTime 2024年04月16日 16:03:00
 */
@Data
public class TBusDispatchReleasePO extends BasePO implements Serializable {

    private static final long serialVersionUID = 254641635660526169L;

    /**
     * 主键ID
     */
    private Long id;
    /**
     * 航次id
     */
    private Long shipvoyageId;
    /**
     * 航次子表id
     */
    private Long shipvoyageItemId;
    /**
     * 放行单号
     */
    private String deliveryNumbers;
    /**
     * 数量
     */
    private Long quantity;
    /**
     * 重量
     */
    private BigDecimal ton;
    /**
     * 包装code
     */
    private String packingCode;
    /**
     * 包装name
     */
    private String packingName;
    /**
     * 规格
     */
    private String specs;
    /**
     * 是否放行
     */
    private String permitThrough;
    /**
     * 备注
     */
    private String remark;

}

