package com.yy.ppm.gis.service.impl;

import com.yy.ppm.gis.mapper.ShipAisMapper;
import com.yy.ppm.gis.po.MAiaLocationPO;
import com.yy.ppm.gis.service.ShipAisService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Hu Jingjing
 * @version 1.0.0
 * @ClassName ShipAisServiceImpl.java
 * @Description TODO
 * @createTime 2023年09月20日 16:48:00
 */
@Service
public class ShipAisServiceImpl implements ShipAisService {
    @Resource
    ShipAisMapper shipAisMapper;

    @Override
    public List<MAiaLocationPO> getShipTrack(String mmsi, String startTime, String endTime) {
        if (StringUtils.isNotEmpty(mmsi) && StringUtils.isNotEmpty(startTime) && StringUtils.isNotEmpty(endTime)) {
            return shipAisMapper.getShipTrack(mmsi, startTime, endTime);
        } else {
            return null;
        }
    }

    @Override
    public Map getShipInfo(String id) {
        if (StringUtils.isNotEmpty(id)) {
            Map baseInfo = shipAisMapper.getShipBaseInfo(id);
            Map preComing = shipAisMapper.getShipPreComing(id);
            Map resMap = new HashMap();
            resMap.put("baseInfo", baseInfo);
            resMap.put("preComing", preComing);
            return resMap;
        } else {
            return null;
        }
    }
}
