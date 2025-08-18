package com.yy.ppm.finance.bean.dto;

import com.yy.ppm.common.bean.po.BasePO;
import lombok.Data;

@Data
public class StatementStatusUpdateDTO extends BasePO {
    private Long status;
    private Long id;
}
