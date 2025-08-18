package com.yy.ppm.produce.bean.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yy.common.page.PageParameter;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
@Data
public class TWholeShipAdjustmentQueryDTO extends PageParameter implements Serializable {
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date startTime;
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endTime;

    private Long shipVoyageItemId;

    private String shipName;
    private String voyage;

    /**
     *  2是机械 3 是装卸队
     */
    private String allotType;
    //scn
    private String scn;

    private String isWorkEnd;

    /**
     *  1调度确认 2 库场确认
     */
    private String deptConfirm;

    private String shipStatusCode;
}
