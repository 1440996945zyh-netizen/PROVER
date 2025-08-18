package com.yy.ppm.produce.service;

import com.yy.common.page.Pages;
import com.yy.ppm.produce.bean.dto.TPrdPlanEntrustResultDTO;
import com.yy.ppm.produce.bean.dto.TPrdPlanEntrustSearchDTO;
import com.yy.ppm.produce.bean.dto.TPrdVehicleReservationDTO;
import com.yy.ppm.produce.bean.dto.TPrdVehicleReservationSearchDTO;

public interface TPrdPlanEntrustService {

    Pages<TPrdPlanEntrustResultDTO> getPage(TPrdPlanEntrustSearchDTO searchDTO);

    Pages<TPrdVehicleReservationDTO> getVehicleList(TPrdVehicleReservationSearchDTO searchDTO);

}
