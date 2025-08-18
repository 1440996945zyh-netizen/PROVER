package com.yy.ppm.business.bean.po;


import lombok.Data;
import com.yy.ppm.common.bean.po.BasePO;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName (TBusCustomerProperty)PO
 * @author yy
 * @version 1.0.0
 * @Description
 * @createTime 2023年06月29日 13:10:00
 */
@Data
public class TBusCustomerPropertyPO extends BasePO implements Serializable {

    private static final long serialVersionUID = 577005391550419974L;

        /**  */
    private Long id;
            /** 客户ID */
    private Long customerId;
            /** 客户属性代码,逗号隔开（字典CUSTOMER_PROPERTY） */
    private String customerPropertyCode;
            /** 客户属性名称 */
    private String customerPropertyName;

}

