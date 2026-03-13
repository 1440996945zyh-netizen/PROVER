package com.yy.ppm.equipment.service.impl;

import cn.hutool.core.lang.Snowflake;
import com.yy.common.page.Pages;
import com.yy.common.util.JSONUtils;
import com.yy.common.util.PageHelperUtils;
import com.yy.common.util.SecurityUtils;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.auth.bean.dto.UserInfo;
import com.yy.ppm.common.bean.dto.SysFileDTO;
import com.yy.ppm.common.enums.SerialNumberPrefixEnum;
import com.yy.ppm.common.service.SysFileService;
import com.yy.ppm.common.service.impl.CommonServiceImpl;
import com.yy.ppm.equipment.bean.dto.*;
import com.yy.ppm.equipment.bean.po.EMaintHourFeedbackPO;
import com.yy.ppm.equipment.bean.po.EMaintInfoPO;
import com.yy.ppm.equipment.bean.po.EMaintInfoPartItemPO;
import com.yy.ppm.equipment.bean.po.EMaintPartReplacePO;
import com.yy.ppm.equipment.enums.EMaintInfoLogActionEnum;
import com.yy.ppm.equipment.mapper.EMaintHourFeedbackMapper;
import com.yy.ppm.equipment.mapper.EMEquipRepairUserMapper;
import com.yy.ppm.equipment.mapper.EMaintInfoMapper;
import com.yy.ppm.equipment.mapper.EMaintInfoPartItemMapper;
import com.yy.ppm.equipment.mapper.EMaintPartReplaceMapper;
import com.yy.ppm.equipment.service.EMaintInfoLogService;
import com.yy.ppm.equipment.service.EMaintInfoService;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * 设备维修派工信息 Service 实现类
 *
 * @author system
 */
@RequiredArgsConstructor
@Service
public class EMaintInfoServiceImpl implements EMaintInfoService {

    @Resource
    private EMaintInfoMapper mapper;

    @Resource
    private Snowflake snowflake;

    @Resource
    private SecurityUtils securityUtils;

    @Resource
    private SysFileService sysFileService;

    @Resource
    private EMaintPartReplaceMapper partReplaceMapper;

    @Resource
    private EMaintInfoPartItemMapper partItemMapper;

    @Resource
    private EMaintHourFeedbackMapper hourFeedbackMapper;

    @Resource
    private EMEquipRepairUserMapper repairUserMapper;

    @Resource
    private EMaintInfoLogService maintInfoLogService;

    @Autowired
    private CommonServiceImpl commonService;

    /**
     * 查询设备维修信息列表
     */
    @Override
    public Pages<EMaintInfoDTO> getList(EMaintInfoSearchDTO searchDTO) {
        // 非派工角色仅能查看自己创建或负责的记录
        UserInfo userInfo = securityUtils.getUserInfo();
        if (userInfo != null) {
            boolean isAdmin = "1".equals(userInfo.getIsSuperadmin());
            boolean hasMaintDisRole = userInfo.getRoles() != null
                    && userInfo.getRoles().contains("EQPT_MAINT_DIS");
            if (!isAdmin && !hasMaintDisRole) {
                Long loginUserId = securityUtils.getLoginUserId();
                searchDTO.setCreateBy(loginUserId);
                searchDTO.setMaintLeaderId(String.valueOf(loginUserId));
            }
        }

        return PageHelperUtils.limit(searchDTO, () -> mapper.selectList(searchDTO));
    }

    /**
     * 查询设备维修提报信息列表
     */
    @Override
    public Pages<EMaintInfoDTO> listReport(EMaintInfoSearchDTO searchDTO) {
        // 非派工角色仅能查看自己创建或负责的记录
        UserInfo userInfo = securityUtils.getUserInfo();
        if (userInfo != null) {
            boolean isAdmin = "1".equals(userInfo.getIsSuperadmin());
            boolean hasMaintDisRole = userInfo.getRoles() != null
                    && userInfo.getRoles().contains("EQPT_MAINT_DIS");
            if (!isAdmin && !hasMaintDisRole) {
                Long loginUserId = securityUtils.getLoginUserId();
                searchDTO.setCreateBy(loginUserId);
                searchDTO.setMaintLeaderId(String.valueOf(loginUserId));
            }
        }

        return PageHelperUtils.limit(searchDTO, () -> mapper.selectList(searchDTO));
    }

    /**
     * 统计各个状态的数量
     */
    @Override
    public Map<String, Object> getStatusCount(EMaintInfoSearchDTO searchDTO) {
        // 非派工角色仅能查看自己创建或负责的记录
        UserInfo userInfo = securityUtils.getUserInfo();
        if (userInfo != null) {
            boolean isAdmin = "1".equals(userInfo.getIsSuperadmin());
            boolean hasMaintDisRole = userInfo.getRoles() != null
                    && userInfo.getRoles().contains("EQPT_MAINT_DIS");
            if (!isAdmin && !hasMaintDisRole) {
                Long loginUserId = securityUtils.getLoginUserId();
                searchDTO.setCreateBy(loginUserId);
                searchDTO.setMaintLeaderId(String.valueOf(loginUserId));
            }
        }

        List<Map<String, Object>> list = mapper.selectStatusCount(searchDTO);

        Map<String, Object> result = new LinkedHashMap<>();
        int reportCount = 0;
        int dispatchCount = 0;
        int finishCount = 0;
        int acceptCount = 0;

        if (list != null) {
            for (Map<String, Object> map : list) {
                Object statusObj = map.get("status");
                if (statusObj == null) statusObj = map.get("STATUS");

                Object countObj = map.get("count");
                if (countObj == null) countObj = map.get("COUNT");

                if (statusObj != null && countObj != null) {
                    int status = Integer.parseInt(statusObj.toString());
                    int count = Integer.parseInt(countObj.toString());
                    if (status == 0) {
                        reportCount += count;
                    } else if (status == 1) {
                        dispatchCount += count;
                    } else if (status == 4) {
                        finishCount += count;
                    } else if (status == 5) {
                        acceptCount += count;
                    }
                }
            }
        }

        result.put("reportCount", reportCount);
        result.put("dispatchCount", dispatchCount);
        result.put("finishCount", finishCount);
        result.put("acceptCount", acceptCount);

        return result;
    }

    /**
     * 查询设备维修派工信息列表
     */
    @Override
    public Pages<EMaintInfoDTO> listWork(EMaintInfoSearchDTO searchDTO) {
        return PageHelperUtils.limit(searchDTO, () -> mapper.selectList(searchDTO));
    }

    /**
     * 根据主键查询设备维修派工信息
     */
    @Override
    public EMaintInfoDTO getById(Long id) {
        // 校验记录状态
        EMaintInfoDTO dto = mapper.selectById(id);
        if (dto != null && dto.getId() != null) {
            dto.setPartReplaceList(partReplaceMapper.selectListByMaintInfoId(dto.getId()));
            dto.setItemList(partItemMapper.selectListByMaintInfoId(dto.getId()));
            dto.setHourFeedbackList(hourFeedbackMapper.selectListByMaintInfoId(dto.getId()));
            dto.setOperateLogList(maintInfoLogService.getListByMaintInfoId(dto.getId()));
        }
        return dto;
    }

    /**
     * 新增或修改设备维修派工信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public void save(EMaintInfoDTO dto) {
        if (dto.getEquipId() == null) {
            throw new BusinessRuntimeException("设备ID不能为空");
        }
        if (dto.getFaultFindTime() == null) {
            throw new BusinessRuntimeException("故障发现时间不能为空");
        }

        EMaintInfoPO po = new EMaintInfoPO();
        BeanUtils.copyProperties(dto, po);

        if (dto.getId() == null) {
            Long id = snowflake.nextId();
            po.setId(id);
            po.setNow(new Date());
            po.setWorkOrderNo(commonService.generateSerialNumber(SerialNumberPrefixEnum.REPAIR));
            EMaintInfoLogActionEnum action = EMaintInfoLogActionEnum.REPORT;
            // 派工状态下自动回填派工人和派工时间
            if (dto.getStatus() != null && dto.getStatus() == 1) {
                po.setDispatcherId(securityUtils.getLoginUserId());
                po.setDispatcherName(securityUtils.getUserInfo().getUserName());
                po.setDispatchTime(new Date());
                action = EMaintInfoLogActionEnum.DISPATCH;
            } else if (dto.getStatus() == null) {
                po.setStatus(0);
            }
            mapper.insert(po);
            syncPartItemList(id, dto.getItemList());
            if (dto.getFaultImageIds() != null && !dto.getFaultImageIds().isEmpty()) {
                sysFileService.saveFileBusRelation(dto.getFaultImageIds(), id);
            }

            maintInfoLogService.saveLog(id, po.getWorkOrderNo(), action,
                    null, po.getStatus(), po.getFaultDesc(), buildReportSnapshot(po, dto));
            return;
        }

        // 修改前先校验记录是否存在以及当前状态
        EMaintInfoDTO existingDto = mapper.selectById(dto.getId());
        if (existingDto == null) {
            throw new BusinessRuntimeException("记录不存在");
        }
        Integer status = existingDto.getStatus();
        if (status != null) {
            if (status == 2) {
                throw new BusinessRuntimeException("维修中的记录不允许修改");
            }
            if (status == 4) {
                throw new BusinessRuntimeException("维修完成的记录不允许修改");
            }
            if (status == 5) {
                throw new BusinessRuntimeException("验收通过的记录不允许修改");
            }
            if (status == 7) {
                throw new BusinessRuntimeException("已作废的记录不允许修改");
            }
        }
        po.setStatus(null);
        mapper.update(po);
        if (dto.getItemList() != null) {
            syncPartItemList(dto.getId(), dto.getItemList());
        }
        if (dto.getFaultImageIds() != null && !dto.getFaultImageIds().isEmpty()) {
            sysFileService.saveFileBusRelation(dto.getFaultImageIds(), dto.getId());
        }
        maintInfoLogService.saveLog(po.getId(), po.getWorkOrderNo(), EMaintInfoLogActionEnum.UPDATE,
                status, status, po.getFaultDesc(), buildReportSnapshot(po, dto));
    }

    /**
     * 根据设备ID查询可用的出库单和申领单明细
     */
    @Override
    public List<EMaintPartReplaceQueryDTO> getAvailableDetailsByEquipId(Long equipId) {
        if (equipId == null) {
            throw new BusinessRuntimeException("设备ID不能为空");
        }
        return partReplaceMapper.selectAvailableDetailsByEquipId(equipId);
    }

    /**
     * 根据承修单位ID查询维修人员下拉列表
     */
    @Override
    public List<EMaintRepairUserOptionDTO> getRepairUserListByMaintOrgId(Long maintOrgId) {
        if (maintOrgId == null) {
            throw new BusinessRuntimeException("承修单位ID不能为空");
        }
        return repairUserMapper.getRepairUserListByMaintOrgId(maintOrgId);
    }

    /**
     * 根据维修信息ID查询配件更换列表
     */
    @Override
    public List<EMaintPartReplaceDTO> getPartReplaceListByMaintInfoId(Long maintInfoId) {
        if (maintInfoId == null) {
            throw new BusinessRuntimeException("维修信息ID不能为空");
        }
        return partReplaceMapper.selectListByMaintInfoId(maintInfoId);
    }

    /**
     * 删除单条设备维修派工信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public void deleteById(Long id) {
        //只有作废和提报状态的数据可以删除
        Integer status = mapper.selectById(id).getStatus();
        if (status != null) {
            if (status != 0 && status != 7) {
                throw new BusinessRuntimeException("只有提报和作废状态的数据可以删除");
            }
        }
        EMaintInfoPO po = new EMaintInfoPO();
        po.setId(id);
        po.setNow(new Date());
        po.setLoginUserId(securityUtils.getLoginUserId());
        po.setLoginUserName(securityUtils.getUserInfo().getUserName());
        mapper.deleteById(po);
    }

    /**
     * 批量删除设备维修派工信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public void deleteByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new BusinessRuntimeException("ID列表不能为空");
        }
        //只有作废和提报状态的数据可以删除
        for (Long id : ids) {
            Integer status = mapper.selectById(id).getStatus();
            if (status != null) {
                if (status != 0 && status != 7) {
                    throw new BusinessRuntimeException("只有提报和作废状态的数据可以删除");
                }
            }
        }
        EMaintInfoPO po = new EMaintInfoPO();
        po.setIds(ids);
        mapper.deleteByIds(po);
    }

    /**
     * 更新派工信息，仅更新派工相关字段
     */
    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public void updateDispatch(EMaintInfoDTO dto) {
        if (dto.getId() == null) {
            throw new BusinessRuntimeException("ID不能为空");
        }

        EMaintInfoDTO existingDto = mapper.selectById(dto.getId());
        if (existingDto == null) {
            throw new BusinessRuntimeException("记录不存在");
        }

        Date now = new Date();
        EMaintInfoPO po = new EMaintInfoPO();
        po.setId(dto.getId());
        po.setNow(now);
        po.setLoginUserId(securityUtils.getLoginUserId());
        po.setLoginUserName(securityUtils.getLoginUserName());

        if (dto.getDispatchTypeCode() != null) {
            po.setDispatchTypeCode(dto.getDispatchTypeCode());
        }
        if (dto.getDispatchTypeName() != null) {
            po.setDispatchTypeName(dto.getDispatchTypeName());
        }
        if (dto.getMaintOrgId() != null) {
            po.setMaintOrgId(dto.getMaintOrgId());
        }
        if (dto.getMaintOrgName() != null) {
            po.setMaintOrgName(dto.getMaintOrgName());
        }
        if (dto.getMaintLeaderId() != null) {
            po.setMaintLeaderId(dto.getMaintLeaderId());
        }
        if (dto.getMaintLeaderName() != null) {
            po.setMaintLeaderName(dto.getMaintLeaderName());
        }
        if (dto.getMaintLeaderMobile() != null) {
            po.setMaintLeaderMobile(dto.getMaintLeaderMobile());
        }
        if (dto.getMantAppNumber() != null) {
            po.setMantAppNumber(dto.getMantAppNumber());
        }
        if (dto.getIsSpecialJob() != null) {
            po.setIsSpecialJob(dto.getIsSpecialJob());
            if ("1".equals(dto.getIsSpecialJob())) {
                po.setSpecialJobCode(dto.getSpecialJobCode());
                po.setSpecialJobName(dto.getSpecialJobName());
            } else {
                po.setSpecialJobCode("");
                po.setSpecialJobName("");
            }
        }
        po.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);
        po.setDispatcherId(securityUtils.getLoginUserId());
        po.setDispatcherName(securityUtils.getLoginUserName());
        po.setDispatchTime(now);

        mapper.update(po);
        if (dto.getItemList() != null) {
            syncPartItemList(dto.getId(), dto.getItemList());
        }

        EMaintInfoDTO latestDto = mapper.selectById(dto.getId());
        if (latestDto != null) {
            latestDto.setItemList(partItemMapper.selectListByMaintInfoId(dto.getId()));
            maintInfoLogService.saveLog(dto.getId(), latestDto.getWorkOrderNo(), EMaintInfoLogActionEnum.DISPATCH,
                    existingDto.getStatus(), latestDto.getStatus(), null, buildDispatchSnapshot(latestDto));
        }
    }

    /**
     * 批量作废工单
     */
    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public void cancelWorkOrder(List<Long> ids, String cancelRemark) {
        if (ids == null || ids.isEmpty()) {
            throw new BusinessRuntimeException("ID列表不能为空");
        }

        List<EMaintInfoDTO> records = mapper.selectByIds(ids);
        if (records.size() < ids.size()) {
            List<Long> foundIds = records.stream()
                    .map(EMaintInfoDTO::getId)
                    .collect(java.util.stream.Collectors.toList());
            List<Long> notFoundIds = ids.stream()
                    .filter(id -> !foundIds.contains(id))
                    .collect(java.util.stream.Collectors.toList());
            throw new BusinessRuntimeException("以下ID的记录不存在：" + notFoundIds);
        }

        List<String> alreadyCanceledWorkOrders = new ArrayList<>();
        List<Long> validIds = new ArrayList<>();
        List<EMaintInfoDTO> validRecords = new ArrayList<>();
        for (EMaintInfoDTO dto : records) {
            if (dto.getStatus() != null && dto.getStatus() == 7) {
                if (dto.getWorkOrderNo() != null && !dto.getWorkOrderNo().isEmpty()) {
                    alreadyCanceledWorkOrders.add(dto.getWorkOrderNo());
                } else {
                    alreadyCanceledWorkOrders.add("ID:" + dto.getId());
                }
            } else {
                validIds.add(dto.getId());
                validRecords.add(dto);
            }
        }

        if (!alreadyCanceledWorkOrders.isEmpty()) {
            String workOrderList = String.join("、", alreadyCanceledWorkOrders);
            throw new BusinessRuntimeException("以下工单已经作废，不能重复作废：" + workOrderList);
        }

        if (!validIds.isEmpty()) {
            Date now = new Date();
            EMaintInfoBatchUpdateDTO updateDTO = new EMaintInfoBatchUpdateDTO();
            updateDTO.setIds(validIds);
            updateDTO.setStatus(7);
            updateDTO.setLoginUserId(securityUtils.getLoginUserId());
            updateDTO.setLoginUserName(securityUtils.getLoginUserName());
            updateDTO.setNow(now);
            updateDTO.setCancelRemark(cancelRemark);
            mapper.batchUpdateStatusToCanceled(updateDTO);

            for (EMaintInfoDTO dto : validRecords) {
                maintInfoLogService.saveLog(dto.getId(), dto.getWorkOrderNo(), EMaintInfoLogActionEnum.CANCEL,
                        dto.getStatus(), 7, cancelRemark, buildCancelSnapshot(now, cancelRemark));
            }
        }
    }

    /**
     * 开始维修
     */
    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public void startMaintenance(Long id, Date maintStartTime) {
        if (id == null) {
            throw new BusinessRuntimeException("ID不能为空");
        }
        if (maintStartTime == null) {
            throw new BusinessRuntimeException("维修开始时间不能为空");
        }

        EMaintInfoDTO dto = mapper.selectById(id);
        if (dto == null) {
            throw new BusinessRuntimeException("记录不存在");
        }
        if (dto.getStatus() == null || dto.getStatus() != 1) {
            throw new BusinessRuntimeException("只有已派工状态的记录才能开始维修");
        }

        EMaintInfoPO po = new EMaintInfoPO();
        po.setId(id);
        po.setStatus(2);
        po.setMaintStartTime(maintStartTime);
        po.setNow(new Date());
        po.setLoginUserId(securityUtils.getLoginUserId());
        po.setLoginUserName(securityUtils.getLoginUserName());
        mapper.update(po);

        maintInfoLogService.saveLog(id, dto.getWorkOrderNo(), EMaintInfoLogActionEnum.START_MAINT,
                dto.getStatus(), 2, null, buildStartMaintSnapshot(maintStartTime));
    }

    /**
     * 结束维修
     */
    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public void endMaintenance(Long id, Date maintEndTime, List<Long> imageIds, String maintRemark,
                               List<EMaintPartReplaceDTO> partReplaceList,
                               List<EMaintHourFeedbackDTO> hourFeedbackList) {
        if (id == null) {
            throw new BusinessRuntimeException("ID不能为空");
        }
        if (maintEndTime == null) {
            throw new BusinessRuntimeException("结束维修时间不能为空");
        }

        EMaintInfoDTO dto = mapper.selectById(id);
        if (dto == null) {
            throw new BusinessRuntimeException("记录不存在");
        }
        if (dto.getStatus() == null || dto.getStatus() != 2) {
            throw new BusinessRuntimeException("只有维修中状态的记录才能结束维修");
        }

        Date now = new Date();
        EMaintInfoPO po = new EMaintInfoPO();
        po.setId(id);
        po.setStatus(4);
        po.setMaintEndTime(maintEndTime);
        po.setMaintRemark(maintRemark);
        po.setNow(now);
        po.setLoginUserId(securityUtils.getLoginUserId());
        po.setLoginUserName(securityUtils.getLoginUserName());
        mapper.updateEndMaintenance(po);

        List<EMaintPartReplaceDTO> savedPartReplaceList = new ArrayList<>();
        partReplaceMapper.deleteByMaintInfoId(id);
        if (partReplaceList != null && !partReplaceList.isEmpty()) {
            for (EMaintPartReplaceDTO partReplaceDTO : partReplaceList) {
                if (partReplaceDTO.getUsedQuantity() != null
                        && partReplaceDTO.getUsedQuantity().compareTo(BigDecimal.ZERO) > 0) {
                    EMaintPartReplacePO partReplacePO = new EMaintPartReplacePO();
                    BeanUtils.copyProperties(partReplaceDTO, partReplacePO);
                    partReplacePO.setId(snowflake.nextId());
                    partReplacePO.setMaintInfoId(id);
                    partReplacePO.setEquipId(dto.getEquipId());
                    partReplacePO.setNow(now);
                    partReplacePO.setLoginUserId(securityUtils.getLoginUserId());
                    partReplacePO.setLoginUserName(securityUtils.getLoginUserName());
                    partReplaceMapper.insert(partReplacePO);

                    EMaintPartReplaceDTO savedPartReplace = new EMaintPartReplaceDTO();
                    BeanUtils.copyProperties(partReplacePO, savedPartReplace);
                    savedPartReplaceList.add(savedPartReplace);
                }
            }
        }

        syncHourFeedbackList(id, hourFeedbackList);

        if (imageIds != null && !imageIds.isEmpty()) {
            List<SysFileDTO> existingFaultImages = sysFileService.getBusFiles(id, "MAINT_INFO_IMAGE");
            List<Long> allFileIds = new ArrayList<>(imageIds);
            if (existingFaultImages != null && !existingFaultImages.isEmpty()) {
                for (SysFileDTO file : existingFaultImages) {
                    allFileIds.add(file.getId());
                }
            }
            sysFileService.saveFileBusRelation(allFileIds, id);
        }

        maintInfoLogService.saveLog(id, dto.getWorkOrderNo(), EMaintInfoLogActionEnum.END_MAINT,
                dto.getStatus(), 4, maintRemark,
                buildEndMaintSnapshot(dto, maintEndTime, maintRemark, imageIds, savedPartReplaceList, hourFeedbackList));
    }

    /**
     * 验收通过
     */
    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public void acceptMaintenance(Long id, Integer isAccepted, Integer returnStatus, Integer status, String acceptanceRemark) {
        if (id == null) {
            throw new BusinessRuntimeException("ID不能为空");
        }
        if (isAccepted == null || (isAccepted != 0 && isAccepted != 1)) {
            throw new BusinessRuntimeException("验收结果参数错误");
        }
        if (acceptanceRemark == null || acceptanceRemark.trim().isEmpty()) {
            throw new BusinessRuntimeException("验收原因不能为空");
        }

        EMaintInfoDTO dto = mapper.selectById(id);
        if (dto == null) {
            throw new BusinessRuntimeException("记录不存在");
        }
        if (dto.getStatus() == null || dto.getStatus() != 4) {
            throw new BusinessRuntimeException("只有维修完成状态的记录才能验收");
        }

        Date now = new Date();
        String finalAcceptanceRemark = acceptanceRemark.trim();
        Integer toStatus;
        EMaintInfoLogActionEnum action;
        EMaintInfoPO po = new EMaintInfoPO();
        po.setId(id);
        if (isAccepted == 1) {
            Integer finalStatus = status == null ? 5 : status;
            if (!Integer.valueOf(5).equals(finalStatus)) {
                throw new BusinessRuntimeException("验收通过时状态必须为5");
            }
            toStatus = 5;
            action = EMaintInfoLogActionEnum.ACCEPT_PASS;
            po.setStatus(5);
        } else {
            if (returnStatus == null) {
                throw new BusinessRuntimeException("验收不通过时退回状态不能为空");
            }
            if (returnStatus != 0 && returnStatus != 1 && returnStatus != 2) {
                throw new BusinessRuntimeException("退回状态只能是提报、已派工或维修中");
            }
            if (status != null && !status.equals(returnStatus)) {
                throw new BusinessRuntimeException("状态与退回状态不一致");
            }
            toStatus = returnStatus;
            action = EMaintInfoLogActionEnum.ACCEPT_REJECT;
            po.setStatus(returnStatus);
        }
        po.setAccepterId(securityUtils.getLoginUserId());
        po.setAccepterName(securityUtils.getLoginUserName());
        po.setAcceptanceTime(now);
        po.setAcceptanceRemark(finalAcceptanceRemark);
        po.setNow(now);
        po.setLoginUserId(securityUtils.getLoginUserId());
        po.setLoginUserName(securityUtils.getLoginUserName());
        mapper.update(po);

        if (isAccepted == 0) {
            partReplaceMapper.deleteByMaintInfoId(id);
        }

        String snapshotJson = isAccepted == 1
                ? buildAcceptPassSnapshot(now, finalAcceptanceRemark)
                : buildAcceptRejectSnapshot(now, finalAcceptanceRemark, returnStatus);
        maintInfoLogService.saveLog(id, dto.getWorkOrderNo(), action, dto.getStatus(), toStatus,
                finalAcceptanceRemark, snapshotJson);
    }

    private String buildReportSnapshot(EMaintInfoPO po, EMaintInfoDTO dto) {
        Map<String, Object> snapshot = new LinkedHashMap<>();
        snapshot.put("equipId", po.getEquipId());
        snapshot.put("faultFindTime", po.getFaultFindTime());
        snapshot.put("emergencyLevel", po.getEmergencyLevel());
        snapshot.put("maintTypeCode", po.getMaintTypeCode());
        snapshot.put("maintTypeName", po.getMaintTypeName());
        snapshot.put("isStopped", po.getIsStopped());
        snapshot.put("faultDesc", po.getFaultDesc());
        snapshot.put("reportTypeCode", po.getReportTypeCode());
        snapshot.put("reportTypeName", po.getReportTypeName());
        snapshot.put("faultImageIds", dto.getFaultImageIds());
        snapshot.put("itemList", dto.getItemList());
        return buildSnapshot(snapshot);
    }

    private String buildDispatchSnapshot(EMaintInfoDTO dto) {
        Map<String, Object> snapshot = new LinkedHashMap<>();
        snapshot.put("dispatchTypeCode", dto.getDispatchTypeCode());
        snapshot.put("dispatchTypeName", dto.getDispatchTypeName());
        snapshot.put("maintOrgId", dto.getMaintOrgId());
        snapshot.put("maintOrgName", dto.getMaintOrgName());
        snapshot.put("maintLeaderId", dto.getMaintLeaderId());
        snapshot.put("maintLeaderName", dto.getMaintLeaderName());
        snapshot.put("maintLeaderMobile", dto.getMaintLeaderMobile());
        snapshot.put("isSpecialJob", dto.getIsSpecialJob());
        snapshot.put("specialJobCode", dto.getSpecialJobCode());
        snapshot.put("specialJobName", dto.getSpecialJobName());
        snapshot.put("dispatchTime", dto.getDispatchTime());
        snapshot.put("itemList", dto.getItemList());
        snapshot.put("mantAppNumber", dto.getMantAppNumber());
        return buildSnapshot(snapshot);
    }

    private String buildStartMaintSnapshot(Date maintStartTime) {
        Map<String, Object> snapshot = new LinkedHashMap<>();
        snapshot.put("maintStartTime", maintStartTime);
        return buildSnapshot(snapshot);
    }

    private String buildEndMaintSnapshot(EMaintInfoDTO dto, Date maintEndTime, String maintRemark, List<Long> imageIds,
                                         List<EMaintPartReplaceDTO> partReplaceList,
                                         List<EMaintHourFeedbackDTO> hourFeedbackList) {
        Map<String, Object> snapshot = new LinkedHashMap<>();
        snapshot.put("maintStartTime", dto.getMaintStartTime());
        snapshot.put("maintEndTime", maintEndTime);
        snapshot.put("maintRemark", maintRemark);
        snapshot.put("maintDuration", calculateDuration(dto.getMaintStartTime(), maintEndTime));
        snapshot.put("faultDuration", calculateDuration(dto.getFaultFindTime(), maintEndTime));
        snapshot.put("partReplaceList", partReplaceList);
        snapshot.put("hourFeedbackList", hourFeedbackList);
        snapshot.put("completionImageIds", imageIds);
        return buildSnapshot(snapshot);
    }

    private String buildAcceptPassSnapshot(Date acceptanceTime, String acceptanceRemark) {
        Map<String, Object> snapshot = new LinkedHashMap<>();
        snapshot.put("acceptanceTime", acceptanceTime);
        snapshot.put("acceptanceRemark", acceptanceRemark);
        return buildSnapshot(snapshot);
    }

    private String buildAcceptRejectSnapshot(Date acceptanceTime, String acceptanceRemark, Integer returnStatus) {
        Map<String, Object> snapshot = new LinkedHashMap<>();
        snapshot.put("acceptanceTime", acceptanceTime);
        snapshot.put("acceptanceRemark", acceptanceRemark);
        snapshot.put("returnStatus", returnStatus);
        snapshot.put("returnStatusName", getStatusName(returnStatus));
        return buildSnapshot(snapshot);
    }

    private String buildCancelSnapshot(Date cancelTime, String cancelRemark) {
        Map<String, Object> snapshot = new LinkedHashMap<>();
        snapshot.put("cancelTime", cancelTime);
        snapshot.put("cancelRemark", cancelRemark);
        return buildSnapshot(snapshot);
    }

    private String buildSnapshot(Map<String, Object> snapshot) {
        return JSONUtils.NON_NULL.toJSONString(snapshot);
    }

    private BigDecimal calculateDuration(Date startTime, Date endTime) {
        if (startTime == null || endTime == null) {
            return null;
        }
        return BigDecimal.valueOf(endTime.getTime() - startTime.getTime())
                .divide(BigDecimal.valueOf(1000 * 60 * 60), 2, RoundingMode.HALF_UP);
    }

    private String getStatusName(Integer status) {
        if (status == null) {
            return null;
        }
        switch (status) {
            case 0:
                return "提报";
            case 1:
                return "已派工";
            case 2:
                return "维修中";
            case 4:
                return "维修完成";
            case 5:
                return "验收通过";
            case 7:
                return "作废";
            default:
                return String.valueOf(status);
        }
    }

    /**
     * 同步派工部位部件明细
     */
    private void syncPartItemList(Long maintInfoId, List<EMaintInfoPartItemDTO> itemList) {
        if (maintInfoId == null) {
            return;
        }

        Date now = new Date();
        Long loginUserId = securityUtils.getLoginUserId();
        String loginUserName = securityUtils.getUserInfo() == null ? null : securityUtils.getUserInfo().getUserName();

        // 先逻辑删除旧明细
        EMaintInfoPartItemPO deletePO = new EMaintInfoPartItemPO();
        deletePO.setMaintInfoId(maintInfoId);
        deletePO.setNow(now);
        deletePO.setLoginUserId(loginUserId);
        deletePO.setLoginUserName(loginUserName);
        partItemMapper.logicDeleteByMaintInfoId(deletePO);

        if (itemList == null || itemList.isEmpty()) {
            return;
        }

        // 以部件ID去重后重新插入
        Map<Long, EMaintInfoPartItemDTO> distinctMap = new LinkedHashMap<>();
        for (EMaintInfoPartItemDTO item : itemList) {
            if (item == null || item.getEquipUnitId() == null || item.getEquipInstitutionId() == null) {
                continue;
            }
            distinctMap.put(item.getEquipUnitId(), item);
        }

        List<EMaintInfoPartItemPO> saveList = new ArrayList<>();
        int sortNum = 1;
        for (EMaintInfoPartItemDTO item : distinctMap.values()) {
            EMaintInfoPartItemPO po = new EMaintInfoPartItemPO();
            BeanUtils.copyProperties(item, po);
            po.setId(snowflake.nextId());
            po.setMaintInfoId(maintInfoId);
            po.setSortOrder(item.getSortOrder() == null ? sortNum : item.getSortOrder());
            po.setNow(now);
            po.setLoginUserId(loginUserId);
            po.setLoginUserName(loginUserName);
            saveList.add(po);
            sortNum++;
        }

        if (!saveList.isEmpty()) {
            partItemMapper.insertBatch(saveList);
        }
    }

    /**
     * 同步作业工时反馈明细
     */
    private void syncHourFeedbackList(Long maintInfoId, List<EMaintHourFeedbackDTO> hourFeedbackList) {
        if (maintInfoId == null) {
            return;
        }

        Date now = new Date();
        Long loginUserId = securityUtils.getLoginUserId();
        String loginUserName = securityUtils.getUserInfo() == null ? null : securityUtils.getUserInfo().getUserName();

        // 先逻辑删除旧明细
        EMaintHourFeedbackPO deletePO = new EMaintHourFeedbackPO();
        deletePO.setMaintInfoId(maintInfoId);
        deletePO.setNow(now);
        deletePO.setLoginUserId(loginUserId);
        deletePO.setLoginUserName(loginUserName);
        hourFeedbackMapper.logicDeleteByMaintInfoId(deletePO);

        if (hourFeedbackList == null || hourFeedbackList.isEmpty()) {
            return;
        }

        List<EMaintHourFeedbackPO> saveList = new ArrayList<>();
        for (EMaintHourFeedbackDTO item : hourFeedbackList) {
            if (item == null || item.getMaintUserId() == null) {
                continue;
            }
            if (item.getStartTime() != null && item.getEndTime() != null
                    && item.getEndTime().before(item.getStartTime())) {
                throw new BusinessRuntimeException("作业工时反馈结束时间不能早于开始时间");
            }
            if (item.getWorkHour() != null && item.getWorkHour().compareTo(BigDecimal.ZERO) < 0) {
                throw new BusinessRuntimeException("作业工时不能小于0");
            }

            EMaintHourFeedbackPO po = new EMaintHourFeedbackPO();
            BeanUtils.copyProperties(item, po);
            po.setId(snowflake.nextId());
            po.setMaintInfoId(maintInfoId);
            po.setWorkHour(calculateWorkHour(item.getStartTime(), item.getEndTime(), item.getWorkHour()));
            po.setNow(now);
            po.setLoginUserId(loginUserId);
            po.setLoginUserName(loginUserName);
            saveList.add(po);
        }

        if (!saveList.isEmpty()) {
            hourFeedbackMapper.insertBatch(saveList);
        }
    }

    /**
     * 计算作业工时
     */
    private BigDecimal calculateWorkHour(Date startTime, Date endTime, BigDecimal workHour) {
        if (workHour != null) {
            return workHour;
        }
        if (startTime == null || endTime == null) {
            return null;
        }
        long diffMillis = endTime.getTime() - startTime.getTime();
        if (diffMillis < 0) {
            throw new BusinessRuntimeException("作业工时反馈结束时间不能早于开始时间");
        }
        return BigDecimal.valueOf(diffMillis)
                .divide(BigDecimal.valueOf(1000 * 60 * 60), 2, RoundingMode.HALF_UP);
    }


    /**
     * 功能描述: 根据设备ID、申请类型、申请单号查询维修项目申请列表
     *
     * @param equipId   设备ID
     * @param appType   申请类型
     * @param appNumber 申请单号
     * @return : java.util.List<com.yy.ppm.equipment.bean.dto.MaintProjApplyDTO>
     */
    @Override
    public List<EMaintProjApplyDTO> getMaintProjSelectList(String equipId, String appType, String appNumber, String maintInfoId) {
        return mapper.getMaintProjSelectList(equipId, appType, appNumber, maintInfoId);
    }

    @Override
    public EMaintProjApplyDTO getMaintProjApplyByAppNumber(String appNumber) {
        return mapper.getMaintProjApplyByAppNumber(appNumber);
    }

    /**
     * 根据维修项目申请id查询派工单
     *
     * @return 根据维修项目申请id查询派工单
     */

    @Override
    public int number(String mantAppNumber) {
       int count=  mapper.getCount(mantAppNumber);
        return count;
    }

    /**
     * 生成工单号：PPM + 时间戳 + 随机6位数字
     *
     * @return 工单号
     */
    private String generateWorkOrderNo() {
        // 时间戳（毫秒）
        long timestamp = System.currentTimeMillis();
        // 随机6位数字
        Random random = new Random();
        int randomNum = random.nextInt(900000) + 100000; // 生成100000-999999之间的随机数
        // 组合：PPM + 时间戳 + 随机6位数字
        return "PPM" + timestamp + randomNum;
    }
}
