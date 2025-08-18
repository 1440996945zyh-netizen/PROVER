package com.yy.ppm.largescreen.bean.dto;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

@Setter
@Getter
public class SShipTrendsExportDTO implements Serializable {

    @ExcelProperty(value = "港区代码", index = 0)
    @ExcelIgnore
    private String portCode;

    @ExcelProperty(value = "港区", index = 1)
    private String portLabel;
    public void setPortCode(String portCode) {
        this.portCode = portCode;
        if("10".equals(portCode)){
            this.portLabel = "潍坊港";
        } else if("20".equals(portCode)){
            this.portLabel = "寿光港";
        } else if("30".equals(portCode)){
            this.portLabel = "东营港";
        } else if("40".equals(portCode)){
            this.portLabel = "滨州港";
        }
    }
    @ExcelProperty(value = "船名", index = 2)
    private String shipName;

    @ExcelProperty(value = "货名", index = 3)
    private String cargoName;

    @ExcelProperty(value = "装/卸", index = 4)
    private String loadOrUnload;

    @ExcelProperty(value = "载货吨", index = 5)
    private String ton;

    @ExcelProperty(value = "船舶状态代码", index = 6)
    @ExcelIgnore
    private String shipStatus;
    @ExcelProperty(value = "船舶状态", index = 7)
    private String shipStatusLabel;
    public void setShipStatus(String shipStatus) {
        this.shipStatus = shipStatus;
        if("1".equals(shipStatus)){
            this.shipStatusLabel = "靠泊";
        } else if("2".equals(shipStatus)){
            this.shipStatusLabel = "到港";
        }
        else if("3".equals(shipStatus)){
            this.shipStatusLabel = "离港";
        }
        else if("4".equals(shipStatus)){
            this.shipStatusLabel = "开工";
        }
        else if("5".equals(shipStatus)){
            this.shipStatusLabel = "完工";
        }
        else if("6".equals(shipStatus)){
            this.shipStatusLabel = "停工";
        }
        else if("9".equals(shipStatus)){
            this.shipStatusLabel = "预报";
        }

    }

    @ExcelProperty(value = "时间", index = 8)
    private String berthTime;

    @ExcelProperty(value = "离港时间", index = 9)
    private String leaveTime;

    @ExcelProperty(value = "泊位", index = 10)
    private String berthName;

    @ExcelProperty(value = "进度", index = 11)
    private String schedule;

    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ExcelProperty(value = "创建时间", index = 12)
    private Date createTime;


}
