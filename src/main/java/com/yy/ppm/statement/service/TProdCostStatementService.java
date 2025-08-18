package com.yy.ppm.statement.service;

import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.ppm.business.bean.dto.TBusContractDTO;
import com.yy.ppm.business.bean.dto.TBusTrustDTO;
import com.yy.ppm.business.bean.po.TBusServicePO;
import com.yy.ppm.statement.bean.dto.prodCostStatement.TBusHandoverlistDTO;
import com.yy.ppm.statement.bean.dto.prodCostStatement.TBusHandoverlistQueryDTO;
import com.yy.ppm.statement.bean.dto.prodCostStatement.TCostStatementDTO;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @Auther linqi
 * @Description
 * @Date 2023-09-11 17:32
 */
public interface TProdCostStatementService {

    Pages<TBusHandoverlistDTO> listBusHandoverlist(TBusHandoverlistQueryDTO query, PageParameter parameter);

    List<TCostStatementDTO> listCostStatement(Long busHandoverlistId,Long type);

    List<Map<String, BigDecimal>> listPreSettlement(Long handoverlistId);

    List<TBusServicePO> listService(String impExp, String type);

    void statement(TCostStatementDTO dto);

    void cancelStatement(Long id);

    TCostStatementDTO getCostStatement(Long id,Long bhId);

    List<TBusContractDTO> getSettlementBasis(Long handoverlistId);

    TBusTrustDTO getSettlementBasisByHandover(Long handoverlistId);

    BigDecimal getZQQuantity(Long handoverlistId,String type);

    TBusHandoverlistDTO getWeighCapacity(Long handoverlistId);

    BigDecimal getBondedAreaTon(Long cargoInfoId);

    TCostStatementDTO getJSGWeightByHandoverlistId(Long id);
}
