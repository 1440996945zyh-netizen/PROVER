package com.yy.ppm.master.bean.dto;


import com.yy.common.page.PageParameter;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.math.BigDecimal;

/**
 * @author lizx
 * @version 1.0.0
 * @ClassName 船舶预缴标准, (MShipPrepayStd)SearchDTO
 * @Description TODO
 * @createTime 2023年10月23日 15:50:00
 */
@Data
public class MShipPrepayStdSearchDTO extends PageParameter implements Serializable {

    private static final long serialVersionUID = -66890363123958244L;

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
    /**
     * 创建者-姓名
     */
    private String createByName;
    /**
     * 更新者-姓名
     */
    private String updateByName;
}

