package com.yy.ppm.largescreen.bean.po;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import com.yy.ppm.common.bean.po.BasePO;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author makejava
 * @version 1.0.0
 * @ClassName 在港车辆表(SInportCar)PO
 * @Description
 * @createTime 2024年03月14日 10:42:00
 */
@Data
public class SInportCarPO extends BasePO implements Serializable {

    private static final long serialVersionUID = 318924576215794258L;

    /**
     *
     */
    private Long id;
    /**
     * 车牌号
     */
    private String carNum;
    /**
     * 船名
     */
    private String shipName;
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
     * 货名
     */
    private String cargoName;
    /**
     * 计划量
     */
    private Long planTon;
    /**
     * 货主
     */
    private String cargoOwner;
    /**
     * 进港时间（年月日）
     */
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date inPortDate;
    /**
     * 在港时长(小数点后一位)
     */
    private BigDecimal inPortTime;

}

