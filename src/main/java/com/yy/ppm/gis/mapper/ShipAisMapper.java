package com.yy.ppm.gis.mapper;

import com.yy.ppm.gis.po.MAiaLocationPO;

import java.util.List;
import java.util.Map;

/**
 * @author Hu Jingjing
 * @version 1.0.0
 * @ClassName ShipAisMapper.java
 * @Description TODO
 * @createTime 2023年09月20日 16:48:00
 */
public interface ShipAisMapper {
    List<MAiaLocationPO> getShipTrack(String mmsi, String startTime, String endTime);

    Map getShipBaseInfo(String id);

    Map getShipPreComing(String id);
}
