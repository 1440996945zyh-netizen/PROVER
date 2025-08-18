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
public class SShipTrendsInfoDTO extends BasePO {
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
    @ExcelProperty(value = "船名", index = 1)
    @NotNull(message = "文件中船名列(第二列)有空数据")
    private String shipName;

    @ExcelProperty(value = "货名", index = 2)
    @NotNull(message = "文件中船名列(第三列)有空数据")
    private String cargoName;

    @ExcelProperty(value = "装/卸", index = 3)
    @NotNull(message = "文件中船名列(第四列)有空数据")
    private String loadOrUnload;

    @ExcelProperty(value = "载货吨", index = 4)
    @NotNull(message = "文件中船名列(第五列)有空数据")
    private String ton;

    @ExcelIgnore
    private String shipStatus;

    @ExcelProperty(value = "船舶状态", index = 5)
    @NotNull(message = "文件中船名列(第六列)有空数据")
    private String shipStatusLabel;
    public void setShipStatusLabel(String shipStatusLabel) {
        this.shipStatusLabel = shipStatusLabel;
        if("靠泊".equals(shipStatusLabel)){
            this.shipStatus = "1";
        } else if("到港".equals(shipStatusLabel)){
            this.shipStatus = "2";
        }
        else if("离港".equals(shipStatusLabel)){
            this.shipStatus = "3";
        }
        else if("开工".equals(shipStatusLabel)){
            this.shipStatus = "4";
        }
        else if("完工".equals(shipStatusLabel)){
            this.shipStatus = "5";
        }
        else if("停工".equals(shipStatusLabel)){
            this.shipStatus = "6";
        }
        else if("预报".equals(shipStatusLabel)){
            this.shipStatus = "9";
        }

    }

    @ExcelProperty(value = "时间", index = 6)
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @NotNull(message = "文件中船名列(第七列)有空数据")
    private Date berthTime;

    @ExcelProperty(value = "离港时间", index = 7)
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @NotNull(message = "文件中船名列(第八列)有空数据")
    private Date leaveTime;

    @ExcelProperty(value = "泊位", index = 8)
    @NotNull(message = "文件中船名列(第九列)有空数据")
    private String berthName;

    @ExcelProperty(value = "进度", index = 9)
    @NotNull(message = "文件中船名列(第十列)有空数据")
    private String schedule;
}
