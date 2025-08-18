package com.yy.ppm.statement.bean.dto.busHandoverlist;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @Auther linqi
 * @Description
 * @Date 2023-09-07 14:28
 */
@Setter
@Getter
public class TDisShipvoyageItemQueryDTO {

    /**
     * 船舶航次ID
     */
    private Long shipvoyageItemId;

    /**
     * 状态CODE，预报、接收、抵锚..,（字典SHIP_STATUS）
     */
    private String shipStatusCode;
    /**
     * 是否开工，1 是；0 否
     */
    private String isStartWork;
    /**
     * 船名航次模糊查询
     */
    private String shipOrVoyage;
    /**
     * 票货号
     */
    private String cargoInfoNo;
    /**
     * 船代名称ID
     */
    private String customerId;
    /**
     * 货种
     */
    private String cargoCategoryName;
    /**
     * 离泊时间起始
     */
    @DateTimeFormat(pattern = "yy-MM-dd")
    private Date beginLeavePortTime;

    /**
     * 离泊时间截止
     */
    @DateTimeFormat(pattern = "yy-MM-dd")
    private Date endLeavePortTime;

    private String shipName;

    private String voyage;

}
