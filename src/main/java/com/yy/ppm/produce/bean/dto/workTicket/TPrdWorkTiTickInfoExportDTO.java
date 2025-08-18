package com.yy.ppm.produce.bean.dto.workTicket;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Setter
@Getter
public class TPrdWorkTiTickInfoExportDTO implements Serializable
{
    @ExcelIgnore
    private Long trustId;

    @ExcelIgnore
    private String shipVoyageIds;

    @ExcelProperty(value = "通知单编号", index = 0)
    private String trustNo;

    @ExcelProperty(value = "签票人", index = 1)
    private String createByName;

    /**
     * 录入时间
     */
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ExcelProperty(value = "签票时间", index = 2)
    private Date createTime;

    /**
     * 状态:字典(WORK_TICKET_STATUS)
     */
    @ExcelProperty(value = "审核状态", index = 3)
    private String workTicketStatusName;

    /**
     * 审核人姓名
     */
    @ExcelProperty(value = "审核人", index = 4)
    private String examineByName;

    /**
     * 审核时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ExcelProperty(value = "审核时间", index = 5)
    private Date examineTime;

    /**
     * 班次名称
     */
    @ExcelProperty(value = "班次名称", index = 6)
    private String className;

    /**
     * 船名
     */
    @ExcelProperty(value = "船名", index = 7)
    private String shipNameVoyage;

    @ExcelProperty(value = "装卸", index = 8)
    private String loadUnload;

    /**
     * 部门名称
     */
    @ExcelProperty(value = "部门名称", index = 9)
    private String deptName;

    @ExcelProperty(value = "货主", index = 10)
    private String cargoOwnerName;

    @ExcelProperty(value = "货名", index = 11)
    private String cargoName;

    @ExcelProperty(value = "票货号", index = 12)
    private String cargoInfoNo;

    @ExcelProperty(value = "操作班组", index = 13)
    private String deptItemName;

    @ExcelProperty(value = "人员班组", index = 14)
    private String personNelName;

    /**
     * 子作业过程名称
     */
    @NotBlank(message = "子作业过程名称不能为空")
    @ExcelProperty(value = "作业过程", index = 15)
    private String processDetailName;

    @ExcelProperty(value = "件数", index = 16)
    private String quantity;

    @ExcelProperty(value = "吨数", index = 17)
    private String ton;

    @ExcelProperty(value = "机械类型", index = 18)
    private String equipmentTypeName;

    @ExcelProperty(value = "机械编号", index = 19)
    private String equipmentNo;

    @ExcelProperty(value = "开始时间", index = 20)
    private String startTime;

    /**
     * 结束时间
     */
    @ExcelProperty(value = "结束时间", index = 21)
    private String endTime;

    @ExcelProperty(value = "起始区域", index = 22)
    private String storehouseNameSourceLabel;

    @ExcelProperty(value = "终点区域", index = 23)
    private String storehouseNameTargetLabel;

}
