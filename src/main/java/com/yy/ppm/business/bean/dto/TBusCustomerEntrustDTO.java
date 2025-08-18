package com.yy.ppm.business.bean.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yy.common.page.PageParameter;
import com.yy.ppm.common.bean.po.BasePO;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 客户委托主表
 */
@Data
public class TBusCustomerEntrustDTO extends BasePO implements Serializable {
   private Long id;
   private Long trustId;
   private String trustStaus;
   private String entrustNo;
   private String trustNo;
   private Long companyId;
   private String companyName;
   private Long customerId;
   private String customerName;
   private String tradeType;
   private String impExp;
   private String settlementBasisCode;
   private String settlementBasisName;
   @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm")
   @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
   private Date startTime;
   @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm")
   @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
   private Date endTime;
   private String planQuantity;
   private String cargoInfoNo;
   private String planTon;
   private String processCode;
   private String processName;
   private String cargoCategoryCode;
   private String cargoCategoryName;
   private String remark;
   private Long shipvoyageId;
   private String shipName;
   private String checkTon;
   private String checkNumber;
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

   private List<TBusEntrustDetailDTO> cargoList;
   private String entrustStatus;
   private String entrustId;
   private String preChangeShipName;
   private String preChangeShipNo;
}
