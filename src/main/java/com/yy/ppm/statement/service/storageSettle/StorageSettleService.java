package com.yy.ppm.statement.service.storageSettle;

import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.ppm.statement.bean.dto.ConfirmForMiscAndStorageDTO;
import com.yy.ppm.statement.bean.dto.CostBillDtoSheetTemplate;
import com.yy.ppm.statement.bean.dto.storageSettle.TBusHandoverlistDTO;
import com.yy.ppm.statement.bean.dto.storageSettle.TBusHandoverlistQueryDTO;
import com.yy.ppm.statement.bean.dto.storageSettle.TCostStorageSettleDTO;
import com.yy.ppm.statement.bean.dto.storageSettle.TCostStorageSettleDetailDTO;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Auther linqi
 * @Description
 * @Date 2023-11-24 9:12
 */
public interface StorageSettleService {

    Pages<TBusHandoverlistDTO> listHandoverlist(PageParameter parameter, TBusHandoverlistQueryDTO query);

    List<TCostStorageSettleDetailDTO> listDetail(Long handoverlistId);

    List<Map<String, Object>> listContract(Long handoverlistId, Date date);

    List<TCostStorageSettleDetailDTO> listDetailWithContract(Long handoverlistId, Long contractRateId, String isUseReduce);

    void settle(TCostStorageSettleDTO storageSettle);

    List<TCostStorageSettleDTO> listStorageSettle(Long handoverlistId);

    void cancelSettle(Long storageSettleId);

    void review(Long storageSettleId);

    void cancelReview(Long storageSettleId);

    void saveConfirmFile(ConfirmForMiscAndStorageDTO dto);

    void confirm(ConfirmForMiscAndStorageDTO dto);

    void cancelConfirm(ConfirmForMiscAndStorageDTO dto);

    List<TCostStorageSettleDTO> listStorageSettleForConfirm(Long handoverlistId);

    CostBillDtoSheetTemplate printFeeList(List<Long> ids);
}
