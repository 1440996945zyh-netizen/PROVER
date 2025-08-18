package com.yy.ppm.statement.service;

import com.yy.common.excel.export.bean.SheetMapping;
import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.ppm.business.bean.po.TBusRatePO;
import com.yy.ppm.statement.bean.dto.costShip.TCostShipExportDTO;
import com.yy.ppm.statement.bean.dto.costShip.TDisShipDynamicDTO;
import com.yy.ppm.statement.bean.dto.costShip.TDisShipvoyageItemDTO;
import com.yy.ppm.statement.bean.dto.costShip.TDisShipvoyageItemQueryDTO;
import com.yy.ppm.statement.bean.po.TCostShipPO;

import java.util.List;

/**
 * @Auther linqi
 * @Description
 * @Date 2023-09-20 11:24
 */
public interface TCostShipService {

    Pages<TDisShipvoyageItemDTO> listShipvoyageItem(TDisShipvoyageItemQueryDTO query, PageParameter parameter);

    List<TDisShipDynamicDTO> listStopRecord(Long shipvoyageId);

    /**
     * 计算非标准性停工记录
     *
     * @param shipvoyageId
     * @return
     */
    List<TDisShipDynamicDTO> listStopRecordNew(Long shipvoyageId);

    List<TBusRatePO> listRate(Long shipvoyageId);

    void statement(List<TCostShipPO> costShips);

    List<TCostShipPO> listCostShip(Long shipvoyageId);

    void cancelStatement(Long shipvoyageId);

    void review(Long shipvoyageId);

    void cancelReview(Long shipvoyageId);

    TCostShipExportDTO exportFee(Long shipvoyageId, Long shipvoyageItemId);

    List<TCostShipPO> listOtherCostShip(Long shipvoyageItemId);

    Integer getBerthDyas(Long shipvoyageItemId);

    List<TDisShipvoyageItemDTO> getSpecialDynamicList(TDisShipvoyageItemQueryDTO query, PageParameter parameter);
}
