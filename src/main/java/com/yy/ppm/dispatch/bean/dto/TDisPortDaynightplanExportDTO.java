package com.yy.ppm.dispatch.bean.dto;

import com.yy.common.excel.export.bean.SheetMapping;
import com.yy.ppm.statement.bean.dto.costShip.CostShipDetailExportDTO;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class TDisPortDaynightplanExportDTO extends SheetMapping {
    //东作业区
    List<TDisPortDaynightplanEastExportDTO> detailList1;
    //中作业区
    List<TDisPortDaynightplanEastExportDTO> detailList2;
    //西作业区
    List<TDisPortDaynightplanEastExportDTO> detailList3;

    //东作业区汇总
    private BigDecimal eastAllTon;

    //中作业区汇总
    private BigDecimal midAllTon;

    //西作业区汇总
    private BigDecimal westAllTon;

    //散货夜班合计
    private BigDecimal bulkAllTon;

}
