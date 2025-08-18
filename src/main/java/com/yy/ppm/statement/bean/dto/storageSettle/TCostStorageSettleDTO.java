package com.yy.ppm.statement.bean.dto.storageSettle;

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

    private String statementStatus;

    private String statementStatusLabel;

    private String statementId;
    private String confirmByName;
    private String statementByName;

    private String statementNo;
}
