package com.yy.ppm.dispatch.bean.po;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import com.yy.ppm.common.bean.po.BasePO;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @ClassName 封航记录表(TDisCloseSail)PO
 * @author yy
 * @version 1.0.0
 * @Description
 * @createTime 2023年07月12日 11:54:00
 */
@Data
public class TDisCloseSailPO extends BasePO implements Serializable {

    private static final long serialVersionUID = 388847922107674316L;

    /** 主键ID */
    private Long id;
    /** 封航开始时间 */
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd HH:mm")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date startTime;
    /** 封航结束时间 */
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd HH:mm")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date endTime;
    /** 封航时长（小时） */
    private BigDecimal timeLength;
    /** 注意事项 */
    private String remark;

    /**
     * 封航原因 { type: 'DICT', dictType: 'DIS_CLOSE_SAIL_REASON' }
     * T_DIS_CLOSE_SAIL CLOSE_REASON_CODE
     */
    private Long closeReasonCode;
    private String closeReasonName;

}

