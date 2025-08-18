package com.yy.ppm.produce.bean.dto.workTicket;

import com.yy.ppm.produce.bean.po.TPrdWorkPlanPO;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

/**
 * @Auther linqi
 * @Description
 * @Date 2023-08-14 16:10
 */
@Setter
@Getter
public class TPrdWorkPlanQuery extends TPrdWorkPlanPO {
    private Integer flag;
    private String LoginId;
    private List<Long> ids;
    private String cargoType; //1件货 2散货

    private String startDay;
    private String endDay;

    private String ticketType;

    private String isTicket;

    private String shipName;
    private String voyage;
}
