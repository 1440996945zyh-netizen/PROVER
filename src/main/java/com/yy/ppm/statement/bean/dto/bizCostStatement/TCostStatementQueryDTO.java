package com.yy.ppm.statement.bean.dto.bizCostStatement;

import com.yy.ppm.statement.bean.po.TCostStatementPO;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @Auther linqi
 * @Description
 * @Date 2023-09-14 14:36
 */
@Setter
@Getter
public class TCostStatementQueryDTO extends TCostStatementPO {

    /**
     * 请求类型  256  陆集陆疏   134  普通包干费   通过页面上的routeType进行控制
     */
    private String routeType;
    /**
     * 船名航次下拉框
     */
    private Long shipvoyageItemId;

    /**
     * 批量查询id
     */
    private List<Long> settlementIds;

    /**
     * 货名
     */
    private String cargoName;

    private String scn;
    private String shipName;
    private String voyage;

    private String workType;
    /**
     * 金额是否为0
     */
    private String isAmountZero;

    private String trustNo;

    private String isClear;

    private String cargoInfoNo;
}
