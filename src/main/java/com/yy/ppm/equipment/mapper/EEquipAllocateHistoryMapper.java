package com.yy.ppm.equipment.mapper;

import com.yy.ppm.equipment.bean.dto.AllocateEquipDTO;
import com.yy.ppm.equipment.bean.po.EEquipAllocateHistoryPO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 设备调拨历史Mapper接口
 * @author system
 */
public interface EEquipAllocateHistoryMapper {

    /**
     * 新增调拨历史记录
     */
    int insert(EEquipAllocateHistoryPO po);

    /**
     * 根据工单ID查询调拨历史记录
     */
    List<EEquipAllocateHistoryPO> getHistoryByOrderId(@Param("orderId") Long orderId);

    /**
     * 根据工单ID查询调拨设备列表
     */
    List<AllocateEquipDTO> getEquipListByOrderId(@Param("orderId") Long orderId);

    /**
     * 根据工单ID删除
     */
    int deleteByOrderId(@Param("orderId") Long orderId);

    /**
     * 根据工单ID批量删除
     */
    int deleteByOrderIds(@Param("orderIds") List<Long> orderIds);
}
