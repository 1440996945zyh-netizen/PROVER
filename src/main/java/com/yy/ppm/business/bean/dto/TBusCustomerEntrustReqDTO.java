package com.yy.ppm.business.bean.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yy.common.page.PageParameter;
import com.yy.ppm.common.bean.po.BasePO;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 客户委托
 */
@Data
public class TBusCustomerEntrustReqDTO extends PageParameter implements Serializable  {
   private Long id;
   private String entrustNo;
   private Long trustId;

   private String trustNo;
   private Long companyId;
   private String companyName;
   private Long customerId;
   private String customerName;
   private String tradeType;
   private String impExp;
   private String settlementBasisCode;
   private String settlementBasisName;
   @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd HH:mm")
   @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
   private Date startTime;
   @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd HH:mm")
   @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
   private Date endTime;
   private Integer planQuantity;
   private BigDecimal planTon;
   private String processCode;
   private String processName;
   private String cargoCategoryCode;
   private String cargoCategoryName;
   private String remark;
   private Long shipvoyageId;
   private String shipName;
   private BigDecimal checkTon;
   private BigDecimal checkNumber;
   private String status;
   private Long releaseBy;
   private String releaseByName;
   private Date releaseTime;
   private Long checkBy;
   private String checkByName;
   private Date checkTime;
   private Long examineBy;
   private String examineByName;
   private Date examineTime;
   private String cargoOwnerName;
   private String cargoAgentName;
   private String cargoName;
   private Long shipvoyageItemId;
   private Long workAccompanyingId;
   private String isBill;
   private String estAmount;
   private String type;
   private String poundRemark;
   private String isWeiqiaoPoundRemark;
}
