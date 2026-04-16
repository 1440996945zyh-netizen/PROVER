package com.yy.ppm.equipment.bean.dto;

import com.yy.ppm.common.bean.po.BasePO;
import lombok.Data;

import java.io.Serializable;

/**
 * 供应商DTO
 */
@Data
public class EMaterialSupplierDTO extends BasePO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private Long id;

    /**
     * 供应商编码
     */
    private String supplierCode;

    /**
     * 企业主数据ID
     */
    private String companyId;

    /**
     * 客户分类 0-个人 1-企业
     */
    private String customerType;

    /**
     * 供应商名称
     */
    private String supplierName;

    /**
     * 企业名称
     */
    private String companyName;

    /**
     * 企业简称
     */
    private String companyShort;

    /**
     * 企业曾用名
     */
    private String formerName;

    /**
     * 企业英文名称
     */
    private String eCompanyName;

    /**
     * 行业类型
     */
    private String industryType;

    /**
     * 注册地所属国家
     */
    private String country;

    /**
     * 注册地所属省
     */
    private String province;

    /**
     * 注册地所属市
     */
    private String city;

    /**
     * 注册币种
     */
    private String registeredCurrency;

    /**
     * 注册地详细地址
     */
    private String address;

    /**
     * 统一社会信用代码
     */
    private String uniformSocialCreditCode;

    /**
     * 组织机构代码
     */
    private String organizationCode;

    /**
     * 企业类型
     */
    private String businessType;

    /**
     * 邓氏编号
     */
    private String duns;

    /**
     * 助记码
     */
    private String mnemonicCode;

    /**
     * 企业成立日期
     */
    private String estiblishTime;

    /**
     * 注册资本
     */
    private String registeredCapital;

    /**
     * 法定代表人
     */
    private String legalPerson;

    /**
     * 法定代表人身份证号
     */
    private String legalPersonCardNo;

    /**
     * 经营状态
     */
    private String businessState;

    /**
     * 经营范围
     */
    private String businessScope;

    /**
     * 公司电话
     */
    private String companyTel;

    /**
     * 公司经营地址
     */
    private String businessAddress;

    /**
     * 营业执照图片
     */
    private String licenseImgUrl;

    /**
     * 法定代表人身份证正面
     */
    private String legalPersonCardImg1;

    /**
     * 法定代表人身份证反面
     */
    private String legalPersonCardImg2;

    /**
     * 实名认证状态
     */
    private String isAuth;

    /**
     * 状态 2-正常 1-停用
     */
    private String status;

    /**
     * 拒绝原因
     */
    private String remark;

    /**
     * 备注信息
     */
    private String companyInfoRemark;

    /**
     * 联系人
     */
    private String supplierPerson;

    /**
     * 联系电话
     */
    private String supplierPhone;

    /**
     * 作废人
     */
    private String relationVoidPerson;

    /**
     * 作废原因
     */
    private String relationVoidReason;
}
