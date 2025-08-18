package com.yy.ppm.business.bean.dto.contractOld;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 老数据同步过磅信息
 */
@Setter
@Getter
public class TBusOldWeightDTO {

    /**
     * 合同ID
     */
    private String contractId;

    /**
     * 子合同ID
     */
    private String contractItemId;

    /**
     * 磅单ID
     */
    private Long noteId;

    private String unionNo;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date weighInDt;

    private String checkerInName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date weighOutDt;

    private String checkerOutName;

    private String invNo;

    private String planNo;

    private String inteFore;

    private String goodsName;

    private String truckPlate;

    private BigDecimal weightSelf;

    private BigDecimal weightAll;

    private BigDecimal weightGoods;

    private String comName;

    private String comNo;

    private String scn;

    private String portCode;

    private String goodsCode;

    private String tradeTyp;

    private String conUnit;

    private String recUnit;

    private String idNumber;

    private String invRem;

    private String tallyer;

    private String cargoInfoId;

}
