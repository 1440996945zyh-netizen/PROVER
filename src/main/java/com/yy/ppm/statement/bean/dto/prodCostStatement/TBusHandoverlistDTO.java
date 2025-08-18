package com.yy.ppm.statement.bean.dto.prodCostStatement;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yy.ppm.statement.bean.po.TBusHandoverlistPO;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Auther linqi
 * @Description
 * @Date 2023-09-07 11:04
 */
@Setter
@Getter
public class TBusHandoverlistDTO extends TBusHandoverlistPO {

    /**
     * 作业公司
     */
    private String companyName;

    /**
     * 作业公司
     */
    private Long companyId;

    /**
     * 船名航次
     */
    private String shipNameVoyage;

    /**
     * 内外贸Label
     */
    private String tradeTypeLabel;

    /**
     * 进出口Label
     */
    private String impExpLabel;

    /**
     * 进出口编码
     */
    private String impExp;

    /**
     * 离泊时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private Date leaveBerthTime;


    /**
     * scn 1108 回显预结算主列表的scn
     */
    private String scn;

    /**
     * 靠泊时间 1108 回显预结算主列表的靠泊时间
     */
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date berthTime;

    /**
     * 离泊时间 1108 回显预结算主列表的离泊时间
     */
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date leavePortTime;

    /**
     * 回显件杂
     */
    private String workType;

    /**
     * 直取作业量
     */
    private BigDecimal zqQuantity;

    /**
     * 过磅量
     */
    private BigDecimal weighCapacity;
    /**
     * 非陆销过磅量
     */
    private BigDecimal flxWeigh;
    /**
     * 陆销过磅量
     */
    private BigDecimal lxWeigh;

    private String trustNo;
    private String isClear;
    private String berthName;
}
