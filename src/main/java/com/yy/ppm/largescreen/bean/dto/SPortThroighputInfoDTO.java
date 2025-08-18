package com.yy.ppm.largescreen.bean.dto;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.common.bean.po.BasePO;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.NotNull;
import java.util.Date;

@Data
public class SPortThroighputInfoDTO extends BasePO {
    @ExcelIgnore
    private Long id;
    /**
     * 港区代码（10：潍坊港，20：寿光港，30：东营港，40：滨州港）
     */
    @ExcelIgnore
    private String portCode;

    @ExcelProperty(value = "港区",index = 0)
    @NotNull(message = "文件中船名列(第一列)有空数据")
    private String portLabel;

    public void setPortLabel(String portLabel) {
        this.portLabel = portLabel;
        if("潍坊港".equals(portLabel)){
            this.portCode = "10";
        } else if("寿光港".equals(portLabel)){
            this.portCode = "20";
        } else if("东营港".equals(portLabel)){
            this.portCode = "30";
        } else if("滨州港".equals(portLabel)){
            this.portCode = "40";
        }else{
            throw new BusinessRuntimeException("港区无法添加，系统无此港区");
        }
    }

    /**
     * 时间类型（1：年，2：月，3：日）（3个抽屉）
     */
    @ExcelIgnore
    private String dateType;

    @ExcelProperty(value = "时间类型", index = 1)
    @NotNull(message = "文件中船名列(第一列)有空数据")
    private String dateTypeLabel;
    public void setDateTypeLabel(String dateTypeLabel) {
        this.dateTypeLabel = dateTypeLabel;
        if("年".equals(dateTypeLabel)){
            this.dateType = "1";
        } else if("月".equals(dateTypeLabel)){
            this.dateType = "2";
        } else if("日".equals(dateTypeLabel)){
            this.dateType = "3";
        }
    }

    @ExcelProperty(value = "时间", index = 2)
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date dateTime;

    @ExcelIgnore
    private String cargoType;

    public void setCargoTypeLabel(String cargoTypeLabel) {
        this.cargoTypeLabel = cargoTypeLabel;
        if("件货".equals(cargoTypeLabel)){
            this.cargoType = "1";
        } else if("散货".equals(cargoTypeLabel)){
            this.cargoType = "2";
        }
    }
    @ExcelProperty(value = "货物类型", index = 3)
    @NotNull(message = "文件中计划量列(第三列)有空数据")
    private String cargoTypeLabel;

    @ExcelProperty(value = "吨数", index = 4)
    @NotNull(message = "文件中计划量列(第四列)有空数据")
    private String ton;
}
