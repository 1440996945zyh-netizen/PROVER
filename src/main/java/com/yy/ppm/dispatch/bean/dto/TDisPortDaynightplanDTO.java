package com.yy.ppm.dispatch.bean.dto;


import com.yy.ppm.dispatch.bean.po.TDisPortDaynightplanPO;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @ClassName 集疏港昼夜计划(TDisPortDaynightplan)DTO
 * @author yy
 * @version 1.0.0
 * @Description
 * @createTime 2023年11月14日 10:31:00
 */
@Data
public class TDisPortDaynightplanDTO extends TDisPortDaynightplanPO {

    private static final long serialVersionUID = 933843842890294965L;

    private String scn;
    private String shipVoyageLabel;
    private BigDecimal ton;
    private String searchDate;
    private String cargoCode;
    private String planTypeName;
    //过磅量
    private BigDecimal weighCount;
    //昼夜计划剩余量
    private BigDecimal remainCount;
    //计划总量
    private BigDecimal allPlanCount;
    //计划剩余量
    private BigDecimal remainPlanCount;

    private List<Long> ids;

    private String trustNo;

    private String companyName;

    private Long companyCode;

    private String settlementBasisName;

    private String type;

    private String remark;

    private String shipStatusName;

}
