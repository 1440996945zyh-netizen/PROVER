package com.yy.ppm.equipment.service;

import com.yy.common.page.Pages;
import com.yy.ppm.equipment.bean.dto.*;

import java.util.Date;
import java.util.List;

/**
 * 设备维修派工信息 Service 接口
 *
 * @author system
 */
public interface EMaintInfoService {

    /** 查询设备维修信息列表 */
    Pages<EMaintInfoDTO> getList(EMaintInfoSearchDTO searchDTO);

    /** 查询设备维修提报信息列表 */
    Pages<EMaintInfoDTO> listReport(EMaintInfoSearchDTO searchDTO);

    /** 统计各个状态的数量 */
    java.util.Map<String, Object> getStatusCount(EMaintInfoSearchDTO searchDTO);

    /** 查询设备维修派工信息列表 */
    Pages<EMaintInfoDTO> listWork(EMaintInfoSearchDTO searchDTO);

    /** 根据主键查询详情 */
    EMaintInfoDTO getById(Long id);

    /** 新增或修改设备维修派工信息 */
    void save(EMaintInfoDTO dto);

    /** 删除单条记录 */
    void deleteById(Long id);

    /** 批量删除记录 */
    void deleteByIds(List<Long> ids);

    /** 更新派工信息 */
    void updateDispatch(EMaintInfoDTO dto);

    /** 批量作废工单 */
    void cancelWorkOrder(List<Long> ids, String cancelRemark);

    /** 开始维修 */
    void startMaintenance(Long id, Date maintStartTime);

    /** 结束维修 */
    void endMaintenance(Long id, Date maintEndTime, List<Long> imageIds, String maintRemark,
                        List<EMaintPartReplaceDTO> partReplaceList,
                        List<EMaintHourFeedbackDTO> hourFeedbackList);

    /** 根据设备ID查询可用明细 */
    List<EMaintPartReplaceQueryDTO> getAvailableDetailsByEquipId(Long equipId);

    /** 根据承修单位ID查询维修人员下拉列表 */
    List<EMaintRepairUserOptionDTO> getRepairUserListByMaintOrgId(Long maintOrgId);

    /** 根据维修信息ID查询配件更换列表 */
    List<EMaintPartReplaceDTO> getPartReplaceListByMaintInfoId(Long maintInfoId);

    /** 验收处理 */
    void acceptMaintenance(Long id, Integer isAccepted, Integer returnStatus, Integer status, String acceptanceRemark);

    List<EMaintProjApplyDTO> getMaintProjSelectList(String equipId, String appType, String appNumber, String maintInfoId);

    /**
     * 根据申请单号查询维修项目申请表获取维修单位
     */
    EMaintProjApplyDTO getMaintProjApplyByAppNumber(String appNumber);
}
