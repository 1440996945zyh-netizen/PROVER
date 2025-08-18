package com.yy.ppm.dispatch.service;

import com.yy.ppm.dispatch.bean.po.TDisLeavePortCostShipPO;

import java.util.List;

public interface TDisLeavePortShipService {

    List<TDisLeavePortCostShipPO> getCostShipList(Long shipvoyageId);
}
