package com.yy.ppm.equipment.mapper;

import com.github.pagehelper.Page;
import com.yy.ppm.equipment.bean.dto.EEquipAllocateDTO;
import com.yy.ppm.equipment.bean.dto.EEquipAllocateSearchDTO;
import com.yy.ppm.equipment.bean.dto.AllocateEquipDTO;
import com.yy.ppm.equipment.bean.po.EEquipAllocatePO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 设备调拨Mapper接口
 * @author system
 */
public interface EEquipAllocateMapper {

    /**
     * 查询设备调拨列表（分页）
     */
    Page<EEquipAllocateDTO> getList(EEquipAllocateSearchDTO searchDTO);

    /**
     * 查询可调拨设备列表（分页）
     */
    Page<AllocateEquipDTO> allocateEquipList(EEquipAllocateSearchDTO.EquipSelectSearchDTO searchDTO);

    /**
     * 根据ID查询设备调拨
     */
    EEquipAllocateDTO getById(@Param("id") Long id);

    /**
     * 根据流程ID查询设备调拨
     */
    EEquipAllocatePO getByFlowId(@Param("flowId") String flowId);

    /**
     * 新增设备调拨
     */
    int insert(EEquipAllocatePO po);

    /**
     * 更新设备调拨
     */
    int update(EEquipAllocatePO po);

    /**
     * 更新设备调拨状态
     */
    int updateStatus(EEquipAllocatePO po);

    /**
     * 根据ID查询设备调拨
     */
    EEquipAllocatePO selectById(@Param("id") Long id);

    /**
     * 查询设备调拨列表（导出用，不分页）
     */
    List<EEquipAllocateDTO> getListForExport(EEquipAllocateSearchDTO searchDTO);

}
