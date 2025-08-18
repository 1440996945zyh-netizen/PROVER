package com.yy.ppm.business.bean.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.yy.common.page.PageParameter;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author makejava
 * @version 1.0.0
 * @ClassName 单船测试记录(TStdShipRecord)SearchDTO
 * @Description
 * @createTime 2023年12月31日 10:35:00
 */
@Data
public class PoundbillSearchDTO extends PageParameter implements Serializable {

    private static final long serialVersionUID = -93835603383956075L;

    /**
     * 票货
     */
    private Long cargoInfoId;
    private List<Long> cargoInfoIds;
    /**
     * 船名
     */
    private String shipName;
    /**
     * 航次
     */
    private String voyage;
    /**
     * 船名航次
     */
    private Long shipvoyageItemId;
    /**
     * 船名航次
     */
    private String unionNo;
    /**
     * 船名航次
     */
    private String planNo;
    /**
     * 车牌号
     */
    private String truckPlate;
    /**
     * 状态
     */
    private String status;
    /**
     * 货名
     */
    private String cargoName;
    /**
     * 客户id
     */
    private Long customerId;
    /**
     * 出港开始时间
     */
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String startTime;
    /**
     * 出港结束时间
     */
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String endTime;

    private List<Date> dateTime;

}

