package com.yy.ppm.master.bean.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yy.ppm.common.bean.po.BasePO;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;


@Data
public class MWorkSchedulePO extends BasePO implements Serializable {

    private static final long serialVersionUID = -51136538517422258L;


    private Long id;
    /** 早中晚班，字典 */
    private String workScheduleCode;
    /** 开始日 0：昨日；1:本日；2：次日 */
    private String startDayType;
    @JsonFormat(timezone="GMT+8",pattern="HH:mm")
    @DateTimeFormat(pattern = "HH:mm")
    private Date startTime;
    /** 开始日 0：昨日；1:本日；2：次日 */
    private String endDayType;
    @JsonFormat(timezone="GMT+8",pattern="HH:mm")
    @DateTimeFormat(pattern = "HH:mm")
    private Date endTime;

}
