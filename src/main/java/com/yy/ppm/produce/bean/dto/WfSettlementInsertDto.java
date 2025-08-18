package com.yy.ppm.produce.bean.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.yy.ppm.common.bean.po.BasePO;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class WfSettlementInsertDto extends BasePO {
    private Long id;

    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ExcelProperty(value = "时间", index = 0)
    private Date settlementDate;
    @ExcelProperty(value = "分配类型", index = 2)
    private String distributeType;
    private Long deptId;
    @ExcelProperty(value = "外包单位", index = 1)
    private String deptName;
    private Long parentId;
    private String parentName;
    private String outwardTypeCode;
    @ExcelProperty(value = "外付货物类型", index = 3)
    private String outwardTypeName;
    private String processCode;
    @ExcelProperty(value = "作业过程", index = 4)
    private String processName;
    private String processDetailCode;
    @ExcelProperty(value = "二级作业过程", index = 6)
    private String processDetailName;
    private String workPositionCode;
    @ExcelProperty(value = "位置", index = 5)
    private String workPositionName;
    @ExcelProperty(value = "备注", index = 7)
    private String mechanicalType;
    @ExcelProperty(value = "作业量", index = 8)
    private BigDecimal workTon;
    private BigDecimal weightTon;
    @ExcelProperty(value = "作业费率", index = 9)
    private BigDecimal workPrice;
    private BigDecimal weightPrice;
    private String ddStatus;
    private String kcStatus;
    private String lzStatus;
    private String wfStatus;
    @ExcelProperty(value = "金额", index = 10)
    private BigDecimal amount;
    private String remark;
    private Long createBy;
    private String createByName;
    private Date createTime;
    private Long updateBy;
    private String updateByName;
    private Date updateTime;
    private String isDaoyun;
}
