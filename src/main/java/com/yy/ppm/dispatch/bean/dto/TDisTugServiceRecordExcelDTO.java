package com.yy.ppm.dispatch.bean.dto;


import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.yy.ppm.dispatch.bean.po.TDisTugServiceRecordPO;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @ClassName 拖轮服务记录(TDisTugServiceRecord)DTO
 * @author yy
 * @version 1.0.0
 * @Description
 * @createTime 2023年07月12日 11:45:00
 */
@Getter
@Setter
public class TDisTugServiceRecordExcelDTO{

    /** 拖轮名称 */
    @ExcelProperty(value = "拖轮名称", index = 0)
    private String tugName;

    /** 船名航次 */
    @ExcelProperty(value = "船名航次", index = 1)
    private String shipVoyage;

    /** scn */
    @ExcelProperty(value = "SCN", index = 2)
    private String scn;

    @ExcelProperty(value = "服务内容", index = 3)
    private String tugServiceTypeName;

    /** 开始时间 */
    @ExcelProperty(value = "开始时间", index = 4)
    private Date startTime;

    /** 结束时间 */
    @ExcelProperty(value = "结束时间", index = 5)
    private Date endTime;

    /** 服务时长（小时） */
    @ExcelProperty(value = "服务时长（小时）", index = 6)
    private BigDecimal timeLength;

    /** 备注 */
    @ExcelProperty(value = "备注", index = 7)
    private String remark;

    /** 中文船名 */
//    private String shipName;

    /**
     * 是否标准使用  1:是；0：否
     */
//    private String isStandardUse;

    /**
     * 非标准原因
     */
//    private String reasonName;

    
}
