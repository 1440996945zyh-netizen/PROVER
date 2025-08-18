package com.yy.ppm.appWork.mapper;

import com.yy.framework.annotation.Edit;
import com.yy.ppm.appWork.bean.dto.TDisCargoWaterDTO;
import com.yy.ppm.appWork.bean.dto.TDisWaterDTO;

import java.util.List;

public interface TDisCargoWaterMapper {

    List<TDisCargoWaterDTO> queryCargoAllApp(TDisCargoWaterDTO tDisCargoWaterDTO);

    @Edit
    int cargoWaterAppInsert(TDisCargoWaterDTO tDisCargoWaterDTO);

    TDisCargoWaterDTO count(String trustId);

    List<TDisCargoWaterDTO> queryCargoWaterByIdApp(String trustId);

    @Edit
    void updateTrustStatus(TDisCargoWaterDTO tDisCargoWaterDTO);

    @Edit
    int cargoWaterAppUpdate(TDisCargoWaterDTO tDisCargoWaterDTO);

    TDisCargoWaterDTO queryById(Long id);

    int deleteApp(Long id);
}
