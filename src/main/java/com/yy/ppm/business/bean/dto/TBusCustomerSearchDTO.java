package com.yy.ppm.business.bean.dto;


import com.yy.common.page.PageParameter;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.math.BigDecimal;

/**
 * @ClassName 合同表(TBusCustomer)SearchDTO
 * @author yy
 * @version 1.0.0
 * @Description TODO
 * @createTime 2023年06月29日 13:09:00
 */
@Data
public class TBusCustomerSearchDTO extends PageParameter implements Serializable {

    private static final long serialVersionUID = 372316808311696065L;

    /**客户代码*/
    private String customerCode;
    /**客户名称（判重）*/
    private String customerName;
    /**客户简称（判重）*/
    private String customerShortName;
    /**客户类型，1国内企业2国外企业3个人*/
    private String customerTypeCode;
    /**客户属性代码,逗号隔开（字典CUSTOMER_PROPERTY）*/
    private String customerPropertyCode;
    /**状态，1未审核2已审核*/
    private String status;
    private String bhtId;
    /** 金蝶客户名称 */
    private String customerCodeEas;
    /** 金蝶客户代码 */
    private String customerNameEas;

    private String companyId;

    }

