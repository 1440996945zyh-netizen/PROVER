package com.yy.ppm.statement.bean.dto.storageCalculate;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @Auther linqi
 * @Description
 * @java.util.Date 2023-11-24 14:05
 */
@Setter
@Getter
public class TBusCargoInfoQueryDTO {

    private String cargoInfoNo;

    private Long shipvoyageItemId;

    private Long cargoOwnerId;

    private String shipStatusCode;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date beginLeaveBerthTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endLeaveBerthTime;

    private String cargoName;

    /**
     * 是否超期 0否/1是
     */
    private String isOverdue;

    private String isClear;

    private String cargoCode;

    private String impExp;
}
