package com.yy.ppm.largescreen.bean.po;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import com.yy.ppm.common.bean.po.BasePO;
import org.springframework.format.annotation.DateTimeFormat;
import java.util.Date;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author makejava
 * @version 1.0.0
 * @ClassName 港区吞吐量表(SPortThroighput)PO
 * @Description
 * @createTime 2024年03月15日 09:24:00
 */
@Data
public class SPortThroighputPO extends BasePO implements Serializable {

    private static final long serialVersionUID = 623997644558800523L;

    /**
     *
     */
    private Long id;
    /**
     * 港区代码（10：潍坊港，20：寿光港，30：东营港，40：滨州港）
     */
    private String portCode;
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

    /**
     * 时间类型（1：年，2：月，3：日）（3个抽屉）
     */
    private String dateType;
    private String dateTypeLabel;
    public void setDateType(String dateType) {
        this.dateType = dateType;
        if("1".equals(dateType)){
            this.dateTypeLabel = "年";
        } else if("2".equals(dateType)){
            this.dateTypeLabel = "月";
        } else if("3".equals(dateType)){
            this.dateTypeLabel = "日";
        }
    }
    /**
     * 货物类型（1：散杂货2：集装箱）
     */
    private String cargoType;
    private String cargoTypeLabel;
    public void setCargoType(String cargoType) {
        this.cargoType = cargoType;
        if("2".equals(cargoType)){
            this.cargoTypeLabel = "集装箱";
        } else if("1".equals(cargoType)){
            this.cargoTypeLabel = "散杂货";
        }else if("3".equals(cargoType)){
            this.cargoTypeLabel = "液化品";
        }else if("4".equals(cargoType)){
            this.cargoTypeLabel = "木片";
        }
    }
    /**
     * 吨数/TEU
     */
    private Long ton;

    /**
     * 时间（1：年，2：月，3：日）
     */
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date dateTime;

}

