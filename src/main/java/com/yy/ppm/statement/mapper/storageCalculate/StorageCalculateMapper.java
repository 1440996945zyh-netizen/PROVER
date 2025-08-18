package com.yy.ppm.statement.mapper.storageCalculate;

import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.business.bean.po.TBusCargoInfoPO;
import com.yy.ppm.business.bean.po.TBusContractRatePO;
import com.yy.ppm.statement.bean.dto.storageCalculate.TBusCargoInfoDTO;
import com.yy.ppm.statement.bean.dto.storageCalculate.TBusCargoInfoQueryDTO;
import com.yy.ppm.statement.bean.dto.storageCalculate.TCostStorageSettleDTO;
import com.yy.ppm.statement.bean.po.TBusStackFeeReducePO;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Auther linqi
 * @Description
 * @Date 2023-11-24 9:13
 */
public interface StorageCalculateMapper {

    Page<TBusCargoInfoDTO> listCargoInfo(TBusCargoInfoQueryDTO query);

    TBusCargoInfoPO getCargoInfo(Long id);

    List<TBusCargoInfoPO> listTransferOutCargoInfo(Long parentId);

    List<Map<String, Object>> listMixOutWeight(Long cargoInfoId);

    List<Map<String, Object>> listContract(@Param("cargoInfoId") Long cargoInfoId, @Param("date") Date date);

    List<TCostStorageSettleDTO> listStorageSettle(Long cargoInfoId);

    TBusContractRatePO getContractRate(Long contractRateId);

    TBusStackFeeReducePO getStackFeeReduce(Long cargoInfoId);

    @Edit
    int insertStackFeeReduce(TBusStackFeeReducePO stackFeeReduce);

    int deleteStackFeeReduce(Long cargoInfoId);

    @Edit
    int updateStackFeeReduce(TBusStackFeeReducePO stackFeeReduce);
}
