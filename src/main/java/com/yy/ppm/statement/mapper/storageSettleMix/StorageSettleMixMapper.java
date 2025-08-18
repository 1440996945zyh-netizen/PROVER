package com.yy.ppm.statement.mapper.storageSettleMix;

import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.business.bean.po.TBusCargoInfoPO;
import com.yy.ppm.business.bean.po.TBusContractRatePO;
import com.yy.ppm.statement.bean.dto.storageSettleMix.TBusCargoInfoDTO;
import com.yy.ppm.statement.bean.dto.storageSettleMix.TBusCargoInfoQueryDTO;
import com.yy.ppm.statement.bean.dto.storageSettleMix.TCostStorageSettleDTO;
import com.yy.ppm.statement.bean.dto.storageSettleMix.TCostStorageSettleDetailDTO;
import com.yy.ppm.statement.bean.po.*;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface StorageSettleMixMapper {

    Page<TBusCargoInfoDTO> listCargoInfo(TBusCargoInfoQueryDTO query);

    TBusCargoInfoPO getCargoInfo(Long id);

    TBusCargoInfoDTO getCargoInfo2(Long id);

    List<TBusCargoInfoPO> listTransferOutCargoInfo(Long parentId);

    List<Map<String, Object>> listMixOutWeight(Long cargoInfoId);

    List<Map<String, Object>> listContract(@Param("cargoInfoId") Long cargoInfoId, @Param("date") Date date);

    List<TCostStorageSettleDetailDTO> listStorageSettleDetail(@Param("cargoInfoId") Long cargoInfoId, @Param("startDate") Date startDate, @Param("endDate") Date endDate);

    @Edit
    int insertStorageSettle(TCostStorageSettleDTO storageSettle);

    @Edit
    int insertStorageSettleDetail(@Param("details") List<TCostStorageSettleDetailDTO> details);

    List<TCostStorageSettleDTO> listStorageSettle(Long cargoInfoId);

    TBusContractRatePO getContractRate(Long contractRateId);

    TCostStorageSettleDTO getStorageSettle(Long storageSettleId);

    int deleteStorageSettle(Long id);

    int deleteStorageSettleDetail(Long storageSettleId);

    @Edit
    int review(TCostStorageSettlePO storageSettle);

    TBusHandoverlistPO getHandoverlist(Long id);

    @Edit
    int insertStatement(TCostStatementPO statement);

    @Edit
    int insertStatementDetail(TCostStatementDetailPO detail);

    TCostStatementPO getStatement(Long storageSettleId);

    int cancelReview(TCostStorageSettlePO storageSettle);

    int deleteCostStatement(Long id);

    int deleteCostStatementDetail(Long statementId);

    TBusStackFeeReducePO getStackFeeReduce(Long cargoInfoId);
}
