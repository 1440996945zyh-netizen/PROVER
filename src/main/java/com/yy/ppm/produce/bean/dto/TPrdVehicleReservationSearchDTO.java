package com.yy.ppm.produce.bean.dto;


import com.yy.common.page.PageParameter;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName (TBusPlanEntrust)DTO
 * @author wangxd
 * @version 1.0.0
 * @Description
 * @createTime 2024年01月22日 14:47:00
 */
@Data
public class TPrdVehicleReservationSearchDTO extends PageParameter implements Serializable {

    private static final long serialVersionUID = -57272243886403254L;

    private Long id;
    /**
     * 车牌号
     */
    private String vehicleNo;
    private List<String> vehicleNoList;
    /**
     * 计划号
     */
    private String planNo;
    /**
     * 子计划号
     */
    private String subPlanNo;
    /**
     * 司机
     */
    private String driverNameOne;
    /**
     * 状态
     */
    private String status;
    /**
     * 车牌号或司机
     */
    private String searchContent;
    /**
     * 动态状态
     */
    private String trendStatus;

    private String driverNoOne;

    private List<Long> idList;

}
