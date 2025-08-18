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
 * @ClassName (SShipTrends)PO
 * @Description
 * @createTime 2024年03月15日 09:35:00
 */
@Data
public class SShipTrendsPO extends BasePO implements Serializable {

    private static final long serialVersionUID = 368546803884783951L;

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
     * 船名
     */
    private String shipName;
    /**
     * 货名
     */
    private String cargoName;
    /**
     * 装/卸
     */
    private String loadOrUnload;
    /**
     * 载货吨
     */
    private Long ton;
    /**
     * 船舶状态（1：靠泊 2：到港 3：离港）
     */
    private String shipStatus;
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

    /**
     * 靠泊时间/到港时间
     */
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date berthTime;
    /**
     * 离港时间
     */
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date leaveTime;
    /**
     * 泊位（输入框）
     */
    private String berthName;
    /**
     * 进度（小数点后2位）（新增，删除，查询）
     */
    private BigDecimal schedule;

}

