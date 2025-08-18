package com.yy.ppm.statement.mapper;

import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.business.bean.po.TBusRatePO;
import com.yy.ppm.statement.bean.dto.costShipWaterElectricity.TBusTrustDTO;
import com.yy.ppm.statement.bean.dto.costShipWaterElectricity.TBusTrustQueryDTO;
import com.yy.ppm.statement.bean.dto.costShipWaterElectricity.TPrdWaterElectricityDTO;
import com.yy.ppm.statement.bean.po.TCostShipPO;
import com.yy.ppm.statement.bean.po.TCostStatementDetailPO;
import com.yy.ppm.statement.bean.po.TCostStatementPO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Auther linqi
 * @Description
 * @Date 2023-09-23 11:15
 */
@Component
public interface TCostShipWaterElectricityMapper {

    Page<TBusTrustDTO> listTrust(TBusTrustQueryDTO query);

    List<TPrdWaterElectricityDTO> listWaterElectricity(Long trustId);

    List<TBusRatePO> listRate();

    List<TCostShipPO> listCostShip(Long trustId);

    @Edit
    int insertCostShip(@Param("costShips") List<TCostShipPO> costShips);

    int deleteCostShip(Long trustId);

    @Edit
    int insertCostStatement(TCostStatementPO costStatement);

    @Edit
    int insertCostStatementDetail(@Param("details") List<TCostStatementDetailPO> details);

    int updateCostShip(TCostShipPO costShip);

    TCostStatementPO getCostStatement(Long businessId);

    int deleteCostStatement(Long id);

    int deleteCostStatementDetail(Long statementId);
}
