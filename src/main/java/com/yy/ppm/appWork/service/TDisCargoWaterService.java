package com.yy.ppm.appWork.service;

import com.yy.ppm.appWork.bean.dto.TDisCargoWaterDTO;

import java.util.List;

public interface TDisCargoWaterService {

    List<TDisCargoWaterDTO> queryCargoAllApp(TDisCargoWaterDTO tDisCargoWaterDTO);

    void cargoWaterAppInsert(TDisCargoWaterDTO tDisCargoWaterDTO);

    List<TDisCargoWaterDTO> queryCargoWaterByIdApp(String trustId);

    TDisCargoWaterDTO queryById(Long id);

    void deleteApp(Long id);

}
