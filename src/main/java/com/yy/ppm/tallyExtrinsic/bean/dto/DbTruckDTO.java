package com.yy.ppm.tallyExtrinsic.bean.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yy.ppm.common.bean.po.BasePO;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
public class DbTruckDTO extends BasePO {

    /**
     * 主键ID
     */
    private Long id;
    /**
     * 车号
     */
    private String truckNo;

    /**
     * 货名
     */
    private String cargoName;

    /**
     * 司机
     */
    private String driver;

    /**
     * 电话
     */
    private String phone;

    /**
     * 货主
     */
    private String cargoOwnerName;

    /**
     * 票货号
     */
    private String cargoInfoNo;

    /**
     * 件数
     */
    private String quantity;

    /**
     * 进港时间
     */
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date inTime;

}
