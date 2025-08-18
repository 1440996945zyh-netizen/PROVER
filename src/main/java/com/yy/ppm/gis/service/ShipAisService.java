package com.yy.ppm.gis.service;

import com.yy.ppm.gis.po.MAiaLocationPO;

import java.util.List;
import java.util.Map;

/**
 * @author Hu Jingjing
 * @version 1.0.0
 * @ClassName ShipAisService.java
 * @Description TODO
 * @createTime 2023年09月20日 16:48:00
 */
public interface ShipAisService {
    List<MAiaLocationPO> getShipTrack(String mmsi, String startTime, String endTime);

    Map getShipInfo(String id);
}
