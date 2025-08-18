package com.yy.ppm.statement.mapper;

import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.business.bean.dto.TBusContractDTO;
import com.yy.ppm.business.bean.dto.TBusTrustDTO;
import com.yy.ppm.business.bean.po.TBusServicePO;
import com.yy.ppm.statement.bean.dto.prodCostStatement.TBusHandoverlistDTO;
import com.yy.ppm.statement.bean.dto.prodCostStatement.TBusHandoverlistQueryDTO;
import com.yy.ppm.statement.bean.dto.prodCostStatement.TCostStatementDTO;
import com.yy.ppm.statement.bean.po.TCostStatementDetailPO;
import com.yy.ppm.statement.bean.po.TCostStatementPO;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @Auther linqi
 * @Description
 * @Date 2023-09-11 17:33
 */
public interface TProdCostStatementMapper {

    Page<TBusHandoverlistDTO> listBusHandoverlist(TBusHandoverlistQueryDTO query);

    List<TCostStatementDTO> listCostStatement(@Param("handoverlistId")Long handoverlistId,@Param("isFinal") Long isFinal);

    List<Map<String, BigDecimal>> listPreSettlement(Long handoverlistId);

    List<TBusServicePO> listService(@Param("impExp") String impExp, @Param("type") String type);

    TBusHandoverlistDTO getHandoverlist(Long handoverlistId);
    TBusHandoverlistDTO getHandoverlistById(Long handoverlistId);

    int updateHandoverlist(TBusHandoverlistDTO handoverlist);

    List<TCostStatementDetailPO> listCostStatementDetailByHandoverlistId(Long handoverlistId);

    @Edit
    int insertCostStatement(TCostStatementPO costStatement);

    @Edit
    int insertCostStatementDetail(@Param("details") List<TCostStatementDetailPO> details);

    int deleteCostStatement(Long id);

    int deleteCostStatementDetail(Long id);

    /**
     * 获取进口交接清单直取量
     * @param handoverlistId
     * @return
     */
    TBusHandoverlistDTO getImpZQQuantity(@Param("handoverlistId") Long handoverlistId,@Param("zqTallyStatistic") String type);

    /**
     * 过磅量查询
     * @param handoverlistId
     * @return
     */
    TBusHandoverlistDTO getWeighCapacity(@Param("handoverlistId") Long handoverlistId);

    /**
     * 获取出口交接清单直取量
     * @param handoverlistId
     * @return
     */
    TBusHandoverlistDTO getExpZQQuantity(@Param("handoverlistId") Long handoverlistId,@Param("zqTallyStatistic") String type);

    TCostStatementDTO getCostStatement(Long id);

    List<TCostStatementDetailPO> listCostStatementDetail(Long id);

    List<TBusContractDTO> getContractList(@Param("id") Long handoverlistId);

    TBusTrustDTO getSettlementBasisByHandover(@Param("handoverlistId") Long handoverlistId);

    BigDecimal getBondedAreaTon(@Param("cargoInfoId") Long cargoInfoId);

    List<String> getShipAllBerthName(Long shipvoyageItemId);

    BigDecimal getJGWeightByHandoverlistId(Long id);
    BigDecimal getSGWeightByHandoverlistId(Long id);
}
