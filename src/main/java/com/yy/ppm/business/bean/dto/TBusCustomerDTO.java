package com.yy.ppm.business.bean.dto;


import com.yy.ppm.business.bean.po.TBusCustomerPO;
import lombok.Data;

import java.util.List;

/**
 * @ClassName 合同表(TBusCustomer)DTO
 * @author yy
 * @version 1.0.0
 * @Description
 * @createTime 2023年06月29日 13:09:00
 */
@Data
public class TBusCustomerDTO extends TBusCustomerPO {

    private static final long serialVersionUID = 513568687940341075L;

    /** 客户属性 */
    List<TBusCustomerPropertyDTO> propertyList;
    /** 客户属性 */
    List<TBusCustomerContactDTO> contactList;

    private String statusLabel;

    private String customerTypeLabel;

    /** 营业执照 */
    List<Long> licenseFileIds;
    /** 授权书 */
    List<Long> authorizationFileIds;
    /** 企业开票 */
    List<Long> billingFileIds;


}
