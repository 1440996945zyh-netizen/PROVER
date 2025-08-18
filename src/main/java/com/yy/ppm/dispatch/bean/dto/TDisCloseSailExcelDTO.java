package com.yy.ppm.dispatch.bean.dto;


import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @ClassName 封航记录表(TDisCloseSail)DTO
 * @author yy
 * @version 1.0.0
 * @Description
 * @createTime 2023年07月12日 11:54:00
 */
@Getter
@Setter
public class TDisCloseSailExcelDTO{


    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd HH:mm")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    @ExcelProperty(value = "封航开始时间", index = 0)
    private Date startTime;

    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd HH:mm")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    @ExcelProperty(value = "封航结束时间", index = 1)
    private Date endTime;

    @ExcelProperty(value = "封航时长（小时）", index = 2)
    private BigDecimal timeLength;

    @ExcelProperty(value = "封航原因", index = 3)
    private String closeReasonName;

    @ExcelProperty(value = "影响艘次", index = 4)
    private int shipVoyageNum;

    @ExcelProperty(value = "注意事项", index = 5)
    private String remark;

    @ExcelProperty(value = "影响船舶", index = 6)
    private String effectShipvoyage;

}
