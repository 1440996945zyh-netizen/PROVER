package com.yy.ppm.equipment.service;

import com.yy.common.page.Pages;
import com.yy.ppm.equipment.bean.dto.EMaintInfoDTO;
import com.yy.ppm.equipment.bean.dto.EMaintInfoSearchDTO;
import com.yy.ppm.equipment.bean.dto.EMaintPartReplaceQueryDTO;

import java.util.List;

/**
 * 设备维修派工信息Service接口
 * @author system
 */
public interface EMaintInfoService {

    /**
     * 查询设备维修信息列表（分页）
     */
    Pages<EMaintInfoDTO> getList(EMaintInfoSearchDTO searchDTO);

    /**
     * 查询设备维修提报信息列表（分页）
     */
    Pages<EMaintInfoDTO> listReport(EMaintInfoSearchDTO searchDTO);

    /**
     * 查询设备维修派工信息列表（分页）
     */
    Pages<EMaintInfoDTO> listWork(EMaintInfoSearchDTO searchDTO);

    /**
     * 根据ID查询设备维修派工信息
     */
    EMaintInfoDTO getById(Long id);

    /**
     * 新增设备维修派工信息
     */
    void save(EMaintInfoDTO dto);

    /**
     * 删除设备维修派工信息
     */
    void deleteById(Long id);

    /**
     * 批量删除设备维修派工信息
     */
    void deleteByIds(List<Long> ids);

    /**
     * 更新派工信息（只更新派工相关字段）
     */
    void updateDispatch(EMaintInfoDTO dto);

    /**
     * 作废工单（批量）
     */
    void cancelWorkOrder(List<Long> ids, String cancelRemark);

    /**
     * 开始维修
     */
    void startMaintenance(Long id, java.util.Date maintStartTime);

    /**
     * 结束维修
     */
    void endMaintenance(Long id, java.util.Date maintEndTime, List<Long> imageIds, String maintRemark, List<com.yy.ppm.equipment.bean.dto.EMaintPartReplaceDTO> partReplaceList);

    /**
     * 根据设备ID查询可用的出库单和申领单明细（用于配件更换选择）
     */
    List<EMaintPartReplaceQueryDTO> getAvailableDetailsByEquipId(Long equipId);

    /**
     * 根据维修信息ID查询配件更换列表
     */
    List<com.yy.ppm.equipment.bean.dto.EMaintPartReplaceDTO> getPartReplaceListByMaintInfoId(Long maintInfoId);

    /**
     * 验收通过
     */
    void acceptMaintenance(Long id, String acceptanceRemark);
}

