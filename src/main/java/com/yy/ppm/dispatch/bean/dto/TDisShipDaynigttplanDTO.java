package com.yy.ppm.dispatch.bean.dto;


import com.yy.ppm.dispatch.bean.po.TDisShipDaynigttplanPO;
import lombok.Data;

/**
 * @ClassName 船舶昼夜计划(TDisShipDaynigttplan)DTO
 * @author yy
 * @version 1.0.0
 * @Description
 * @createTime 2023年07月17日 10:31:00
 */
@Data
public class TDisShipDaynigttplanDTO extends TDisShipDaynigttplanPO {

    private static final long serialVersionUID = 933843842890294965L;

    private String scn;
    private String shipVoyageLabel;

}
