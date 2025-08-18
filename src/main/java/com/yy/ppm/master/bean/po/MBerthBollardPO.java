package com.yy.ppm.master.bean.po;


import lombok.Data;
import com.yy.ppm.common.bean.po.BasePO;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author yy
 * @version 1.0.0
 * @ClassName 缆桩信息(MBerthBollard)PO
 * @Description
 * @createTime 2023年06月05日 16:06:00
 */
@Data
public class MBerthBollardPO extends BasePO implements Serializable {

    private static final long serialVersionUID = 244221839923951222L;

    /**
     * 主键id
     */
    private Long id;
    /**
     * 缆桩代码
     */
    private String bollardCode;
    /**
     * 缆桩名称
     */
    private String bollardName;
    /**
     * 经度
     */
    private BigDecimal lon;
    /**
     * 维度
     */
    private BigDecimal lat;
    /**
     * 所属泊位id
     */
    private Long berthId;

}

