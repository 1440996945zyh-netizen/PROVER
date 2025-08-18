package com.yy.ppm.business.bean.dto;

import com.yy.common.page.PageParameter;
import com.yy.ppm.common.bean.po.BasePO;
import lombok.Data;

@Data
public class TrustStopLogRes extends BasePO {
    private Long trustId;
    private Long cargoInfoId;
    private Long trustCargoId;
    private String remark;
    private Long id;
    private String stopRemark;
}
