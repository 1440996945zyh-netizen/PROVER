package com.yy.ppm.equipment.bean.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * 设备报废导出DTO
 * @author system
 */
@Getter
@Setter
@ToString
public class EEquipScrapExportDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 工单号
     */
    @ExcelProperty(value = "工单号")
    @ColumnWidth(20)
    private String scrapCode;

    /**
     * 标题
     */
    @ExcelProperty(value = "标题")
    @ColumnWidth(30)
    private String title;

    /**
     * 所属公司名称
     */
    @ExcelProperty(value = "所属单位")
    @ColumnWidth(20)
    private String useCompanyName;

    /**
     * 所属部门名称
     */
    @ExcelProperty(value = "所属部门")
    @ColumnWidth(20)
    private String useOrgName;

    /**
     * 申请人姓名
     */
    @ExcelProperty(value = "申请人")
    @ColumnWidth(15)
    private String applyUserName;


    /**
     * 审批状态名称
     */
    @ExcelProperty(value = "审批状态")
    @ColumnWidth(15)
    private String statusName;

    /**
     * 创建时间
     */
    @ExcelProperty(value = "创建时间")
    @ColumnWidth(20)
    private Date createTime;


}
