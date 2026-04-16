package com.yy.ppm.equipment.bean.dto;

import com.yy.common.page.PageParameter;
import lombok.Data;

import java.io.Serializable;

/**
 * 供应商查询DTO
 */
@Data
public class EMaterialSupplierSearchDTO extends PageParameter implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 供应商编码
     */
    private String supplierCode;

    /**
     * 供应商名称
     */
    private String supplierName;

    /**
     * 统一社会信用代码
     */
    private String uniformSocialCreditCode;

    /**
     * 企业类型
     */
    private String businessType;

    /**
     * 状态
     */
    private String status;
}
