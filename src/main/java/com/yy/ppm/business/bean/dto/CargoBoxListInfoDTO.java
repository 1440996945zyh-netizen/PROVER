package com.yy.ppm.business.bean.dto;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.yy.ppm.common.bean.po.BasePO;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.NotNull;
import java.util.Date;

@Data
public class CargoBoxListInfoDTO extends BasePO {
    @ExcelProperty(value = "序号",index = 0)
    @NotNull(message = "文件中序号列(第一列)有空数据")
    private String num;

    @ExcelProperty(value = "箱号",index = 1)
    @NotNull(message = "文件中箱号列(第二列)有空数据")
    private String coilNum;

    @ExcelProperty(value = "重量",index = 2)
    @NotNull(message = "文件中重量列(第三列)有空数据")
    private String ton;

    @ExcelProperty(value = "尺寸1",index =3)
    @NotNull(message = "文件中尺寸1(第四列)有空数据")
    private String coilSize1;

    @ExcelProperty(value = "尺寸2",index = 4)
    private String coilSize2;

    /**
     * 收货地址
     */
    @ExcelProperty(value = "收货地址",index = 5)
    private String receiveAddress;

    @ExcelIgnore
    @NotNull(message = "请选中票货")
    private Long cargoInfoId;
    @ExcelIgnore
    private Long id;

    /**
     * 10：已导入  20：入库 30：出库
     */
    @ExcelIgnore
    private String status;
    /**
     * 出库人Id
     */
    @ExcelIgnore
    private String outBoundBy;
    /**
     * 出库人
     */
    @ExcelIgnore
    private String outBoundByName;
    /**
     * 出库时间
     */
    @ExcelIgnore
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date outBoundTime;
    /**
     * 入库人ID
     */
    @ExcelIgnore
    private String wareHousingBy;
    /**
     * 入库人
     */
    @ExcelIgnore
    private String wareHousingByName;
    /**
     * 时间
     */
    @ExcelIgnore
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date wareHousingTime;

    /**
     * 业务类型：1卷钢2集装箱
     */
    @ExcelIgnore
    private String businessType;
}
