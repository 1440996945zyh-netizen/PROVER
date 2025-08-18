package com.yy.ppm.tallyExtrinsic.bean.dto;


import com.yy.ppm.tallyExtrinsic.bean.po.TBusReservationConfirmPO;
import lombok.Data;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @author yy
 * @version 1.0.0
 * @ClassName 前沿确认
 * @Description
 * @createTime 2024年06月13日 10:48:00
 */
@Data
public class TBusReservationConfirmDTO extends TBusReservationConfirmPO {

    private static final long serialVersionUID = -95785517457272995L;
    /** 票货id */
    private Long cargoInfoId;
    /** 票货号 */
    private String cargoInfoNo;
}
