package com.yy.ppm.equipment.mapper;

import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.equipment.bean.dto.MEquipmentChangeLogDTO;
import com.yy.ppm.equipment.bean.po.MEquipmentChangeLogPO;
import org.apache.ibatis.annotations.Param;

/**
 * 设备变更记录Mapper接口
 * @author system
 */
public interface MEquipmentChangeLogMapper {

    /**
     * 查询变更记录列表（分页）
     */
    Page<MEquipmentChangeLogDTO> selectList(@Param("equipId") Long equipId, @Param("changeType") String changeType);

    /**
     * 根据ID查询变更记录详情
     */
    MEquipmentChangeLogDTO selectById(@Param("id") Long id);

    /**
     * 新增变更记录主表
     */
    @Edit
    void insert(MEquipmentChangeLogPO po);
}

