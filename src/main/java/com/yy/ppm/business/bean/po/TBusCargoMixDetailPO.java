package com.yy.ppm.business.bean.po;

import com.yy.ppm.common.bean.po.BasePO;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 票货混配明细表(TBusCargoMixDetail)PO
 *
 * @author linqi
 * @since 2024-03-04 16:31:59
 */
@Setter
@Getter
public class TBusCargoMixDetailPO extends BasePO {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 混配记录ID
     */
    private Long mixRecordId;

    /**
     * 原票货ID
     */
    @NotNull(message = "原票货ID不能为空")
    private Long cargoInfoId;

    /**
     * 库场ID
     */
    @NotNull(message = "库场ID不能为空")
    private Long storehouseId;

    /**
     * 区域ID
     */
    @NotNull(message = "区域ID不能为空")
    private Long regionId;

    /**
     * 垛位ID
     */
    @NotNull(message = "垛位ID不能为空")
    private Long massId;

    /**
     * 混配重量
     */
    @NotNull(message = "混配重量不能为空")
    private BigDecimal mixWeight;

    /**
     * 合同ID
     */
    @NotNull(message = "合同ID不能为空")
    private Long contractId;

    /**
     * 免堆存期
     */
    @NotNull(message = "免堆存期不能为空")
    private Integer freeStorageDays;

    /**
     * 剩余免堆存期
     */
    private Integer remainFreeStorageDays;
}

