package com.yy.ppm.business.bean.dto;


import com.yy.ppm.business.bean.po.TBusCargoInfoPO;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @ClassName 票货免堆存期
 * @author yy
 * @version 1.0.0
 * @Description
 */
@Data
public class TBusCargoFreeStorageDTO implements Serializable {

    private static final long serialVersionUID = 216082502981674447L;

    private Long companyId;
    private Long customerId;
    private String cargoCode;
    private Integer freeStorageDays;

    /**
     * 有效期-始
     */
    private Date startTime;
    /**
     * 有效期-止
     */
    private Date endTime;

    /**
     * 创建日期
     */
    private Date createTime;

}
