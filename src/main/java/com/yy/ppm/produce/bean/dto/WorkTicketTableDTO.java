package com.yy.ppm.produce.bean.dto;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.yy.common.excel.export.bean.SheetMapping;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class WorkTicketTableDTO  extends SheetMapping {
    @ExcelIgnore
    private int sortNum;
    @ExcelIgnore
    private long id;
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ExcelProperty(value = "日期", index = 0)
    private Date workDate;
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ExcelIgnore
    private Date startTime;
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ExcelIgnore
    private Date endTime;
    @ExcelProperty(value = "班次", index = 1)
    private String className;
    @ExcelIgnore
    private String classCode;
    @ExcelProperty(value = "作业分类", index = 2)
    private String planType;//作业分类
    @ExcelProperty(value = "作业过程", index = 3)
    private String processName;
    @ExcelIgnore
    private String processCode;
    @ExcelIgnore
    private Long workTicketId;
    @ExcelProperty(value = "SCN", index = 4)
    private String scn;
    @ExcelProperty(value = "船名航次", index = 5)
    private String shipvoyage;
    @ExcelIgnore
    private String shipName;
    @ExcelIgnore
    private String voyage;
    @ExcelProperty(value = "包装", index = 6)
    private String packingName;
    @ExcelProperty(value = "通知单编号", index = 8)
    private String trustNo;
    @ExcelProperty(value = "票货号", index = 7)
    private String cargoInfoNo;
    @ExcelProperty(value = "货名", index = 9)
    private String cargoName;
    @ExcelIgnore
    private String deptName;
    @ExcelIgnore
    private Long deptId;
    private Long parentId;
    @ExcelProperty(value = "二级作业过程", index = 13)
    private String processDetailName;
    @ExcelIgnore
    private String processDetailCode;

    private Date startTimeMonth;
    private Date endTimeMonth;

    //位置信息
    @ExcelIgnore
    private String workPositionCode;
    @ExcelIgnore
    private String workPositionName;
    //外包合同分类
    @ExcelIgnore
    private String concatOutPacke;
    @ExcelIgnore
    private BigDecimal ticketTon;
    @ExcelIgnore
    private BigDecimal ton;
    private BigDecimal price;
    private BigDecimal amount;
    @ExcelIgnore
    private String equipmentTypeName;
    //分配类型
    @ExcelProperty(value = "分配类型", index = 12)
    private String allotType;
    @ExcelIgnore
    private String allotTypeLabel;
    @ExcelProperty(value = "前沿操作工班", index = 14)
    private String frontDeptName;
    @ExcelProperty(value = "机械类型", index = 15)
    private String frontEquipmentTypeName;
    @ExcelProperty(value = "前沿作业量", index = 16)
    private BigDecimal frontWorkTon;
    @ExcelProperty(value = "后场操作工班", index = 17)
    private String backDeptName;
    @ExcelProperty(value = "机械类型", index = 18)
    private String backEquipmentTypeName;
    @ExcelProperty(value = "后场作业量", index = 19)
    private BigDecimal backWorkTon;
    @ExcelProperty(value = "水平操作工班", index = 20)
    private String lineDeptName;
    @ExcelProperty(value = "机械类型", index = 21)
    private String lineEquipmentTypeName;
    @ExcelProperty(value = "水平作业量", index = 22)
    private BigDecimal lineWorkTon;
    @ExcelIgnore
    private String otherDeptName;
    @ExcelIgnore
    private String otherEquipmentTypeName;
    @ExcelIgnore
    private BigDecimal otherWorkTon;
    @ExcelProperty(value = "外包合同分类", index = 10)
    private String packingNameWaiBao;
    @ExcelProperty(value = "作业量", index = 11)
    private String workTon;
    @ExcelIgnore
    private String groupId;

    @ExcelProperty(value = "其中倒运过磅量", index = 23)
    private BigDecimal daoYunWeightGoods;
    @ExcelIgnore
    private String trustType;

    @ExcelIgnore
    private String isDirectAccess;
//    签票详情查询
    @ExcelIgnore
    private Long ticketDetailId;

    private String status;
    private String isVoidExStatus;
    private List<String> checkDateList;

    private Long userBy;
    private String userByName;
    private Date userTime;
    private Long userBy2;
    private String userByName2;
    private Date userTime2;
    //是否水平
    private String isShuiPing;


    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ExcelIgnore
    private Date startDate;
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ExcelIgnore
    private Date endDate;
    @ExcelIgnore
    private String externalStatus;


    @ExcelIgnore
    private String exFlag;
    //外服金额明细审核 1已审核 0未审核
    @ExcelIgnore
    private String calNewStatus;
}
