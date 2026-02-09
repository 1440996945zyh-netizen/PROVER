package com.yy.ppm.equipment.service.impl;

import cn.hutool.core.lang.Snowflake;
import com.yy.common.page.Pages;
import com.yy.common.util.PageHelperUtils;
import com.yy.common.util.SecurityUtils;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.auth.bean.dto.UserInfo;
import com.yy.ppm.common.enums.SerialNumberPrefixEnum;
import com.yy.ppm.common.service.impl.CommonServiceImpl;
import com.yy.ppm.equipment.bean.dto.EMaterialApplicationDTO;
import com.yy.ppm.equipment.bean.dto.EMaterialApplicationDetailDTO;
import com.yy.ppm.equipment.bean.dto.EMaterialApplicationDetailSearchDTO;
import com.yy.ppm.equipment.bean.dto.EMaterialApplicationSearchDTO;
import com.yy.ppm.equipment.bean.po.EMaterialApplicationPO;
import com.yy.ppm.equipment.bean.po.EMaterialApplicationDetailPO;
import com.yy.ppm.equipment.mapper.EMaterialApplicationDetailMapper;
import com.yy.ppm.equipment.mapper.EMaterialApplicationMapper;
import com.yy.ppm.equipment.mapper.EMaterialWarehouseInDetailMapper;
import com.yy.ppm.equipment.service.EMaterialApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 物资申报Service业务层处理
 * @author system
 */
@RequiredArgsConstructor
@Service
public class EMaterialApplicationServiceImpl implements EMaterialApplicationService {

    @Resource
    private EMaterialApplicationMapper mapper;

    @Resource
    private EMaterialApplicationDetailMapper detailMapper;

    @Resource
    private EMaterialWarehouseInDetailMapper warehouseInDetailMapper;

    @Resource
    private Snowflake snowflake;

    @Resource
    private SecurityUtils securityUtils;

    /**
     * 查询物资申报列表（分页）
     */
    @Override
    public Pages<EMaterialApplicationDTO> getList(EMaterialApplicationSearchDTO searchDTO) {
        // 权限控制：管理员能查看全部，否则只能查看同部门的数据
        UserInfo userInfo = securityUtils.getUserInfo();
        if (userInfo != null) {
            // 判断是否为超级管理员
            boolean isAdmin = "1".equals(userInfo.getIsSuperadmin());
            // 如果不是管理员，强制使用当前用户的部门ID进行过滤
            if (!isAdmin && userInfo.getDeptId() != null) {
                searchDTO.setDeptId(userInfo.getDeptId());
            }
        }
        return PageHelperUtils.limit(searchDTO, () -> mapper.selectList(searchDTO));
    }

    /**
     * 根据ID查询物资申报（包含明细）
     */
    @Override
    public EMaterialApplicationDTO getById(Long id) {
        EMaterialApplicationDTO dto = mapper.selectById(id);
        if (dto != null) {
            // 查询明细列表
            List<EMaterialApplicationDetailDTO> detailList = detailMapper.selectListByApplicationId(id);
            dto.setDetailList(detailList);
        }
        return dto;
    }

    @Autowired
    CommonServiceImpl commonService;
    /**
     * 新增或修改物资申报
     */
    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public void save(EMaterialApplicationDTO dto) {
        EMaterialApplicationPO po = new EMaterialApplicationPO();
        BeanUtils.copyProperties(dto, po);

        // 自动赋值部门信息（新增和修改都自动赋值，不允许前端修改）
        if (securityUtils.getUserInfo() != null) {
            po.setDeptId(securityUtils.getUserInfo().getDeptId());
            po.setDeptName(securityUtils.getUserInfo().getDeptName());
        }

        // 设置状态（如果前端没有传，新增时默认为暂存）
        if (dto.getStatus() == null || dto.getStatus().trim().isEmpty()) {
            po.setStatus("1"); // 默认暂存
        } else {
            po.setStatus(dto.getStatus());
        }

        if (dto.getId() == null) {
            // 新增
            // 自动生成申请单号：DE + 时间戳 + 6位随机数
            String applicationNo = commonService.generateSerialNumber(SerialNumberPrefixEnum.APPLICATION);
            
            // 验重：检查申请单号是否已存在
            int count = mapper.countByApplicationNo(applicationNo, null);
            int maxRetries = 10; // 最多重试10次
            int retries = 0;
            while (count > 0 && retries < maxRetries) {
                // 如果申请单号已存在，重新生成
                applicationNo = commonService.generateSerialNumber(SerialNumberPrefixEnum.APPLICATION);
                count = mapper.countByApplicationNo(applicationNo, null);
                retries++;
            }
            if (count > 0) {
                throw new BusinessRuntimeException("申请单号生成失败，请重试");
            }
            
            po.setApplicationNo(applicationNo);
            dto.setApplicationNo(applicationNo);
            // 验证申报主题
            if (dto.getApplicationTitle() == null || dto.getApplicationTitle().trim().isEmpty()) {
                throw new BusinessRuntimeException("申报主题不能为空");
            }
            po.setId(snowflake.nextId());
            mapper.insert(po);
            dto.setId(po.getId());
            // 新增时保存明细
            if (dto.getDetailList() != null && !dto.getDetailList().isEmpty()) {
                for (EMaterialApplicationDetailDTO detailDTO : dto.getDetailList()) {
                    EMaterialApplicationDetailPO detailPO = new EMaterialApplicationDetailPO();
                    BeanUtils.copyProperties(detailDTO, detailPO);
                    detailPO.setApplicationId(dto.getId());
                    // 新增明细
                    detailPO.setId(snowflake.nextId());
                    detailMapper.insert(detailPO);
                }
            }
        } else {
            // 修改
            // 验证申报主题
            if (dto.getApplicationTitle() == null || dto.getApplicationTitle().trim().isEmpty()) {
                throw new BusinessRuntimeException("申报主题不能为空");
            }
            
            // 如果是驳回状态修改，且新状态不是驳回，则清空审核信息
            EMaterialApplicationDTO existingDto = mapper.selectById(dto.getId());
            if (existingDto != null && "4".equals(existingDto.getStatus()) && !"4".equals(po.getStatus())) {
                // 清空审核信息
                po.setApprovalRemark(null);
                po.setApprovalBy(null);
                po.setApprovalByName(null);
                po.setApprovalTime(null);
            }
            
            mapper.update(po);
            // 处理明细：新增、修改、删除
            saveDetailList(dto.getId(), dto.getDetailList());
        }


    }

    /**
     * 删除物资申报
     */
    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public void deleteById(Long id) {
        // 先删除明细
        detailMapper.deleteByApplicationId(id);
        // 再删除主表
        mapper.deleteById(id);
    }

    /**
     * 保存明细列表（修改时使用）
     * 分别处理新增、修改、删除
     */
    private void saveDetailList(Long applicationId, List<EMaterialApplicationDetailDTO> detailList) {
        // 查询数据库中原有的明细ID列表
        List<EMaterialApplicationDetailDTO> existingDetails = detailMapper.selectListByApplicationId(applicationId);
        Set<Long> existingIds = existingDetails.stream()
                .map(EMaterialApplicationDetailDTO::getId)
                .collect(Collectors.toSet());

        // 获取前端传来的明细ID列表
        Set<Long> incomingIds = detailList != null ? detailList.stream()
                .filter(detail -> detail.getId() != null)
                .map(EMaterialApplicationDetailDTO::getId)
                .collect(Collectors.toSet()) : Set.of();

        // 找出需要删除的明细（数据库有但前端没有传）
        Set<Long> idsToDelete = existingIds.stream()
                .filter(id -> !incomingIds.contains(id))
                .collect(Collectors.toSet());

        // 删除明细
        for (Long id : idsToDelete) {
            detailMapper.deleteById(id);
        }

        // 处理前端传来的明细
        if (detailList != null && !detailList.isEmpty()) {
            for (EMaterialApplicationDetailDTO detailDTO : detailList) {
                EMaterialApplicationDetailPO detailPO = new EMaterialApplicationDetailPO();
                BeanUtils.copyProperties(detailDTO, detailPO);
                detailPO.setApplicationId(applicationId);

                if (detailPO.getId() == null) {
                    // 新增明细
                    detailPO.setId(snowflake.nextId());
                    detailMapper.insert(detailPO);
                } else {
                    // 修改明细
                    detailMapper.update(detailPO);
                }
            }
        }
    }

    /**
     * 生成申请单号：DE + 时间戳 + 6位随机数
     */
    private String generateApplicationNo() {
        long timestamp = System.currentTimeMillis();
        Random random = new Random();
        int randomNum = random.nextInt(900000) + 100000; // 生成6位随机数（100000-999999）
        return "DE" + timestamp + randomNum;
    }

    /**
     * 审批物资申报
     */
    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public void approve(Long id, String status, String approvalRemark) {
        // 验证状态（只能是3-审批通过或4-驳回）
        if (!"3".equals(status) && !"4".equals(status)) {
            throw new BusinessRuntimeException("审批状态不正确，只能是审批通过(3)或驳回(4)");
        }

        // 查询申报记录
        EMaterialApplicationDTO dto = mapper.selectById(id);
        if (dto == null) {
            throw new BusinessRuntimeException("申报记录不存在");
        }

        // 验证当前状态必须是等待审批(2)
        if (!"2".equals(dto.getStatus())) {
            throw new BusinessRuntimeException("当前状态不允许审批，只有等待审批状态的记录才能审批");
        }
        // 更新审批信息
        EMaterialApplicationPO po = new EMaterialApplicationPO();
        po.setId(id);
        po.setStatus(status);
        po.setApprovalRemark(approvalRemark);
        // 执行审批
        mapper.approve(po);
    }

    @Override
    public Pages<EMaterialApplicationDetailDTO> getDetailListForPurchase(EMaterialApplicationDetailSearchDTO searchDTO) {
        return PageHelperUtils.limit(searchDTO, () -> detailMapper.selectDetailListForPurchase(searchDTO));
    }

    @Override
    public Pages<com.yy.ppm.equipment.bean.dto.EMaterialApplicationDetailForWarehouseInDTO> getDetailListForWarehouseIn(com.yy.ppm.equipment.bean.dto.EMaterialApplicationDetailForWarehouseInSearchDTO searchDTO) {
        return PageHelperUtils.limit(searchDTO, () -> detailMapper.selectDetailListForWarehouseIn(searchDTO));
    }

    /**
     * 查询物资申报主表列表（包含明细列表，用于出库申请时选择）
     * 只查询审核通过的申报（状态为'3'）
     */
    @Override
    public Pages<EMaterialApplicationDTO> getListWithDetails(EMaterialApplicationSearchDTO searchDTO) {
        // 权限控制：管理员能查看全部，否则只能查看自己创建的
        UserInfo userInfo = securityUtils.getUserInfo();
        if (userInfo != null) {
            // 判断是否为超级管理员
            boolean isAdmin = "1".equals(userInfo.getIsSuperadmin());
            // 如果不是管理员，只查询自己创建的申报
            if (!isAdmin && userInfo.getId() != null) {
                searchDTO.setCreateBy(userInfo.getId());
            }
        }
        
        // 只查询审核通过的申报（状态为'3'）
        searchDTO.setStatus("3");
        
        // 使用一个SQL查询主表和明细（包含库存数量）
        Pages<EMaterialApplicationDTO> result = PageHelperUtils.limit(searchDTO, () -> mapper.selectListWithDetails(searchDTO));
        
        return result;
    }
}

