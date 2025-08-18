package com.yy.ppm.statement.bean.dto.storageFee;

import com.yy.ppm.statement.bean.po.TCostStorageSettleDetailPO;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * @Auther linqi
 * @Description
 * @Date 2023-11-24 10:11
 */
@Setter
@Getter
public class TCostStorageSettleDetailDTO extends TCostStorageSettleDetailPO {

    /**
     * 本日应计费量（减免前）
     */
    private BigDecimal originalBillableTon;

    /**
     * 本日结算金额（减免前）
     */
    @NotNull(message = "本日结算金额（减免前）不能为空")
    private BigDecimal originalAmount;

    /**
     * 票货ID
     */
    private Long cargoInfoId;
}
