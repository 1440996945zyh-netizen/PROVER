package com.yy.ppm.equipment.service.impl;

import cn.hutool.core.lang.Snowflake;
import com.yy.common.page.Pages;
import com.yy.common.util.PageHelperUtils;
import com.yy.common.util.SecurityUtils;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.auth.bean.dto.UserInfo;
import com.yy.ppm.common.enums.SerialNumberPrefixEnum;
import com.yy.ppm.common.service.impl.CommonServiceImpl;
import com.yy.ppm.equipment.bean.dto.EMaintInfoDTO;
import com.yy.ppm.equipment.bean.dto.EMaintInfoSearchDTO;
import com.yy.ppm.equipment.bean.dto.EMaintInfoBatchUpdateDTO;
import com.yy.ppm.equipment.bean.dto.EMaintPartReplaceDTO;
import com.yy.ppm.equipment.bean.dto.EMaintPartReplaceQueryDTO;
import com.yy.ppm.equipment.bean.po.EMaintInfoPO;
import com.yy.ppm.equipment.bean.po.EMaintPartReplacePO;
import com.yy.ppm.equipment.mapper.EMaintInfoMapper;
import com.yy.ppm.equipment.mapper.EMaintPartReplaceMapper;
import com.yy.ppm.equipment.service.EMaintInfoService;
import com.yy.ppm.common.service.SysFileService;
import com.yy.ppm.common.bean.dto.SysFileDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * 设备维修派工信息Service业务层处理
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

    /**
     * 查询设备维修信息列表（分页）
     */
    @Override
    public Pages<EMaintInfoDTO> getList(EMaintInfoSearchDTO searchDTO) {
        // 权限控制：如果不是超级管理员且没有EQPT_MAINT_DIS角色，只能查看自己创建或负责的记录
        UserInfo userInfo = securityUtils.getUserInfo();
        if (userInfo != null) {
            // 判断是否为超级管理员
            boolean isAdmin = "1".equals(userInfo.getIsSuperadmin());
            // 判断是否有EQPT_MAINT_DIS角色
            boolean hasMaintDisRole = false;
            if (userInfo.getRoles() != null) {
                hasMaintDisRole = userInfo.getRoles().contains("EQPT_MAINT_DIS");
            }

            // 如果不是超级管理员且没有EQPT_MAINT_DIS角色，添加权限过滤
            if (!isAdmin && !hasMaintDisRole) {
                Long loginUserId = securityUtils.getLoginUserId();
                searchDTO.setCreateBy(loginUserId);
                searchDTO.setMaintLeaderId(loginUserId);
            }
        }

        Pages<EMaintInfoDTO> pages = PageHelperUtils.limit(searchDTO, () -> {
            return mapper.selectList(searchDTO);
        });
        return pages;
    }

    /**
     * 查询设备维修提报信息列表（分页）
     */
    @Override
    public Pages<EMaintInfoDTO> listReport(EMaintInfoSearchDTO searchDTO) {
        // 权限控制：如果不是超级管理员且没有EQPT_MAINT_DIS角色，只能查看自己创建或负责的记录
        UserInfo userInfo = securityUtils.getUserInfo();
        if (userInfo != null) {
            // 判断是否为超级管理员
            boolean isAdmin = "1".equals(userInfo.getIsSuperadmin());
            // 判断是否有EQPT_MAINT_DIS角色
            boolean hasMaintDisRole = false;
            if (userInfo.getRoles() != null) {
                hasMaintDisRole = userInfo.getRoles().contains("EQPT_MAINT_DIS");
            }

            // 如果不是超级管理员且没有EQPT_MAINT_DIS角色，添加权限过滤
            if (!isAdmin && !hasMaintDisRole) {
                Long loginUserId = securityUtils.getLoginUserId();
                searchDTO.setCreateBy(loginUserId);
                searchDTO.setMaintLeaderId(loginUserId);
            }
        }

        Pages<EMaintInfoDTO> pages = PageHelperUtils.limit(searchDTO, () -> {
            return mapper.selectList(searchDTO);
        });
        return pages;
    }

    /**
     * 查询设备维修派工信息列表（分页）
     */
    @Override
    public Pages<EMaintInfoDTO> listWork(EMaintInfoSearchDTO searchDTO) {

        Pages<EMaintInfoDTO> pages = PageHelperUtils.limit(searchDTO, () -> {
            return mapper.selectList(searchDTO);
        });
        return pages;
    }

    /**
     * 根据ID查询设备维修派工信息
     */
    @Override
    public EMaintInfoDTO getById(Long id) {
        EMaintInfoDTO dto = mapper.selectById(id);
        return dto;
    }

    @Autowired
    CommonServiceImpl commonService;

    /**
     * 新增或修改设备维修派工信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public void save(EMaintInfoDTO dto) {
        // 验证必填字段
        if (dto.getEquipId() == null) {
            throw new BusinessRuntimeException("设备ID不能为空");
        }
        if (dto.getFaultFindTime() == null) {
            throw new BusinessRuntimeException("故障发现时间不能为空");
        }

        EMaintInfoPO po = new EMaintInfoPO();
        BeanUtils.copyProperties(dto, po);

        if (dto.getId() == null) {
            // 新增
            Long id = snowflake.nextId();
            po.setId(id);
            po.setNow(new Date());
            // 生成工单号：PPM + 时间戳 + 随机6位数字
            String workOrderNo = commonService.generateSerialNumber(SerialNumberPrefixEnum.REPAIR);
            po.setWorkOrderNo(workOrderNo);
            // 根据status设置：如果status为1（已派工），自动设置派工人和派工时间；如果status为0（提报），不设置
            if (dto.getStatus() != null && dto.getStatus() == 1) {
                // 派工模式：自动设置派工人和派工时间
                po.setDispatcherId(securityUtils.getLoginUserId());
                po.setDispatcherName(securityUtils.getUserInfo().getUserName());
                po.setDispatchTime(new Date());
            } else {
                // 新增模式：状态为0（提报），不设置派工人和派工时间
                if (dto.getStatus() == null) {
                    po.setStatus(0); // 默认状态为0（提报）
                }
            }
            mapper.insert(po);
            if (dto.getFaultImageIds() != null && !dto.getFaultImageIds().isEmpty()) {
                sysFileService.saveFileBusRelation(dto.getFaultImageIds(), id);
            }
        } else {
            // 修改
            // 先查询原记录状态，判断是否允许修改
            EMaintInfoDTO existingDto = mapper.selectById(dto.getId());
            if (existingDto == null) {
                throw new BusinessRuntimeException("记录不存在");
            }
            // 如果状态是维修中(2)、维修完成(4)、验收通过(5)或作废(7)，不允许修改
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
            if (dto.getFaultImageIds() != null && !dto.getFaultImageIds().isEmpty()) {
                sysFileService.saveFileBusRelation(dto.getFaultImageIds(), dto.getId());
            }
            // 保存故障图片文件关联关系
//            if (dto.getFaultImageIds() != null && !dto.getFaultImageIds().isEmpty()) {
//                sysFileService.saveFileBusRelation(dto.getFaultImageIds(), dto.getId());
//                // 先查询已有的其他业务类型的图片（维修完成图片）
//                List<SysFileDTO> existingEndImages = sysFileService.getBusFiles(dto.getId(), "MAINT_INFO_IMAGE_END");
//                List<Long> allFileIds = new ArrayList<>(dto.getFaultImageIds());
//                // 合并维修完成图片ID
//                if (existingEndImages != null && !existingEndImages.isEmpty()) {
//                    for (SysFileDTO file : existingEndImages) {
//                        allFileIds.add(file.getId());
//                    }
//                }
//                sysFileService.saveFileBusRelation(allFileIds, dto.getId());
//            } else {
//                // 如果图片ID列表为空，只删除故障图片，保留维修完成图片
//                // 先查询已有的维修完成图片
//                List<SysFileDTO> existingEndImages = sysFileService.getBusFiles(dto.getId(), "MAINT_INFO_IMAGE_END");
//                List<Long> allFileIds = new ArrayList<>();
//                // 只保留维修完成图片ID
//                if (existingEndImages != null && !existingEndImages.isEmpty()) {
//                    for (SysFileDTO file : existingEndImages) {
//                        allFileIds.add(file.getId());
//                    }
//                }
//                sysFileService.saveFileBusRelation(allFileIds, dto.getId());
//            }
        }
    }

    /**
     * 根据设备ID查询可用的出库单和申领单明细（用于配件更换选择）
     */
    @Override
    public List<EMaintPartReplaceQueryDTO> getAvailableDetailsByEquipId(Long equipId) {
        if (equipId == null) {
            throw new BusinessRuntimeException("设备ID不能为空");
        }
        return partReplaceMapper.selectAvailableDetailsByEquipId(equipId);
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
     * 删除设备维修派工信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public void deleteById(Long id) {
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

        EMaintInfoPO po = new EMaintInfoPO();
        po.setIds(ids);
        mapper.deleteByIds(po);
    }

    /**
     * 更新派工信息（只更新派工相关字段）
     */
    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public void updateDispatch(EMaintInfoDTO dto) {
        if (dto.getId() == null) {
            throw new BusinessRuntimeException("ID不能为空");
        }

        EMaintInfoPO po = new EMaintInfoPO();
        po.setId(dto.getId());
        po.setNow(new Date());

        // 只更新派工相关字段
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
        // 更新状态为1（已派工）
        if (dto.getStatus() != null) {
            po.setStatus(dto.getStatus());
        } else {
            po.setStatus(1);
        }
        // 自动设置派工人和派工时间
        po.setDispatcherId(securityUtils.getLoginUserId());
        po.setDispatcherName(securityUtils.getUserInfo().getUserName());
        po.setDispatchTime(new Date());

        mapper.update(po);
    }

    /**
     * 作废工单（批量）
     */
    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public void cancelWorkOrder(List<Long> ids, String cancelRemark) {
        if (ids == null || ids.isEmpty()) {
            throw new BusinessRuntimeException("ID列表不能为空");
        }

        // 批量查询所有记录的状态和工单号
        List<EMaintInfoDTO> records = mapper.selectByIds(ids);

        // 检查是否有记录不存在
        if (records.size() < ids.size()) {
            // 找出不存在的ID
            List<Long> foundIds = records.stream()
                    .map(EMaintInfoDTO::getId)
                    .collect(java.util.stream.Collectors.toList());
            List<Long> notFoundIds = ids.stream()
                    .filter(id -> !foundIds.contains(id))
                    .collect(java.util.stream.Collectors.toList());
            throw new BusinessRuntimeException("以下ID的记录不存在：" + notFoundIds);
        }

        // 检查是否有已作废的记录
        List<String> alreadyCanceledWorkOrders = new ArrayList<>();
        List<Long> validIds = new ArrayList<>();

        for (EMaintInfoDTO dto : records) {
            // 检查是否已经作废（状态为7）
            if (dto.getStatus() != null && dto.getStatus() == 7) {
                // 收集已作废的工单号
                if (dto.getWorkOrderNo() != null && !dto.getWorkOrderNo().isEmpty()) {
                    alreadyCanceledWorkOrders.add(dto.getWorkOrderNo());
                } else {
                    alreadyCanceledWorkOrders.add("ID:" + dto.getId());
                }
            } else {
                // 收集未作废的记录ID
                validIds.add(dto.getId());
            }
        }

        // 如果有已作废的记录，抛出异常提示
        if (!alreadyCanceledWorkOrders.isEmpty()) {
            String workOrderList = String.join("、", alreadyCanceledWorkOrders);
            throw new BusinessRuntimeException("以下工单已经作废，不能重复作废：" + workOrderList);
        }

        // 批量更新未作废的记录
        if (!validIds.isEmpty()) {
            EMaintInfoBatchUpdateDTO updateDTO = new EMaintInfoBatchUpdateDTO();
            updateDTO.setIds(validIds);
            updateDTO.setStatus(7);
            // 设置作废人和作废时间
            updateDTO.setLoginUserId(securityUtils.getLoginUserId());
            updateDTO.setLoginUserName(securityUtils.getUserInfo().getUserName());
            updateDTO.setNow(new Date()); // Set current time for CANCEL_TIME and UPDATE_TIME
            updateDTO.setCancelRemark(cancelRemark);
            mapper.batchUpdateStatusToCanceled(updateDTO);
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

        // 查询记录
        EMaintInfoDTO dto = mapper.selectById(id);
        if (dto == null) {
            throw new BusinessRuntimeException("记录不存在");
        }
        if (dto.getStatus() == null || dto.getStatus() != 1) {
            throw new BusinessRuntimeException("只有已派工状态的记录才能开始维修");
        }
        // 更新状态为维修中（2），并设置维修开始时间
        EMaintInfoPO po = new EMaintInfoPO();
        po.setId(id);
        po.setStatus(2); // 维修中
        po.setMaintStartTime(maintStartTime);
        mapper.update(po);
    }

    /**
     * 结束维修
     */
    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public void endMaintenance(Long id, Date maintEndTime, List<Long> imageIds, String maintRemark, List<EMaintPartReplaceDTO> partReplaceList) {
        if (id == null) {
            throw new BusinessRuntimeException("ID不能为空");
        }
        if (maintEndTime == null) {
            throw new BusinessRuntimeException("结束维修时间不能为空");
        }

        // 查询记录
        EMaintInfoDTO dto = mapper.selectById(id);
        if (dto == null) {
            throw new BusinessRuntimeException("记录不存在");
        }
        if (dto.getStatus() == null || dto.getStatus() != 2) {
            throw new BusinessRuntimeException("只有维修中状态的记录才能结束维修");
        }

        // 更新状态为维修完成（4），并设置维修结束时间和维修说明
        EMaintInfoPO po = new EMaintInfoPO();
        po.setId(id);
        po.setStatus(4); // 维修完成
        po.setMaintEndTime(maintEndTime); // 使用前端传递的结束维修时间
        po.setMaintRemark(maintRemark); // 维修说明
        mapper.update(po);

        // 保存配件更换列表
        // 先删除该维修信息下的旧配件更换记录（物理删除）
        partReplaceMapper.deleteByMaintInfoId(id);
        // 如果有新的配件更换记录，则保存
        if (partReplaceList != null && !partReplaceList.isEmpty()) {
            for (EMaintPartReplaceDTO partReplaceDTO : partReplaceList) {
                if (partReplaceDTO.getUsedQuantity() != null && partReplaceDTO.getUsedQuantity().compareTo(java.math.BigDecimal.ZERO) > 0) {
                    EMaintPartReplacePO partReplacePO = new EMaintPartReplacePO();
                    BeanUtils.copyProperties(partReplaceDTO, partReplacePO);
                    partReplacePO.setId(snowflake.nextId());
                    partReplacePO.setMaintInfoId(id);
                    partReplacePO.setEquipId(dto.getEquipId());
                    partReplaceMapper.insert(partReplacePO);
                }
            }
        }

        // 保存维修完成图片
        if (imageIds != null && !imageIds.isEmpty()) {
            // 先查询已有的其他业务类型的图片（故障图片）
            List<SysFileDTO> existingFaultImages = sysFileService.getBusFiles(id, "MAINT_INFO_IMAGE");
            List<Long> allFileIds = new ArrayList<>(imageIds);
            // 合并故障图片ID
            if (existingFaultImages != null && !existingFaultImages.isEmpty()) {
                for (SysFileDTO file : existingFaultImages) {
                    allFileIds.add(file.getId());
                }
            }
            sysFileService.saveFileBusRelation(allFileIds, id);
        }
    }

    /**
     * 验收通过
     */
    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public void acceptMaintenance(Long id, String acceptanceRemark) {
        if (id == null) {
            throw new BusinessRuntimeException("ID不能为空");
        }

        // 查询记录
        EMaintInfoDTO dto = mapper.selectById(id);
        if (dto == null) {
            throw new BusinessRuntimeException("记录不存在");
        }
        if (dto.getStatus() == null || dto.getStatus() != 4) {
            throw new BusinessRuntimeException("只有维修完成状态的记录才能验收");
        }

        // 更新状态为验收通过（5），并设置验收人、验收时间和验收备注
        Date now = new Date();
        EMaintInfoPO po = new EMaintInfoPO();
        po.setId(id);
        po.setStatus(5); // 验收通过
        po.setAccepterId(securityUtils.getLoginUserId());
        po.setAccepterName(securityUtils.getUserInfo().getUserName());
        po.setAcceptanceTime(now);
        po.setAcceptanceRemark(acceptanceRemark);
        mapper.update(po);
    }

    /**
     * 生成工单号：PPM + 时间戳 + 随机6位数字
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

