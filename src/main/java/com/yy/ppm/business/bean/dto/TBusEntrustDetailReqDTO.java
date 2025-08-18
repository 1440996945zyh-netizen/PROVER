package com.yy.ppm.business.bean.dto;

import com.yy.common.page.PageParameter;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 客户委托单子表
 */
@Data
public class TBusEntrustDetailReqDTO extends PageParameter {
    private Long  id;
    private Long  entrustId;
    private Long  cargoInfoId;
    private Long  trustId;
    private String  cargoInfoNo;
    private Long  shipvoyageId;
    private Long  shipvoyageItemId;
    private String  scn;
    private String  shipName;
    private Long  cargoOwnerId;
    private String  cargoOwnerName;
    private String  cargoCode;
    private String  cargoName;
    private String  tradeType;
    private String  packingCode;
    private String  packingName;
    private Long  companyId;
    private String  companyName;
    private Integer  quantity;
    private BigDecimal ton;
    private String  voyage;
    private String  deliveryNumbers;
    private String  deliveryId;
}
