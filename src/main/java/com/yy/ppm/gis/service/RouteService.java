package com.yy.ppm.gis.service;

import com.yy.ppm.gis.dto.route.TRoutesDTO;
import com.yy.ppm.gis.po.TKeypointsPO;
import com.yy.ppm.gis.po.TRoutesPO;

import java.util.List;

/**
 * 路线维护Service接口
 */
public interface RouteService {

  void addKeyPoint(TKeypointsPO point);

  List<TKeypointsPO> listPoints(Long id);

  void editKeyPoint(TKeypointsPO point);

  void deleteKeyPoint(Long id);

  void addRouteInfo(TRoutesPO route);

  List<TRoutesDTO> getAllRouteInfo(Long routeId);

    void editRouteInfo(TRoutesPO route);

    void deleteRouteInfo(Long id);

    void generateNavigationRoute();
}
