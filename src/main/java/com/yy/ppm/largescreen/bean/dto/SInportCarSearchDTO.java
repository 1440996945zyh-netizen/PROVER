package com.yy.ppm.largescreen.bean.dto;


import com.yy.common.page.PageParameter;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.math.BigDecimal;

/**
 * @author makejava
 * @version 1.0.0
 * @ClassName 在港车辆表(SInportCar)SearchDTO
 * @Description TODO
 * @createTime 2024年03月14日 10:42:00
 */
@Data
public class SInportCarSearchDTO extends PageParameter implements Serializable {

    private static final long serialVersionUID = 682065973393225526L;

    /***/
    private Long id;
    /**
     * 创建者-姓名
     */
    private String createByName;
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
    private Date inPortDate;
    /**
     * 在港时长(小数点后一位)
     */
    private BigDecimal inPortTime;
}

