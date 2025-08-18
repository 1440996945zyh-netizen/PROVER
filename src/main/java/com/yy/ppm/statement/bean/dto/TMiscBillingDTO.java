package com.yy.ppm.statement.bean.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yy.ppm.statement.bean.po.TMiscBillingPO;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
public class TMiscBillingDTO extends TMiscBillingPO implements Serializable {

    private String customerName;

    private String statementNo;

   private String statementStatus;

   private String feeType;
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

    private String cargoName;

    private String isMainIncome;

    private String berthName;

    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd hh:mm")
    @DateTimeFormat(pattern = "yyyy-MM-dd hh:mm")
    private Date rejectTime;

    private String rejectByName;

    private String rejectReason;


   /**
    * 审核人信息
    */
    private String reviewByName;
    private String reviewTime;

   private String confirmByName;

}
