package com.yy.ppm.largescreen.bean.dto;


import com.yy.common.page.PageParameter;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.math.BigDecimal;

/**
 * @author makejava
 * @version 1.0.0
 * @ClassName (SShipTrends)SearchDTO
 * @Description TODO
 * @createTime 2024年03月15日 09:35:00
 */
@Data
public class SShipTrendsSearchDTO extends PageParameter implements Serializable {

    private static final long serialVersionUID = 443723876125714835L;

    /***/
    private Long id;
    /**
     * 创建者-姓名
     */
    private String createByName;
    /**
     * 港区代码（10：潍坊港，20：寿光港，30：东营港，40：滨州港）
     */
    private String portCode;
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
     * 船舶状态（1：靠泊2：到港3：离港）
     */
    private String shipStatus;
    /**
     * 靠泊时间/到港时间
     */
    private Date berthTime;
    /**
     * 离港时间
     */
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

