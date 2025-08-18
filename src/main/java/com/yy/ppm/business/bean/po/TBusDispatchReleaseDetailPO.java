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
 * @ClassName 放行单子表(TBusDispatchReleaseDetail)PO
 * @Description
 * @createTime 2024年04月17日 09:27:00
 */
@Data
public class TBusDispatchReleaseDetailPO extends BasePO implements Serializable {

    private static final long serialVersionUID = 873702319204642853L;

    /**
     * 主键ID
     */
    private Long id;
    /**
     * 放行单id
     */
    private Long dispatchReleaseId;

    /**
     * 放行单号
     */
    private String deliveryNumbers;
    /**
     * 票货号
     */
    private String cargoInfoNo;
    /**
     * 货主id
     */
    private String cargoOwnerId;
    /**
     * 货主
     */
    private String cargoOwnerName;
    /**
     * 货名code
     */
    private String cargoCode;
    /**
     * 货名name
     */
    private String cargoName;
    /**
     * 数量
     */
    private Long quantity;
    /**
     * 吨
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
     * 备注
     */
    private String remark;

}

