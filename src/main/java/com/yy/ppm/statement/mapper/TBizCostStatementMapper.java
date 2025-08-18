package com.yy.ppm.statement.mapper;

import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.business.bean.dto.TBusCargoInfoDTO;
import com.yy.ppm.statement.bean.dto.TMiscBillingDTO;
import com.yy.ppm.statement.bean.dto.bizCostStatement.*;
import com.yy.ppm.statement.bean.po.TCostStatementPO;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Auther linqi
 * @Description
 * @Date 2023-09-18 10:38
 */
public interface TBizCostStatementMapper {

    Page<TCostStatementDTO> listCostStatement(TCostStatementQueryDTO query);

    List<Map<String, Object>> listInTime(@Param("ids") List<Long> ids);

    //界面查询结算单详情用
    List<TCostStatementDetailDTO> listCostStatementDetailList(Long statementId);
    List<TCostStatementDetailDTO> getStatementDetailByCargoInfoId(Long statementId);
    //撤销计费用
    List<TCostStatementDetailDTO> listCostStatementDetail(Long statementId);

    List<TBusContractDTO> listContract(@Param("statementId") Long statementId, @Param("date") Date date);
    List<TBusContractDTO> listContractByStatementIds(@Param("statementIds") List<Long> statementIds);

    BigDecimal getAccNumber(Long trateItemId);

    int updateCostStatement(TCostStatementPO costStatement);

    int rejectCostStatement(TCostStatementPO costStatement);

    int statement(TCostStatementDetailDTO detail);

    @Edit
    int insertCostStatementDetail(TCostStatementDetailDTO detail);

    int cancelStatement(@Param("statementDetailId") Long statementDetailId, @Param("number") BigDecimal number);

    int deleteCostStatementDetail(@Param("derivedDetailIds") List<Long> derivedDetailIds);

    TCostStatementPO getCostStatement(Long statementId);

    TCostStatementDetailDTO getCostStatementDetailByStatementID(@Param("statementId") Long statementId);

    List<TBusContractDTO> listContractForLULS(@Param("statementId") Long statementId,@Param("statementIds") List<Long> statementIds, @Param("date") Date date);
    List<TBusContractDTO> getContractListByIds(@Param("ids") List<Long> ids);

    List<TMiscBillingDTO> getMiscFee(Long statementId);

    List<TCostStatementDetailDTO> getcostStatementListByStatementIds(@Param("list") List<Long> statementIds);

    List<String> getShipAllBerthName(Long shipvoyageItemId);

    TBusCargoInfoDTO getCargoInfoByNo(@Param("cargoInfoNo") String cargoInfoNo);

    List<String> getInTimeForHZandLJLS(String cargoInfoNo);

    Page<TCostStatementExportDTO> listCostStatementExport(TCostStatementQueryDTO query);
}
