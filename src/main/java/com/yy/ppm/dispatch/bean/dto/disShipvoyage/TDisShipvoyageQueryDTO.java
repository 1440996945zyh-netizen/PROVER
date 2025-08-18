package com.yy.ppm.dispatch.bean.dto.disShipvoyage;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yy.ppm.dispatch.bean.po.TDisShipvoyagePO;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

/**
 * @Author linqi
 * @Description
 * @Date 2023-07-04 11:13
 */
@Setter
@Getter
public class TDisShipvoyageQueryDTO extends TDisShipvoyagePO {

    /**
     * 船名航次
     */
    private String shipVoyage;

    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date startLeaveTime;
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endLeaveTime;

    private String cargoCategoryName;
}
