package com.yy.ppm.statement.bean.dto.storageSettle;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.NotBlank;
import java.util.Date;

/**
 * @Auther linqi
 * @Description
 * @java.util.Date 2023-11-24 14:05
 */
@Setter
@Getter
public class TBusHandoverlistQueryDTO {

    @NotBlank(message = "类型不能为空")
    private String type;

    private Long shipvoyageItemId;

    private Long cargoOwnerId;

    private String shipStatusCode;

    private String statementStatus;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date beginLeaveBerthTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endLeaveBerthTime;

    private String cargoName;

    /**
     * 是否超期 0否/1是
     */
    private String isOverdue;

    private String isFinal;

    private String cargoCode;

    private String impExp;

    private String shipName;
    private String voyage;

    private String workType;
    /**
     * 金额是否为0
     */
    private String isAmountZero;
    /**
     * 是否完货
     */
    private String isClear;

    /**
     * SCN
     */
    private String scn;

    /**
     * 合同编号
     */
    private String contractCode;
}
