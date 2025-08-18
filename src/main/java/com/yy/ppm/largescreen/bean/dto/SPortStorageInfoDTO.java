package com.yy.ppm.largescreen.bean.dto;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.common.bean.po.BasePO;
import lombok.Data;

import jakarta.validation.constraints.NotNull;

@Data
public class SPortStorageInfoDTO extends BasePO {
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
    @ExcelProperty(value = "货名",index = 1)
    @NotNull(message = "文件中货名列(第四列)有空数据")
    private String cargoName;

    @ExcelProperty(value = "计划量", index = 2)
    @NotNull(message = "文件中计划量列(第五列)有空数据")
    private String ton;

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
    private String cargoTypeLabel;
}
