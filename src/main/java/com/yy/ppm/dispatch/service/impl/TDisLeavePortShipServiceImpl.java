package com.yy.ppm.dispatch.service.impl;

import com.yy.ppm.dispatch.bean.po.TDisLeavePortCostShipPO;
import com.yy.ppm.dispatch.mapper.TDisLeavePortShipMapper;
import com.yy.ppm.dispatch.service.TDisLeavePortShipService;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.List;


@Service
public class TDisLeavePortShipServiceImpl implements TDisLeavePortShipService {


    @Resource
    private TDisLeavePortShipMapper tDisLeavePortShipMapper;


    @Override
    public List<TDisLeavePortCostShipPO> getCostShipList(Long shipvoyageId) {
        return tDisLeavePortShipMapper.getCostShipList(shipvoyageId);
    }
}
