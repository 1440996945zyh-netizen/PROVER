package com.yy.ppm.appWorkNew.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.appWork.bean.dto.*;
import com.yy.ppm.appWork.bean.po.TYardTallyItemPO;
import com.yy.ppm.appWork.bean.po.TYardTallyPO;
import com.yy.ppm.master.bean.dto.MWorkProcessSearchDTO;
import com.yy.ppm.master.bean.po.MWorkProcessPO;
import com.yy.ppm.produce.bean.dto.TPrdDispatchSecondarySearchDTO;
import com.yy.ppm.produce.bean.dto.TPrdWorkPlanDTO;
import com.yy.ppm.produce.bean.dto.TPrdWorkPlanSearchDTO;
import org.apache.ibatis.annotations.Param;
import com.yy.ppm.appWorkNew.bean.dto.WorkPlanSearchDTO;

import java.util.List;
import java.util.Map;

public interface TallyJHMapper {

    List<TPrdWorkPlanDTO> getWorkPlan(WorkPlanSearchDTO searchDTO);

    List<Map<String,Object>>  getTrustInfoNo(@Param("trustId") String trustId, @Param("type") List<String> type);

    List<Map<String,Object>> getWorkPlanCargoInfo(WorkPlanSearchDTO searchDTO);

    Integer getIsDept(Long id);
}
