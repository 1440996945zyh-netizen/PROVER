package com.yy.ppm.appWork.mapper;

import com.yy.framework.annotation.Edit;
import com.yy.ppm.appWork.bean.dto.TDisWaterDTO;

import java.util.List;
import java.util.Map;

public interface TDisWaterElectricityMapper {
    List<TDisWaterDTO> queryAllApp(TDisWaterDTO tDisWaterElectricityDao);
    List<TDisWaterDTO> queryIdApp(String trustId);
    TDisWaterDTO queryById(Long id);

    @Edit
    int AppInsert(TDisWaterDTO tDisWaterElectricityDao);

    @Edit
    int AppUpdate(TDisWaterDTO tDisWaterElectricityDao);
    int deleteApp(Long id);
    TDisWaterDTO count(String trustId);

    @Edit
    int updateTrustStutas(TDisWaterDTO tDisWaterElectricityDTO);

    List<Map<String, Object>> getUserList();

    List<TDisWaterDTO> getByVoyageId(Long shipvoyageId);
}
