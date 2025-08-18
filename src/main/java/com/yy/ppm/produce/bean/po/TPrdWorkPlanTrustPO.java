package com.yy.ppm.produce.bean.po;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import com.yy.ppm.common.bean.po.BasePO;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @ClassName 工班计划指令(TPrdWorkPlanTrust)PO
 * @author yy
 * @version 1.0.0
 * @Description
 * @createTime 2023年08月09日 12:19:00
 */
@Data
public class TPrdWorkPlanTrustPO extends BasePO implements Serializable {

    private static final long serialVersionUID = 386515281662233791L;

        /** 计划指令ID */
    private Long id;
            /** 工班计划ID */
    private Long workPlanId;
            /** 指令ID */
    private Long trustId;

}

