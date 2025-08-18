package com.yy.ppm.statement.service.impl;

import cn.hutool.core.lang.Snowflake;
import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.common.util.PageHelperUtils;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.business.bean.po.TBusRatePO;
import com.yy.ppm.common.enums.AutoNumEnum;
import com.yy.ppm.common.enums.HandoverlistTypeEnum;
import com.yy.ppm.common.enums.IsFinalEnum;
import com.yy.ppm.common.enums.StatementStatusEnum;
import com.yy.ppm.common.service.CommonService;
import com.yy.ppm.statement.bean.dto.costShipWaterElectricity.TBusTrustDTO;
import com.yy.ppm.statement.bean.dto.costShipWaterElectricity.TBusTrustQueryDTO;
import com.yy.ppm.statement.bean.dto.costShipWaterElectricity.TPrdWaterElectricityDTO;
import com.yy.ppm.statement.bean.po.TCostShipPO;
import com.yy.ppm.statement.bean.po.TCostStatementDetailPO;
import com.yy.ppm.statement.bean.po.TCostStatementPO;
import com.yy.ppm.statement.mapper.TCostShipWaterElectricityMapper;
import com.yy.ppm.statement.service.TCostShipWaterElectricityService;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Auther linqi
 * @Description
 * @Date 2023-09-23 11:14
 */
@Service
public class TCostShipWaterElectricityServiceImpl implements TCostShipWaterElectricityService {

    @Autowired
    private TCostShipWaterElectricityMapper tCostShipWaterElectricityMapper;

    @Autowired
    private Snowflake snowflake;

    /**
     * 审核状态-未审核
     */
    private static final String STATUS_10 = "10";

    /**
     * 审核状态-已审核
     */
    private static final String STATUS_20 = "20";

    @Autowired
    private CommonService commonService;

    @Override
    public Pages<TBusTrustDTO> listTrust(TBusTrustQueryDTO query, PageParameter parameter) {
        return PageHelperUtils.limit(parameter, () -> {
            return tCostShipWaterElectricityMapper.listTrust(query);
        });
    }

    @Override
    public List<TPrdWaterElectricityDTO> listWaterElectricity(Long trustId) {
        return tCostShipWaterElectricityMapper.listWaterElectricity(trustId);
    }

    @Override
    public List<TBusRatePO> listRate() {
        return tCostShipWaterElectricityMapper.listRate();
    }

    @Override
    public void statement(List<TCostShipPO> costShips) {
        List<TCostShipPO> tempCostShips = tCostShipWaterElectricityMapper.listCostShip(costShips.get(0).getTrustId());
        if (!tempCostShips.isEmpty()) {
            throw new BusinessRuntimeException("已结算过，无法再次结算");
        }

        costShips.forEach(v1 -> {
            v1.setId(snowflake.nextId());
            v1.setStatus(STATUS_10);
        });
        tCostShipWaterElectricityMapper.insertCostShip(costShips);
    }

    @Override
    public List<TCostShipPO> listCostShip(Long trustId) {
        return tCostShipWaterElectricityMapper.listCostShip(trustId);
    }

    @Override
    public void cancelStatement(Long trustId) {
        List<TCostShipPO> costShips = tCostShipWaterElectricityMapper.listCostShip(trustId);
        boolean anyMatch = costShips.stream().anyMatch(v1 -> STATUS_20.equals(v1.getStatus()));
        if (anyMatch) {
            throw new BusinessRuntimeException("已审核无法撤销结算，请先销审");
        }

        tCostShipWaterElectricityMapper.deleteCostShip(trustId);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public void review(Long trustId) {
        List<TCostShipPO> costShips = tCostShipWaterElectricityMapper.listCostShip(trustId);
        if (costShips.isEmpty()) {
            throw new BusinessRuntimeException("未结算无法审核");
        }

        boolean anyMatch = costShips.stream().anyMatch(v1 -> STATUS_20.equals(v1.getStatus()));
        if (anyMatch) {
            throw new BusinessRuntimeException("已审核无法再次审核");
        }

        TCostShipPO costShip = new TCostShipPO();
        costShip.setTrustId(trustId);
        costShip.setStatus(STATUS_20);
        tCostShipWaterElectricityMapper.updateCostShip(costShip);

        TCostStatementPO costStatement = new TCostStatementPO();
        costStatement.setId(snowflake.nextId());
        costStatement.setCompanyId(costShips.get(0).getCompanyId());
        costStatement.setCompanyName(costShips.get(0).getCompanyName());
        costStatement.setStatementNo(commonService.getAutoNum(AutoNumEnum.BusinessAutoEnum.STATEMENT_NO, null));
        costStatement.setCustomerId(costShips.get(0).getCustomerId());
        costStatement.setCustomerName(costShips.get(0).getCustomerName());
        costStatement.setType(HandoverlistTypeEnum._40.getCode());
        costStatement.setTrustId(costShips.get(0).getTrustId());
        costStatement.setSettlementDate(new DateTime().withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0).toDate());
        costStatement.setStatus(StatementStatusEnum._31.getCode());
        costStatement.setIsFinal(IsFinalEnum.TRUE.getCode());
        costStatement.setShipvoyageId(costShips.get(0).getShipvoyageId());
        costStatement.setShipvoyageItemId(costShips.get(0).getShipvoyageItemId());

        List<TCostStatementDetailPO> costStatementDetails = costShips.stream().map(v1 -> {
            TCostStatementDetailPO costStatementDetail = new TCostStatementDetailPO();
            costStatementDetail.setId(snowflake.nextId());
            costStatementDetail.setStatement(costStatement.getId());
            costStatementDetail.setRateItemCode(v1.getRateItemCode());
            costStatementDetail.setRateItemName(v1.getRateItemName());
            costStatementDetail.setRate(v1.getRate());
            costStatementDetail.setUnitCode(v1.getUnitCode());
            costStatementDetail.setUnitName(v1.getUnitName());
            costStatementDetail.setNumber(v1.getNumber());
            costStatementDetail.setAmount(v1.getAmount());
            costStatementDetail.setTax(v1.getTaxRate());
            costStatementDetail.setTaxAmount(v1.getTaxAmount());
            costStatementDetail.setRemark(v1.getRemark());
            costStatementDetail.setBusinessId(v1.getId());
            costStatementDetail.setRateId(v1.getRateId());
            return costStatementDetail;
        }).collect(Collectors.toList());

        tCostShipWaterElectricityMapper.insertCostStatement(costStatement);
        tCostShipWaterElectricityMapper.insertCostStatementDetail(costStatementDetails);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public void cancelReview(Long trustId) {
        List<TCostShipPO> costShips = tCostShipWaterElectricityMapper.listCostShip(trustId);
        if (costShips.isEmpty()) {
            throw new BusinessRuntimeException("当前指令未结算");
        }
        TCostStatementPO costStatement = tCostShipWaterElectricityMapper.getCostStatement(costShips.get(0).getId());
        if (costStatement == null) {
            throw new BusinessRuntimeException("未审核无法销审");
        }
        if (!StatementStatusEnum._31.getCode().equals(costStatement.getStatus())) {
            throw new BusinessRuntimeException("水电结算单已不是审核状态");
        }

        TCostShipPO costShip = new TCostShipPO();
        costShip.setTrustId(trustId);
        costShip.setStatus(STATUS_10);
        tCostShipWaterElectricityMapper.updateCostShip(costShip);

        tCostShipWaterElectricityMapper.deleteCostStatement(costStatement.getId());
        tCostShipWaterElectricityMapper.deleteCostStatementDetail(costStatement.getId());
    }
}
