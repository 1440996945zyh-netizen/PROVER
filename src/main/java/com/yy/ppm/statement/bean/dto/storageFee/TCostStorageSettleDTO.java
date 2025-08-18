package com.yy.ppm.statement.bean.dto.storageFee;

import com.yy.ppm.statement.bean.po.TCostStorageSettlePO;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @Auther linqi
 * @Description
 * @Date 2023-11-24 10:11
 */
@Setter
@Getter
public class TCostStorageSettleDTO extends TCostStorageSettlePO {

    /**
     * 明细
     */
    @NotEmpty(message = "明细不能为空")
    private List<TCostStorageSettleDetailDTO> details;

    /**
     * 合同编号
     */
    private String contractNo;

    /**
     * 结算状态
     */
    private String statementStatus;

    /**
     * 结算状态Label
     */
    private String statementStatusLabel;

    /**
     * 结算单ID
     */
    private String statementId;
}
