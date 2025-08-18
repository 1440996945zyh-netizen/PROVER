package com.yy.ppm.statement.bean.dto.storageFee;

import com.yy.ppm.common.bean.po.BasePO;
import lombok.Data;

import java.util.List;

@Data
public class TReqStorageStatementExportDTO extends BasePO  {
    private String routeId;
    private List<Long> ids;
}
