package com.yy.ppm.equipment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.equipment.bean.dto.ScrapEquipDTO;
import com.yy.ppm.equipment.bean.po.EEquipScrapHistoryPO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author: fanxianjin
 * @Desc: 设备报废历史Mapper接口
 * @Date: 2026/2/28 14:25
 */
public interface EEquipScrapHistoryMapper extends BaseMapper<EEquipScrapHistoryPO> {

    /**
     * 根据订单ID查询报废设备历史列表
     */
    List<EEquipScrapHistoryPO> getHistoryByOrderId(@Param("orderId") Long orderId);

    /**
     * 新增报废历史记录
     */
    @Edit
    int insert(EEquipScrapHistoryPO po);


}
