package com.yy.ppm.largescreen.bean;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.common.bean.po.BasePO;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class SInportCarInfoDTO extends BasePO {
    @ExcelIgnore
    private Long id;

    @ExcelProperty(value = "车牌号",index = 0)
    @NotNull(message = "文件中车牌号列(第一列)有空数据")
    private String carNum;

    @ExcelProperty(value = "船名",index = 1)
    @NotNull(message = "文件中船名列(第二列)有空数据")
    private String shipName;

    /**
     * 港区代码（10：潍坊港，20：寿光港，30：东营港，40：滨州港）
     */
    @ExcelIgnore
    private String portCode;

    @ExcelProperty(value = "港区",index = 2)
    @NotNull(message = "文件中船名列(第三列)有空数据")
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
    @ExcelProperty(value = "货名",index = 3)
    @NotNull(message = "文件中货名列(第四列)有空数据")
    private String cargoName;

    @ExcelProperty(value = "计划量", index = 4)
    @NotNull(message = "文件中计划量列(第五列)有空数据")
    private String planTon;

    @ExcelProperty(value = "货主", index = 5)
    @NotNull(message = "文件中货主列(第六列)有空数据")
    private String cargoOwner;
    /**
     * 进港时间（年月日）
     */
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ExcelProperty(value = "进港时间", index = 6)
    @NotNull(message = "文件中进港时间列(第七列)有空数据")
    private Date inPortDate;
    /**
     * 在港时长(小数点后一位)
     */
    @ExcelProperty(value = "在港时长", index = 7)
    @NotNull(message = "文件中在港时长列(第八列)有空数据")
    private BigDecimal inPortTime;
}
