package com.yy.ppm.equipment.bean.po;

import com.yy.ppm.common.bean.po.BasePO;
import lombok.Data;

import java.io.Serializable;

/**
 * 供应商PO
 */
@Data
public class EMaterialSupplierPO extends BasePO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String supplierCode;
    private String companyId;
    private String customerType;
    private String supplierName;
    private String companyName;
    private String companyShort;
    private String formerName;
    private String eCompanyName;
    private String industryType;
    private String country;
    private String province;
    private String city;
    private String registeredCurrency;
    private String address;
    private String uniformSocialCreditCode;
    private String organizationCode;
    private String businessType;
    private String duns;
    private String mnemonicCode;
    private String estiblishTime;
    private String registeredCapital;
    private String legalPerson;
    private String legalPersonCardNo;
    private String businessState;
    private String businessScope;
    private String companyTel;
    private String businessAddress;
    private String licenseImgUrl;
    private String legalPersonCardImg1;
    private String legalPersonCardImg2;
    private String isAuth;
    private String status;
    private String remark;
    private String companyInfoRemark;
    private String supplierPerson;
    private String supplierPhone;
    private String relationVoidPerson;
    private String relationVoidReason;
}
