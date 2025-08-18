package com.yy.ppm.statement.bean.dto.storageFee;

import com.yy.common.excel.export.bean.SheetMapping;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 导出堆存费结算详情专用
 */
@Data
public class TStorageCostDetailExportDTO extends SheetMapping {
    private String cargoOwnerName;
    private String cargoName;
    private String shipNameVoyage;
    private String freeStorageDays;
    private String endWorkTime;
    private String berthTime;
    List<TStorageCostDetailInfoExportDTO> details;
    private String amount;
}
