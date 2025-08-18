package com.yy.ppm.gis.mapper;

import com.yy.framework.annotation.Edit;
import com.yy.ppm.gis.dto.route.TRoutesDTO;
import com.yy.ppm.gis.po.TKeypointsPO;
import com.yy.ppm.gis.po.TRoutesPO;
import org.apache.ibatis.annotations.Param;

import java.util.HashMap;
import java.util.List;

public interface RouteMapper {

    @Edit
    Integer insertKeyPoint(TKeypointsPO bo);

    List<TKeypointsPO> getAllKeyPoints(@Param("id") Long id, @Param("status") Integer status);

    @Edit
    Integer updateKeyPoint(TKeypointsPO bo);

    Integer deleteRouteByPointId(Long pointId);

    Integer deleteSysKeyPoint(@Param("pointId") Long pointId);

    @Edit
    Integer insertRouteInfo(TRoutesPO route);

    List<TRoutesDTO> getAllRoutes(@Param("routeId") Long routeId);

    @Edit
    Integer updateRouteInfo(TRoutesPO route);

    Integer deleteRouteById(@Param("routeId") Long routeId);

    Integer delNavigationRoute();

    List<HashMap> getKeyPointList(HashMap parameterMap);

    List<HashMap> getRouteList();

    Integer addNavigationRoute(List<HashMap> map);
}
