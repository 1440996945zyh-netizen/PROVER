package com.yy.ppm.statement.bean.dto.storageSettle;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Auther linqi
 * @Description
 * @java.util.Date 2023-11-24 14:05
 */
@Setter
@Getter
public class TBusHandoverlistDTO {

    private Long id;

    private Long companyId;

    private String companyName;

    private String scn;

    private Long shipvoyageId;

    private Long shipvoyageItemId;

    private String shipNameVoyage;

    private Long trustId;

    private String trustNo;

    private Long cargoOwnerId;

    private String cargoOwnerName;

    private Long cargoInfoId;

    private String workType;

    private String workTypeLabel;

    private String cargoCode;

    private String cargoName;

    private String tradeType;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date berthTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date leaveBerthTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date leavePortTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date releaseTime;

    private String loadUnload;

    private String statementStatus;

    private String statementStatusLabel;

    private String impExp;

    private String impExpLabel;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date workEndTime;

    /**
     * 是否超期 0否/1是
     */
    private String isOverdue;

    private String isFinal;

    private String isFinalLabel;

    private String clearByName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date clearDate;

//导出账单用
    /** 纳税人识别号 */
    private String tin;
    /** 开户行 */
    private String bank;
    /** 银行账号 */
    private String bankAccount;
    /** 企业电话 */
    private String telephoneNumber;
    /**
     * 客户的联系电话
     */
    private String contactNumber;
    /** 联系人姓名 */
    private String contact;
    /** 企业地址 */
    private String address;

    private BigDecimal amount;
    private String isClear;
}
