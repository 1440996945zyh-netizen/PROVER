package com.yy.ppm.statement.service.storageSettleMix;

import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.ppm.statement.bean.dto.storageSettleMix.TBusCargoInfoDTO;
import com.yy.ppm.statement.bean.dto.storageSettleMix.TBusCargoInfoQueryDTO;
import com.yy.ppm.statement.bean.dto.storageSettleMix.TCostStorageSettleDTO;
import com.yy.ppm.statement.bean.dto.storageSettleMix.TCostStorageSettleDetailDTO;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface StorageSettleMixService {

    Pages<TBusCargoInfoDTO> listCargoInfo(PageParameter parameter, TBusCargoInfoQueryDTO query);

    List<TCostStorageSettleDetailDTO> listDetail(Long cargoInfoId);

    List<Map<String, Object>> listContract(Long cargoInfoId, Date date);

    List<TCostStorageSettleDetailDTO> listDetailWithContract(Long cargoInfoId, Long contractRateId, String isUseReduce);

    void settle(TCostStorageSettleDTO storageSettle);

    List<TCostStorageSettleDTO> listStorageSettle(Long cargoInfoId);

    void cancelSettle(Long storageSettleId);

    void review(Long storageSettleId);

    void cancelReview(Long storageSettleId);
}
