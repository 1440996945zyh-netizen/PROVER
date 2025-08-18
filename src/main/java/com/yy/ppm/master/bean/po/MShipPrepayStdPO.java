package com.yy.ppm.master.bean.po;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import com.yy.ppm.common.bean.po.BasePO;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author lizx
 * @version 1.0.0
 * @ClassName 船舶预缴标准, (MShipPrepayStd)PO
 * @Description
 * @createTime 2023年10月23日 15:50:00
 */
@Data
public class MShipPrepayStdPO extends BasePO implements Serializable {

    private static final long serialVersionUID = -32735359917448331L;

    /**
     * 主键ID
     */
    private Long id;
    /**
     * 内贸、外贸
     */
    private String tradeType;
    /**
     * 载重吨开始区间
     */
    private Long dwtStart;
    /**
     * 载重吨结束区间
     */
    private Long dwtEnd;
    /**
     * 预缴费用
     */
    private Long advancePayment;

}

