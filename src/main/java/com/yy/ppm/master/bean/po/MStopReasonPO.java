package com.yy.ppm.master.bean.po;


import lombok.Data;
import com.yy.ppm.common.bean.po.BasePO;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName 船舶停时原因维护(MStopReason)PO
 * @author yy
 * @version 1.0.0
 * @Description
 * @createTime 2023年06月05日 17:21:00
 */
@Data
public class MStopReasonPO extends BasePO implements Serializable {

    private static final long serialVersionUID = -30759460270269370L;

    /**主键*/
    private Long id;
    /**停工原因分类（STOP_REASON_CLASS）*/
    private String stopReasonClassCode;
    /**名称*/
    private String stopReasonName;
    /**停工类型(字典 STOP_REASON_TYPE）*/
    private String stopReasonTypeCode;
    /**排序号*/
    private Integer sortNum;
    /** 朱家吗 */
    private String shorthandCode;
}

