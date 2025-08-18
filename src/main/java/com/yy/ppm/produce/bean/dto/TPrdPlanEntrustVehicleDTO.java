package com.yy.ppm.produce.bean.dto;


import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName (TBusPlanEntrust)DTO
 * @author wangxd
 * @version 1.0.0
 * @Description
 * @createTime 2024年01月24日 14:47:00
 */
@Data
public class TPrdPlanEntrustVehicleDTO implements Serializable {

    private static final long serialVersionUID = -57272243886403294L;
    /**
     * 计划号
     */
    private String planNo;
    private String subPlanNo;
    /**
     * 预约车辆
     */
    private Integer orderNum;
    /**
     * 停止车辆
     */
    private Integer stopNum;
	
}
