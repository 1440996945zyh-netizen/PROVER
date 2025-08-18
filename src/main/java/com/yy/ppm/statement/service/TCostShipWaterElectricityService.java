package com.yy.ppm.statement.service;

import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.ppm.business.bean.po.TBusRatePO;
import com.yy.ppm.statement.bean.dto.costShipWaterElectricity.TBusTrustDTO;
import com.yy.ppm.statement.bean.dto.costShipWaterElectricity.TBusTrustQueryDTO;
import com.yy.ppm.statement.bean.dto.costShipWaterElectricity.TPrdWaterElectricityDTO;
import com.yy.ppm.statement.bean.po.TCostShipPO;

import java.util.List;

/**
 * @Auther linqi
 * @Description
 * @Date 2023-09-23 11:14
 */
public interface TCostShipWaterElectricityService {

    Pages<TBusTrustDTO> listTrust(TBusTrustQueryDTO query, PageParameter parameter);

    List<TPrdWaterElectricityDTO> listWaterElectricity(Long trustId);

    List<TBusRatePO> listRate();

    void statement(List<TCostShipPO> costShips);

    List<TCostShipPO> listCostShip(Long trustId);

    void cancelStatement(Long trustId);

    void review(Long trustId);

    void cancelReview(Long trustId);
}
