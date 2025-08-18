package com.yy.ppm.business.bean.dto.TBusCargoMix;

import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotNull;

@Setter
@Getter
public class TPrdPortStorageQueryDTO {

    /**
     * 货主ID
     */
    @NotNull(message = "货主ID不能为空")
    private Long cargoOwnerId;

    private Long mixRecordId;
}
