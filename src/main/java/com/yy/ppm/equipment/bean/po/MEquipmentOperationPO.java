package com.yy.ppm.equipment.bean.po;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.yy.ppm.common.bean.po.BasePO;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class MEquipmentOperationPO extends BasePO {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date workDate;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long equipId;
    private String equipName;
    /**
     * 运行台时
     */
    private BigDecimal runTime;
    /**
     * 作业台时
     */
    private BigDecimal workTime;
    /**
     * 故障台时
     */
    private BigDecimal faultTime;
    /**
     * 运行里程
     */
    private BigDecimal runMileage;
    /**
     * 作业吨数
     */
    private BigDecimal workTon;
    /**
     * 作业箱量
     */
    private BigDecimal workBox;
    /**
     * 耗油量
     */
    private BigDecimal oilConsume;
    /**
     * 耗电量
     */
    private BigDecimal powerConsume;
}
