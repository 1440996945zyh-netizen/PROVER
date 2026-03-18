package com.yy.ppm.equipment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.equipment.bean.dto.EEquipScrapDTO;
import com.yy.ppm.equipment.bean.dto.EEquipScrapSearchDTO;
import com.yy.ppm.equipment.bean.dto.ScrapEquipDTO;
import com.yy.ppm.equipment.bean.po.EEquipScrapPO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author: fanxianjin
 * @Desc: 设备报废Mapper接口
 * @Date: 2026/2/28 14:29
 */
public interface EEquipScrapMapper extends BaseMapper<EEquipScrapPO> {

    /**
     * 查询设备报废列表
     */
    Page<EEquipScrapDTO> getList(EEquipScrapSearchDTO searchDTO);

    /**
     * 查询可报废设备列表
     */
    Page<ScrapEquipDTO> scrapEquipList(EEquipScrapSearchDTO.EquipSelectSearchDTO searchDTO);

    /**
     * 根据ID查询设备报废
     */
    EEquipScrapDTO getById(Long id);

    /**
     * 新增设备报废
     */
    @Edit
    int insert(EEquipScrapPO po);

    /**
     * 修改设备报废
     */
    @Edit
    int update(EEquipScrapPO po);

    /**
     * 修改设备报废状态
     */
    @Edit
    int updateStatus(EEquipScrapPO po);

    /**
     * 根据流程ID查询设备报废
     */
    EEquipScrapPO getByFlowId(String flowId);

    /**
     * 根据ID查询设备报废
     */
    EEquipScrapPO selectById(@Param("id") Long id);

    /**
     * 查询设备报废列表（导出用，不分页）
     */
    List<EEquipScrapDTO> getListForExport(EEquipScrapSearchDTO searchDTO);

    /**
     * 功能描述: 根据流程实例ID获取业务ID
     * @param processInstanceId
     * @return : java.lang.Long
     */
    Long getBusinessDataIdByProcessInstanceId(String processInstanceId);

    /**
     * 根据ID删除设备报废
     */
    @Edit
    int deleteById(@Param("id") Long id);

    /**
     * 批量删除设备报废
     */
    @Edit
    int deleteByIds(@Param("ids") List<Long> ids);
}
