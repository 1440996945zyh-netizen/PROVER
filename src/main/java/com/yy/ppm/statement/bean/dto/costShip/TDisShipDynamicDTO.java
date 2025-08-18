package com.yy.ppm.statement.bean.dto.costShip;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @Auther linqi
 * @Description
 * @Date 2023-09-22 15:11
 */
@Setter
@Getter
public class TDisShipDynamicDTO {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 动态开始时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date dynamicStartTime;

    /**
     * 停工原因名称
     */
    private String stopName;

    /**
     * 停时类型名称
     */
    private String stopTypeName;

    /**
     * 停工原因类型
     */
    private String stopReasonTypeCode;

    /**
     * 停工时长
     */
    private Integer stopHours;
}
