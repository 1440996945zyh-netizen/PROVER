package com.yy.ppm.business.bean.dto.assignFleet;

import lombok.Getter;
import lombok.Setter;

/**
 * @Author linqi
 * @Description
 * @Date 2023-07-05 10:14
 */
@Setter
@Getter
public class TBusTrustCargoQueryDTO {

    /**
     * 指令编号
     */
    private String trustNo;

    /**
     * 作业过程编码
     */
    private String processCode;

    /**
     * 作业公司id
     */
    private Long companyId;

    /**
     * 货主id
     */
    private Long cargoOwnerId;

    /**
     * 货代id
     */
    private Long cargoAgentId;

    /**
     * 货物编码
     */
    private String cargoCode;

    /**
     * 已指派车队id
     */
    private Long customerId;

    /**
     * 航次子表id
     */
    private Long shipvoyageItemId;
}
