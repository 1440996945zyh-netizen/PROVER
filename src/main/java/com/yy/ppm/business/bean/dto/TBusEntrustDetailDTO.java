package com.yy.ppm.business.bean.dto;

import com.yy.ppm.common.bean.po.BasePO;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class TBusEntrustDetailDTO extends BasePO {
  private Long  id;
  private Long  entrustId;
  private Long entrustDetailId;
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

  //货物信息
  private String  permitThrough;
  private String businessNo;
  private String contractName;
  private Long contractId;
  private Long contractRateId;
  private String rateLabel;
  private BigDecimal estAmount;
  private String isSecondWeigh;
  private String printPoundId;
  private Integer printPoundNum;
  private String isStopOperationName;
  private Date isStopOperationTime;

}
