package com.yy.ppm.statement.service.storageCalculate;

import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.ppm.statement.bean.dto.storageCalculate.TBusCargoInfoDTO;
import com.yy.ppm.statement.bean.dto.storageCalculate.TBusCargoInfoQueryDTO;
import com.yy.ppm.statement.bean.dto.storageCalculate.TCostStorageSettleDTO;
import com.yy.ppm.statement.bean.dto.storageCalculate.TCostStorageSettleDetailDTO;
import com.yy.ppm.statement.bean.po.TBusStackFeeReducePO;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Auther linqi
 * @Description
 * @Date 2023-11-24 9:12
 */
public interface StorageCalculateService {

    Pages<TBusCargoInfoDTO> listCargoInfo(PageParameter parameter, TBusCargoInfoQueryDTO query);

    List<TCostStorageSettleDetailDTO> listDetail(Long cargoInfoId);

    List<Map<String, Object>> listContract(Long cargoInfoId, Date date);

    List<TCostStorageSettleDetailDTO> listDetailWithContract(Long cargoInfoId, Long contractRateId, String isUseReduce);

    List<TCostStorageSettleDTO> listStorageSettle(Long cargoInfoId);

    void reduce(TBusStackFeeReducePO stackFeeReduce);
}
