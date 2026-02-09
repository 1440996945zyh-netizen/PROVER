package com.yy.ppm.equipment.mapper;

import com.yy.framework.annotation.Edit;
import com.yy.ppm.equipment.bean.dto.MEquipmentChangeLogDetailDTO;
import com.yy.ppm.equipment.bean.po.MEquipmentChangeLogDetailPO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 设备变更记录详情Mapper接口
 * @author system
 */
public interface MEquipmentChangeLogDetailMapper {

    /**
     * 根据变更记录主表ID查询详情列表
     */
    List<MEquipmentChangeLogDetailDTO> selectByChangeLogId(@Param("changeLogId") Long changeLogId);

    /**
     * 批量新增变更记录详情
     */
    @Edit
    void insertBatch(List<MEquipmentChangeLogDetailPO> list);
}

