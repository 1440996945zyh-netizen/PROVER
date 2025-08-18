package com.yy.ppm.business.bean.dto;


import com.yy.ppm.business.bean.po.TBusCargoTransferPO;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @ClassName 货权转移-港存动态
 * @author yy
 * @version 1.0.0
 * @Description
 * @createTime 2024年01月15日 19:37:00
 */
@Data
public class TBusCargoStorageDTO implements Serializable {

    private static final long serialVersionUID = 575231257950234625L;
    /** 港存动态id */
    private Long id;
    /** 票货号 */
    private Long cargoInfoId;
    /** 票货id */
    private String cargoInfoNo;
    private Long storehouseId;
    /** 库场 */
    private String storehouseName;
    private Long regionId;
    /** 区域 */
    private String regionName;
    private Long massId;
    /** 垛位 */
    private String massName;
    /** 件数 */
    private Integer quantity;
    /** 重量 */
    private BigDecimal ton;
    private Integer detailQuantity;
    private BigDecimal detailTon;
    /** 转移件数 */
    private Integer transferQuantity;
    /** 转移重量 */
    private BigDecimal transferTon;
    /** 是否清场 */
    private String cleanMassSign;

    private Long cargoTransferId;

    private String processDetailCode;
}
