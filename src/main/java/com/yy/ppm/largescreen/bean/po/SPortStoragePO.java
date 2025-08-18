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
 * @ClassName (SPortStorage)PO
 * @Description
 * @createTime 2024年03月14日 23:13:00
 */
@Data
public class SPortStoragePO extends BasePO implements Serializable {

    private static final long serialVersionUID = 596212539191389129L;

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
     * 货名
     */
    private String cargoName;
    /**
     * 计划量
     */
    private Long ton;
    /**
     * 货物类型（1：件2：散）（新增，查询，删除）
     */
    private String cargoType;
    private String cargoTypeLabel;
    public void setCargoType(String cargoType) {
        this.cargoType = cargoType;
        if("1".equals(cargoType)){
            this.cargoTypeLabel = "件货";
        } else if("2".equals(cargoType)){
            this.cargoTypeLabel = "散货";
        }
    }


}

