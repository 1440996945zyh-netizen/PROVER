package com.yy.ppm.master.bean.po;


import lombok.Data;
import com.yy.ppm.common.bean.po.BasePO;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName 客户类型(MCustomerType)PO
 * @author yy
 * @version 1.0.0
 * @Description
 * @createTime 2023年06月05日 16:31:00
 */
@Data
public class MCustomerTypePO extends BasePO implements Serializable {

    private static final long serialVersionUID = 553289800118016586L;

        /** 主键id */
    private Long id;
    /** 客户code */
    private String customerCode;
    /** 客户类型code 字典 CUSTOMER_TYPE */
    private String customerTypeCode;
    /** 客户类型名称 */
    private String customerTypeName;

}

