package com.yy.ppm.master.bean.dto;


import com.yy.common.page.PageParameter;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.math.BigDecimal;

/**
 * @ClassName 船舶停时原因维护(MStopReason)SearchDTO
 * @author yy
 * @version 1.0.0
 * @Description TODO
 * @createTime 2023年06月05日 17:21:00
 */
@Data
public class MStopReasonSearchDTO extends PageParameter implements Serializable {

    private static final long serialVersionUID = -17616333640785404L;

    /** 停工原因分类 */
    private String stopReasonClassCode;
    /**停时name*/
    private String stopReasonName;
    /**停时类型字典 STOP_REASON_TYPE*/
    private String stopReasonTypeCode;
}

