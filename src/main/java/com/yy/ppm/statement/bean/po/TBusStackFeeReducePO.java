package com.yy.ppm.statement.bean.po;

import com.yy.ppm.common.bean.po.BasePO;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotNull;
import java.util.Date;

/**
 * (TBusStackFeeReduce)PO
 *
 * @author linqi
 * @since 2024-03-18 15:58:41
 */
@Setter
@Getter
public class TBusStackFeeReducePO extends BasePO {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 票货id
     */
    @NotNull(message = "票货ID不能为空")
    private Long cargoInfoId;

    /**
     * 优惠类型
     */
    private String reduceType;

    /**
     * 优惠天数
     */
    private Integer reduceDays;

    /**
     * 减免截止日期
     */
    private Date reduceEndDate;

    /**
     * 备注
     */
    private String remark;
}

