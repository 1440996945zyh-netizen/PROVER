package com.yy.ppm.produce.bean.dto.workTicket;

import com.yy.ppm.business.bean.po.TBusTrustPO;
import lombok.Getter;
import lombok.Setter;

/**
 * @Auther linqi
 * @Description
 * @Date 2023-08-15 10:13
 */
@Setter
@Getter
public class TBusTrustDTO extends TBusTrustPO {

    /**
     * 作业计划id
     */
    private Long workPlanId;
}
