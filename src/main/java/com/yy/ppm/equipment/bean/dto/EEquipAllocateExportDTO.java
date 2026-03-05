package com.yy.ppm.equipment.bean.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * 设备调拨导出DTO
 * @author system
 */
@Getter
@Setter
@ToString
public class EEquipAllocateExportDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 调拨编号
     */
    @ExcelProperty(value = "申请单号")
    @ColumnWidth(20)
    private String allocateCode;

    /**
     * 调拨标题
     */
    @ExcelProperty(value = "标题")
    @ColumnWidth(30)
    private String title;

    /**
     * 调入单位名称
     */
    @ExcelProperty(value = "调入单位")
    @ColumnWidth(20)
    private String toCompanyName;

    /**
     * 调入部门名称
     */
    @ExcelProperty(value = "调入部门")
    @ColumnWidth(20)
    private String toOrgName;

    /**
     * 申请人姓名
     */
    @ExcelProperty(value = "申请人")
    @ColumnWidth(15)
    private String applyUserName;

    /**
     * 申请原因
     */
    @ExcelProperty(value = "申请原因")
    @ColumnWidth(40)
    private String applyReason;

    /**
     * 审批状态名称
     */
    @ExcelProperty(value = "审批状态")
    @ColumnWidth(15)
    private String statusName;

    /**
     * 调拨时间
     */
    @ExcelProperty(value = "调拨时间")
    @ColumnWidth(20)
    private Date allocateTime;

    /**
     * 创建时间
     */
    @ExcelProperty(value = "创建时间")
    @ColumnWidth(20)
    private Date createTime;

}
