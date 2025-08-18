package com.yy.ppm.business.bean.dto.contractOld;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 老数据同步合同信息
 */
@Setter
@Getter
public class TBusOldContractDTO {

    /**
     * 合同ID
     */
    private String contractId;

    /**
     * 子合同ID
     */
    private String contractItemId;

    /**
     * 合同编号
     */
    private String contractCode;

    private Long companyId;

    private String companyName;
    /**
     * 进口作业公司ID
     */
    private Long companyIdImp;

    private String companyNameImp;
    /**
     * 出口作业公司ID
     */
    private Long companyIdExp;

    private String companyNameExp;

    private String scn;

    private String inteFore;

    private String impExp;

    private String settlementStatus;

    private BigDecimal quantity;

    private BigDecimal weight;

    private String shipCode;

    private String shipName;

    private String impExpScn;

    private String tradeType;

    private String loadUnload;

    private String isWater;

    private String isShorePower;

    /**
     * 预计加水量
     */
    private BigDecimal reqWater;

    /**
     * 预计加油量
     */
    private BigDecimal reqElectricity;

    private BigDecimal declareDraft;

    private BigDecimal expDeclareDraft;

    private String isPayment;

    private BigDecimal paymentAmount;

    private String paymentSettleImp;

    private String paymentSettleExp;

    private String isDangerous;

    /**
     * 预抵日期
     */
    @NotNull(message = "预抵日期不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private Date arrivalTimePlan;

    /**
     * 预计离港时间
     */
    @NotNull(message = "预计离港时间不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private Date leavePortTimePlan;

    /**
     * 抵锚时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date arrivalAnchorageTime;

    /**
     * 起锚时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date leaveAnchorageTime;

    /**
     * 靠泊时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date berthTime;

    private String berthName;

    private String berthType;

    private String bollardNoStart;

    private String bollardNoEnd;

    /**
     * 离泊时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date leaveBerthTime;

    /**
     * 接收时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date receiveTime;

    private String remark;

    private String lastPort;

    private String nextPort;

    private String shipStatusCode;

    private String shipStatusName;

    private String impVoyage;

    private String expVoyage;

    private String portCodeImp;

    private String portCodeExp;

    private String impCargoName;

    private String expCargoName;

    private String impCargoTon;

    private String expCargoTon;

    private String customerCode1;

    private String customerCode2;

    private String customerName1;

    private String customerName2;

    private Long customerIdImp;

    private Long customerIdExp;

    private String contacts;

    private String contacts2;

    private String contactsTel;

    private String contactsTel2;

    private String listId;

    private String loadUnloadList;

    private String customerCode;

    private String customerName;

    private String cargoCode;

    private String cargoTypeCode;

    private String cargoName;

    private BigDecimal quantityList;

    private BigDecimal weightList;

    private Integer num;

    private String remarkList;

    private BigDecimal preWeight;

}
