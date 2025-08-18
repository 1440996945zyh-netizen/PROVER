package com.yy.ppm.business.bean.po;

import com.yy.ppm.common.bean.po.BasePO;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class ComprehensivePO extends BasePO implements Serializable {

    /**
     * 吞吐量当日数量
     */
    private BigDecimal invoiceDayCount;
    /**
     * 吞吐量当月数量
     */
    private BigDecimal invoiceMouthCount;
    /**
     * 吞吐量当年数量
     */
    private BigDecimal invoiceYearCount;

    /**
     * 吞吐量当日外贸
     */
    private BigDecimal tradeDayCount;
    /**
     * 吞吐量当月外贸
     */
    private BigDecimal tradeMouthCount;
    /**
     * 吞吐量当年外贸
     */
    private BigDecimal tradeYearCount;
    /**
     * 车辆详情实时
     */
    private String nowCarCount;
    /**
     * 车辆详情当日
     */
    private String dayCarCount;
    /**
     * 车辆详情当月
     */
    private String mouthCarCount;
    /**
     * 东作业区
     */
    private String eastPort;
    /**
     * 中作业区
     */
    private String midPort;
    /**
     * 西作业区
     */
    private String westPort;
}
