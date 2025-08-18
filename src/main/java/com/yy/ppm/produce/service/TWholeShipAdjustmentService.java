package com.yy.ppm.produce.service;

import com.yy.common.page.Pages;
import com.yy.ppm.produce.bean.dto.TDisShipVoyageDTO;
import com.yy.ppm.produce.bean.dto.TWholeShipAdjustmenRes;
import com.yy.ppm.produce.bean.dto.TWholeShipAdjustmentQueryDTO;
import com.yy.ppm.produce.bean.dto.workTicket.TPrdWorkTicketDetailDTO;
import com.yy.ppm.produce.bean.po.TWholeShipAdjustmentExaminePO;

import java.util.List;
import java.util.Map;

public interface TWholeShipAdjustmentService {
    Pages<TDisShipVoyageDTO> getList(TWholeShipAdjustmentQueryDTO queryDTO);

    TWholeShipAdjustmenRes getTicketListForChange(TWholeShipAdjustmentQueryDTO queryDTO);
    TWholeShipAdjustmenRes getTicketListForChange1128(TWholeShipAdjustmentQueryDTO queryDTO);

    void updateTickeyDetail(Long shipvoyageItemId,List<TPrdWorkTicketDetailDTO> reqList);

    void updateShipAdjustStatus(Long shipvoyageItemId, String allotType);

    Map<String, Object> getShipPLanTicketStatus(TWholeShipAdjustmentQueryDTO queryDTO);

    void personClearConfirm(TWholeShipAdjustmentExaminePO po);

    void macClearConfirm(TWholeShipAdjustmentExaminePO po);

    void updateShipAdjustClearPersonStatus(Long shipvoyageItemId);

    void updateShipAdjustClearMacStatus(Long shipvoyageItemId);

    void updateTickeyDetail1128(Long shipvoyageItemId, List<TPrdWorkTicketDetailDTO> reqList);
}
