package com.yy.ppm.gis.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.yy.ppm.gis.dto.workArea.Route;
import com.yy.ppm.gis.dto.workArea.ShiftPlan;
import com.yy.ppm.gis.po.TPlanRoutePO;
import com.yy.ppm.gis.po.TPlanWorkareaPO;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Author linqi
 * @Description
 * @Date 2023-06-16 10:21
 */
public interface WorkAreaMapper {

    /**
     * 根据作业日期和班次查`工班计划`
     *
     * @param workDate
     * @param workShift
     * @return
     */
    List<ShiftPlan> listShiftPlan(@Param("workDate") Date workDate, @Param("workShift") String workShift);

    /**
     * id查`船舶艘次信息`
     *
     * @param vesselVisitIds
     * @return
     */
    @DS("ag-qhd-imtos")
    List<Map<String, Object>> listVesselVisit(@Param("vesselVisitIds") List<String> vesselVisitIds);

    /**
     * 查询`路线信息表`，关联`路线关键点信息`
     *
     * @return
     */
    List<Route> listRoute();

    /**
     * 新增`作业计划-作业路线信息`
     *
     * @param planRoute
     */
    int insertPlanRoute(TPlanRoutePO planRoute);

    /**
     * 新增`计划作业区域信息`
     *
     * @param planArea
     */
    int insertPlanArea(TPlanWorkareaPO planArea);

    /**
     * 根据计划和路线关联id删除`作业计划-作业路线信息`
     *
     * @param planRouteId
     */
    int deletePlanRoute(Long planRouteId);

    /**
     * 根据区域id删除`计划作业区域信息`
     *
     * @param areaId
     * @return
     */
    int deletePlanArea(Long areaId);

    /**
     * 根据计划id查询`作业计划-作业路线信息`
     *
     * @param planId
     * @return
     */
    List<TPlanRoutePO> listPlanRoute(Long planId);

    /**
     * 根据计划id查询`计划作业区域信息`
     *
     * @param planId
     * @return
     */
    List<TPlanWorkareaPO> listPlanArea(Long planId);
}
