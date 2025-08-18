package com.yy.ppm.dispatch.mapper;

import com.yy.ppm.dispatch.bean.po.TDisLeavePortCostShipPO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TDisLeavePortShipMapper {


    List<TDisLeavePortCostShipPO> getCostShipList(@Param("shipvoyageId") Long shipvoyageId);
}
