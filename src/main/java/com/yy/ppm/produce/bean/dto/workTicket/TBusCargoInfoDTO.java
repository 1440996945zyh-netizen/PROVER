package com.yy.ppm.produce.bean.dto.workTicket;

import com.yy.ppm.business.bean.po.TBusCargoInfoPO;
import lombok.Getter;
import lombok.Setter;

/**
 * @Auther linqi
 * @Description
 * @Date 2023-08-17 10:23
 */
@Setter
@Getter
public class TBusCargoInfoDTO extends TBusCargoInfoPO {

    /**
     * 指令id
     */
    private Long trustId;

    private Long workPlanId;

    /**
     * 指令票货id
     */
    private Long trustCargoId;

    /**
     * 票货label
     */
    private String cargoLabel;

    /**
     * 操作过程编码
     */
    private String processCode;

    /**
     * 操作过程名称
     */
    private String processName;
}
