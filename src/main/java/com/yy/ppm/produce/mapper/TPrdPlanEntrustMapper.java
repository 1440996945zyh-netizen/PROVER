package com.yy.ppm.produce.mapper;

import com.github.pagehelper.Page;
import com.yy.ppm.produce.bean.dto.*;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TPrdPlanEntrustMapper {

    Page<TPrdPlanEntrustResultDTO>  getPage(TPrdPlanEntrustSearchDTO searchDTO);

    List<TPrdPlanEntrustVehicleDTO> getVehicleNumByPlanNo(@Param("list") List<String> planNoList);

    List<TPrdVehiclePoundDTO> getVehiclePound(@Param("list") List<String> planNoList);

    Page<TPrdVehicleReservationDTO> getVehicleList(TPrdVehicleReservationSearchDTO searchDTO);

}
