package com.yy.ppm.statement.bean.dto.costShipWaterElectricity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Auther linqi
 * @Description
 * @Date 2023-09-23 14:32
 */
@Setter
@Getter
public class TPrdWaterElectricityDTO {

    private Long id;

    private String processCode;

    private String processName;

    private BigDecimal quantity;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    private String createByName;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    private String fileIds;

    private Long shipvoyageId;
}
