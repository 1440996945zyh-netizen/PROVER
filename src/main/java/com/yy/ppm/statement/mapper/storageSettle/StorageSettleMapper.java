package com.yy.ppm.statement.mapper.storageSettle;

import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.business.bean.po.TBusCargoInfoPO;
import com.yy.ppm.business.bean.po.TBusContractRatePO;
import com.yy.ppm.statement.bean.dto.bizCostStatement.TCostStatementDetailDTO;
import com.yy.ppm.statement.bean.dto.storageSettle.TBusHandoverlistDTO;
import com.yy.ppm.statement.bean.dto.storageSettle.TBusHandoverlistQueryDTO;
import com.yy.ppm.statement.bean.dto.storageSettle.TCostStorageSettleDTO;
import com.yy.ppm.statement.bean.dto.storageSettle.TCostStorageSettleDetailDTO;
import com.yy.ppm.statement.bean.po.*;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Auther linqi
 * @Description
 * @Date 2023-11-24 9:13
 */
public interface StorageSettleMapper {

    Page<TBusHandoverlistDTO> listHandoverlist(TBusHandoverlistQueryDTO query);

    TBusCargoInfoPO getCargoInfo(Long handoverlistId);

    List<TBusCargoInfoPO> listTransferOutCargoInfo(Long parentId);

    List<Map<String, Object>> listMixOutWeight(Long cargoInfoId);

    TBusHandoverlistPO getHandoverlist(Long id);

    List<Map<String, Object>> listContract(@Param("handoverlistId") Long handoverlistId, @Param("date") Date date);

    List<TCostStorageSettleDetailDTO> listStorageSettleDetail(@Param("cargoInfoId") Long cargoInfoId, @Param("startDate") Date startDate, @Param("endDate") Date endDate);

    @Edit
    int insertStorageSettle(TCostStorageSettleDTO storageSettle);

    @Edit
    int insertStorageSettleDetail(@Param("details") List<TCostStorageSettleDetailDTO> details);

    List<TCostStorageSettleDTO> listStorageSettle(Long handoverlistId);

    int deleteStorageSettle(Long id);

    int deleteStorageSettleDetail(Long storageSettleId);

    TBusContractRatePO getContractRate(Long contractRateId);

    TCostStorageSettleDTO getStorageSettle(Long storageSettleId);

    @Edit
    int review(TCostStorageSettlePO storageSettle);

    @Edit
    int insertStatement(TCostStatementPO statement);

    @Edit
    int insertStatementDetail(TCostStatementDetailPO detail);

    TCostStatementPO getStatement(Long storageSettleId);

    int cancelReview(TCostStorageSettlePO storageSettle);

    int deleteCostStatement(Long id);

    int deleteCostStatementDetail(Long statementId);

    int updateCostStatement(TCostStatementPO costStatement);

    TCostStatementPO getStatementById(@Param("id") Long tmpId);

    List<TCostStorageSettleDTO> listStorageSettleForConfirm(Long handoverlistId);

    List<TBusHandoverlistDTO> getHandoverListByIds(@Param("list") List<Long> ids);

    List<TCostStatementDetailDTO> getStorageSettleStatementList(@Param("list") List<Long> ids);

    List<TCostStatementDetailDTO> getBGFeeListStatement(@Param("list") List<Long> ids);

    List<TCostStatementDetailDTO> getMISCStatementList(@Param("list") List<Long> cargoInfoIdList);

    TBusStackFeeReducePO getStackFeeReduce(Long cargoInfoId);
}
