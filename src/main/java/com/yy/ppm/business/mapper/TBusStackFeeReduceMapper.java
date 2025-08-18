package com.yy.ppm.business.mapper;

import com.yy.framework.annotation.Edit;
import com.yy.ppm.business.bean.dto.TBusStackFeeReduceDTO;
import com.yy.ppm.statement.bean.dto.storageFee.TCostStorageSettleDTO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface TBusStackFeeReduceMapper {
    @Edit
    void insert(TBusStackFeeReduceDTO dto);
    @Edit
    void update(TBusStackFeeReduceDTO dto);

    List<TBusStackFeeReduceDTO> getList(TBusStackFeeReduceDTO dto);

    List<TCostStorageSettleDTO> getSettleList(@Param("cargoInfoId") Long cargoInfoId);
}
