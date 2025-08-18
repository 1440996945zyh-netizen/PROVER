package com.yy.ppm.business.service;

import com.yy.ppm.business.bean.dto.TBusStackFeeReduceDTO;
import com.yy.ppm.statement.bean.dto.storageFee.TCostStorageSettleDTO;

import java.util.List;

public interface TBusStackFeeReduceService {
    void add(TBusStackFeeReduceDTO dto);

    List<TBusStackFeeReduceDTO> getList(TBusStackFeeReduceDTO dto);

    List<TCostStorageSettleDTO> getSettleList(Long cargoInfoId);
}
