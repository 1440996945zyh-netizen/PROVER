package com.yy.ppm.business.bean.dto.TBusCargoMix;

import com.yy.ppm.produce.bean.po.TPrdPortStoragePO;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TPrdPortStorageDTO extends TPrdPortStoragePO {

    /**
     * 票货号
     */
    private String cargoInfoNo;
}
