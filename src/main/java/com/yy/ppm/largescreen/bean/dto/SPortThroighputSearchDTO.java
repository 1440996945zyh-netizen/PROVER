package com.yy.ppm.largescreen.bean.dto;


import com.yy.common.page.PageParameter;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.math.BigDecimal;

/**
 * @author makejava
 * @version 1.0.0
 * @ClassName 港区吞吐量表(SPortThroighput)SearchDTO
 * @Description TODO
 * @createTime 2024年03月15日 09:24:00
 */
@Data
public class SPortThroighputSearchDTO extends PageParameter implements Serializable {

    private static final long serialVersionUID = 410900951709671219L;

    /***/
    private Long id;
    /**
     * 港区代码（10：潍坊港，20：寿光港，30：东营港，40：滨州港）
     */
    private String portCode;
    /**
     * 时间类型（1：年，2：月，3：日）（3个抽屉）
     */
    private String dateType;
    /**
     * 货物类型（1：散杂货2：集装箱）
     */
    private String cargoType;
    /**
     * 吨数/TEU
     */
    private Long ton;
    /**
     * 创建者-姓名
     */
    private String createByName;
}

