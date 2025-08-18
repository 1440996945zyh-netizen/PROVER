package com.yy.ppm.appWorkNew.service;

import java.util.List;
import java.util.Map;

import com.yy.ppm.appWork.bean.dto.AppTallyLadingDTO;
import com.yy.ppm.appWork.bean.dto.TYardMeasureSearchDTO;
import com.yy.ppm.produce.bean.dto.TPrdWorkPlanDTO;
import com.yy.ppm.appWorkNew.bean.dto.WorkPlanSearchDTO;

public interface TallyNewService {

    List<TPrdWorkPlanDTO> getWorkPlan(WorkPlanSearchDTO searchDTO);

    List<Map<String, Object>> getCarDetailedListNew(TYardMeasureSearchDTO tYardMeasureSearchDTO);


}
