package com.yy.ppm.master.bean.dto;


import com.yy.common.page.PageParameter;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.math.BigDecimal;

/**
 * @author yy
 * @version 1.0.0
 * @ClassName 海轮资料(MShip)SearchDTO
 * @Description TODO
 * @createTime 2023年06月27日 15:44:00
 */
@Data
public class MShipSearchDTO extends PageParameter implements Serializable {

    private static final long serialVersionUID = -62241118078007811L;

    /**
     * 船名
     */
    private Long id;

    /**
     * 船名
     */
    private String shipName;
    /**
     * 船英文名
     */
    private String shipNameEn;
    /**
     * 助记码
     */
    private String shorthandCode;
    /**
     * 船舶类型 （字典 SHIP_KIND)
     */
    private String shipKindCode;
    /**
     * 国籍 船籍代码 （字典 NATION)
     */
    private String nationCode;
    /**
     * 船型 (字典 SHIP_TYPE)
     */
    private String shipTypeCode;
    /**
     * IMO
     */
    private String imo;
    /**
     * MMSI
     */
    private String mmsi;
    /**
     * 呼号
     */
    private String callNo;

    private String status;

//    private String companyId;
}

