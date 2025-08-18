package com.yy.ppm.master.bean.po;


import lombok.Data;
import com.yy.ppm.common.bean.po.BasePO;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName 客户资料(MCustomer)PO
 * @author yy
 * @version 1.0.0
 * @Description
 * @createTime 2023年06月05日 16:27:00
 */
@Data
public class MCustomerPO extends BasePO implements Serializable {

    private static final long serialVersionUID = 351732391577023137L;

    /** 主键id */
    private Long id;
    /** 客户code */
    private String customerCode;
    /** 垛位code */
    private String customerName;
    /** 英文名称 */
    private String enCustomerName;
    /** 简称 */
    private String customerShort;
    /** 备注 */
    private String remark;
    /** 状态 1在用 0停用 */
    private Long stauts;

}

