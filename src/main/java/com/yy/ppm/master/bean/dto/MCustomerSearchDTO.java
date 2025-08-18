package com.yy.ppm.master.bean.dto;


import com.yy.common.page.PageParameter;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.math.BigDecimal;

/**
 * @ClassName 客户资料(MCustomer)SearchDTO
 * @author yy
 * @version 1.0.0
 * @Description TODO
 * @createTime 2023年06月05日 16:27:00
 */
@Data
public class MCustomerSearchDTO extends PageParameter implements Serializable {

    private static final long serialVersionUID = 148733388510489876L;

            /**主键id*/
    private Long id;
            /**客户code*/
    private String customerCode;
            /**垛位code*/
    private String customerName;
            /**英文名称*/
    private String enCustomerName;
            /**简称*/
    private String customerShort;
            /**备注*/
    private String remark;
            /**状态 1在用 0停用*/
    private Long status;
            /**创建人*/
    private Long createBy;
                            }

