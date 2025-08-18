package com.yy.ppm.appWork.service;

import com.yy.ppm.appWork.bean.dto.TDisWaterDTO;

import java.util.List;
import java.util.Map;

public interface TDisWaterElectricityService {

    List<TDisWaterDTO> queryAllApp(TDisWaterDTO tDisWaterElectricityDTO);

    List<TDisWaterDTO> queryIdApp(String trustId);

    void AppInsert(TDisWaterDTO tDisWaterElectricityDTO);

    void deleteApp(Long id);

    TDisWaterDTO queryById(Long id);

    List<Map<String, Object>> getUserList();
}
