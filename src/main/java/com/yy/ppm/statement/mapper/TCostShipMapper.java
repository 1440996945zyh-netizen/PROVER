package com.yy.ppm.statement.mapper;

import com.github.pagehelper.Page;
import com.yy.common.page.Pages;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.business.bean.po.TBusRatePO;
import com.yy.ppm.dispatch.bean.dto.disShipvoyage.TDisShipvoyageDTO;
import com.yy.ppm.dispatch.bean.po.TDisShipvoyageItemPO;
import com.yy.ppm.dispatch.bean.po.TDisShipvoyagePO;
import com.yy.ppm.statement.bean.dto.costShip.*;
import com.yy.ppm.statement.bean.po.TCostShipPO;
import com.yy.ppm.statement.bean.po.TCostStatementDetailPO;
import com.yy.ppm.statement.bean.po.TCostStatementPO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Auther linqi
 * @Description
 * @Date 2023-09-20 11:24
 */
public interface TCostShipMapper {

    Page<TDisShipvoyageItemDTO> listShipvoyageItem(TDisShipvoyageItemQueryDTO query);

    TDisShipvoyagePO getShipvoyage(Long id);

    List<TDisShipvoyageItemPO> listShipvoyageItemByShipvoyageId(Long shipvoyageId);

    List<TDisShipDynamicDTO> listStopRecord(Long shipvoyageId);

    List<TBusRatePO> listRate(Long shipvoyageId);

    List<TCostShipPO> listCostShip(Long shipvoyageItemId);

    List<TCostShipPO> listCostShipByVoyage(@Param("shipvoyageItemId") Long shipvoyageItemId);

    @Edit
    int insertCostShip(@Param("costShips") List<TCostShipPO> costShips);

    int deleteCostShip(Long shipvoyageItemId);

    @Edit
    int insertCostStatement(TCostStatementPO costStatement);

    @Edit
    int insertCostStatementDetail(@Param("details") List<TCostStatementDetailPO> details);

    int updateCostShip(TCostShipPO costShip);

    TCostStatementPO getCostStatement(Long shipvoyageItemId);

    int deleteCostStatement(Long id);

    int deleteCostStatementDetail(Long statementId);

    String getShipKindName(@Param("shipvoyageId") Long shipvoyageId);

    List<TCostShipStatusDTO> getCostShipStatus(Long shipvoyageItemId);
    TCostShipStatusDTO getCostStatementStatus(Long shipvoyageItemId);

    List<TCostShipPO> listOtherCostShip(Long shipvoyageItemId);

    List<TCostShipPO> getMiscByShipItemId(Long shipvoyageItemId);

    List<TDisShipvoyageItemDTO> getShipVoyageDynamicList(TDisShipvoyageItemDTO v1);

    TDisShipvoyagePO getShipvoyageByItemId(Long id);

    List<TDisShipvoyageItemDTO> getDynamicListByShipVoyageId(Long shipvoyageId);

    List<TDisShipvoyageItemDTO> getSpecialDynamicByShipvoyageItemId(@Param("shipvoyageItemId") Long shipvoyageItemId);

    List<CostShipDetailExportDTO> getSpecialCostShipListByShip(@Param("shipvoyageId") Long shipvoyageId,@Param("shipvoyageItemId") Long shipvoyageItemId);

    List<TDisShipvoyageItemDTO> getLastPort(TDisShipvoyageItemPO shipvoyageItem);

}
