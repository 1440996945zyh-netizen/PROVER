package com.yy.ppm.equipment.mapper;

import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.equipment.bean.dto.EMaintInfoBatchUpdateDTO;
import com.yy.ppm.equipment.bean.dto.EMaintInfoDTO;
import com.yy.ppm.equipment.bean.dto.EMaintInfoSearchDTO;
import com.yy.ppm.equipment.bean.dto.EMaintProjApplyDTO;
import com.yy.ppm.equipment.bean.po.EMaintInfoPO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 设备维修派工信息Mapper接口
 * @author system
 */
public interface EMaintInfoMapper {

    /**
     * 查询设备维修派工信息列表（分页）
     */
    Page<EMaintInfoDTO> selectList(EMaintInfoSearchDTO searchDTO);

    /**
     * 统计各个状态的数量
     */
    java.util.List<java.util.Map<String, Object>> selectStatusCount(EMaintInfoSearchDTO searchDTO);

    /**
     * 根据ID查询设备维修派工信息
     */
    EMaintInfoDTO selectById(@Param("id") Long id);

    /**
     * 批量根据ID查询设备维修派工信息（用于校验）
     */
    java.util.List<EMaintInfoDTO> selectByIds(@Param("ids") java.util.List<Long> ids);

    /**
     * 新增设备维修派工信息
     */
    @Edit
    void insert(EMaintInfoPO po);

    /**
     * 修改设备维修派工信息
     */
    @Edit
    void update(EMaintInfoPO po);

    /**
     * 结束维修并清空当前轮次旧验收信息
     */
    @Edit
    void updateEndMaintenance(EMaintInfoPO po);

    /**
     * 批量更新设备维修派工信息状态为作废
     */
    @Edit
    void batchUpdateStatusToCanceled(EMaintInfoBatchUpdateDTO dto);

    /**
     * 删除设备维修派工信息（逻辑删除）
     */
    @Edit
    void deleteById(EMaintInfoPO po);

    /**
     * 批量删除设备维修派工信息（逻辑删除）
     */
    @Edit
    void deleteByIds(EMaintInfoPO po);

    /**
     * 功能描述:  根据设备ID、申请类型、申请单号查询维修项目申请列表
     * @param equipId
     * @param appType
     * @param appNumber
     * @return : java.util.List<com.yy.ppm.equipment.bean.dto.MaintProjApplyDTO>
     */
    List<EMaintProjApplyDTO> getMaintProjSelectList(@Param("equipId")String equipId, @Param("appType")String appType, @Param("appNumber")String appNumber, @Param("maintInfoId")String maintInfoId);

    /**
     * 功能描述: 根据申请单号查询维修项目申请表获取维修单位
     * @param appNumber
     * @return : com.yy.ppm.equipment.bean.dto.MaintProjApplyDTO
     */
    EMaintProjApplyDTO getMaintProjApplyByAppNumber(@Param("appNumber")String appNumber);
}

