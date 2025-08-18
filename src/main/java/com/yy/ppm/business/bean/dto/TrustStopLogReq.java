package com.yy.ppm.business.bean.dto;

import com.yy.common.page.PageParameter;
import lombok.Data;

@Data
public class TrustStopLogReq extends PageParameter {
    private Long trustId;
    private Long cargoInfoId;
    private Long trustCargoId;
    private Long id;
}
