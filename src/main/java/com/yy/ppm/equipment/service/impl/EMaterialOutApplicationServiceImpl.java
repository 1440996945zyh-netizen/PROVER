package com.yy.ppm.equipment.service.impl;

import cn.hutool.core.lang.Snowflake;
import com.yy.common.page.Pages;
import com.yy.common.util.PageHelperUtils;
import com.yy.common.util.SecurityUtils;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.common.enums.SerialNumberPrefixEnum;
import com.yy.ppm.common.service.impl.CommonServiceImpl;
import com.yy.ppm.auth.bean.dto.UserInfo;
import com.yy.ppm.equipment.bean.dto.EMaterialOutApplicationDTO;
import com.yy.ppm.equipment.bean.dto.EMaterialOutApplicationDetailDTO;
import com.yy.ppm.equipment.bean.dto.EMaterialOutApplicationSearchDTO;
import com.yy.ppm.equipment.bean.po.EMaterialOutApplicationPO;
import com.yy.ppm.equipment.bean.po.EMaterialOutApplicationDetailPO;
import com.yy.ppm.equipment.mapper.EMaterialOutApplicationDetailMapper;
import com.yy.ppm.equipment.mapper.EMaterialOutApplicationMapper;
import com.yy.ppm.equipment.service.EMaterialOutApplicationService;
import com.yy.ppm.equipment.service.EMaterialWarehouseInService;
import com.yy.ppm.flowable.bean.dto.BpmProcessInstanceDTO;
import com.yy.ppm.flowable.service.BpmProcessInstanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.util.StringUtils;

import static com.yy.common.util.SecurityUtils.getLoginUserId;

/**
 * 物资出库申请Service业务层处理
 * @author system
 */
@RequiredArgsConstructor
@Service
public class EMaterialOutApplicationServiceImpl implements EMaterialOutApplicationService {

    @Resource
    private EMaterialOutApplicationMapper mapper;

    @Resource
    private EMaterialOutApplicationDetailMapper detailMapper;

    @Resource
    private Snowflake snowflake;

    @Resource
    private SecurityUtils securityUtils;

    @Autowired
    CommonServiceImpl commonService;

    @Resource
    BpmProcessInstanceService bpmProcessInstanceService;

    @Resource
    private EMaterialWarehouseInService warehouseInService;

    /**
     * 查询物资出库申请列表（分页）
     */
    @Override
    public Pages<EMaterialOutApplicationDTO> getList(EMaterialOutApplicationSearchDTO searchDTO) {
        return PageHelperUtils.limit(searchDTO, () -> mapper.selectList(searchDTO));
    }

    /**
     * 根据ID查询物资出库申请（包含明细）
     */
    @Override
    public EMaterialOutApplicationDTO getById(Long id) {
        EMaterialOutApplicationDTO dto = mapper.selectById(id);
        if (dto != null) {
            // 查询明细列表
            List<EMaterialOutApplicationDetailDTO> detailList = detailMapper.selectListByOutApplicationId(id);
            dto.setDetailList(detailList);
        }
        return dto;
    }

    /**
     * 新增或修改物资出库申请
     */
    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public void save(EMaterialOutApplicationDTO dto) {
        EMaterialOutApplicationPO po = new EMaterialOutApplicationPO();
        BeanUtils.copyProperties(dto, po);

        // 设置状态（如果前端没有传，新增时默认为暂存）
        if (dto.getStatus() == null || dto.getStatus().trim().isEmpty()) {
            po.setStatus("1"); // 默认暂存（未审核）
        } else {
            po.setStatus(dto.getStatus());
        }

        if (dto.getId() == null) {
            // 新增
            // 自动生成单号
            String warehouseOutNo = commonService.generateSerialNumber(SerialNumberPrefixEnum.WAREHOUSE_OUT_APP);

            // 验重：检查单号是否已存在
            int count = mapper.countByWarehouseOutNo(warehouseOutNo, null);
            int maxRetries = 10; // 最多重试10次
            int retries = 0;
            while (count > 0 && retries < maxRetries) {
                // 如果单号已存在，重新生成
                warehouseOutNo = commonService.generateSerialNumber(SerialNumberPrefixEnum.WAREHOUSE_OUT_APP);
                count = mapper.countByWarehouseOutNo(warehouseOutNo, null);
                retries++;
            }
            if (count > 0) {
                throw new BusinessRuntimeException("出库申请单号生成失败，请重试");
            }

            po.setWarehouseOutNo(warehouseOutNo);
            dto.setWarehouseOutNo(warehouseOutNo);
            // 验证出库主题
            if (dto.getWarehouseOutTitle() == null || dto.getWarehouseOutTitle().trim().isEmpty()) {
                throw new BusinessRuntimeException("出库申请主题不能为空");
            }
            // 验证仓库
            if (dto.getWarehouseId() == null) {
                throw new BusinessRuntimeException("仓库不能为空");
            }
            // 验证明细：合并相同物资的申请数量，并验证不超过库存数量
            validateAndMergeDetails(dto.getDetailList(), dto.getWarehouseId(), null);
            po.setId(snowflake.nextId());
            mapper.insert(po);
            dto.setId(po.getId());
            // 新增时保存明细
            if (dto.getDetailList() != null && !dto.getDetailList().isEmpty()) {
                for (EMaterialOutApplicationDetailDTO detailDTO : dto.getDetailList()) {
                    EMaterialOutApplicationDetailPO detailPO = new EMaterialOutApplicationDetailPO();
                    BeanUtils.copyProperties(detailDTO, detailPO);
                    detailPO.setOutApplicationId(dto.getId());
                    // 转换 equipIds 和 equipNames（从数组转为逗号分隔的字符串）
                    convertEquipIdsAndNames(detailPO);
                    // 新增明细
                    detailPO.setId(snowflake.nextId());
                    detailMapper.insert(detailPO);
                }
            }
        } else {
            // 修改
            // 验证出库主题
            if (dto.getWarehouseOutTitle() == null || dto.getWarehouseOutTitle().trim().isEmpty()) {
                throw new BusinessRuntimeException("出库申请主题不能为空");
            }
            // 验证仓库
            if (dto.getWarehouseId() == null) {
                throw new BusinessRuntimeException("仓库不能为空");
            }

            // 如果是驳回状态修改，则清空审批信息
            EMaterialOutApplicationDTO existingDto = mapper.selectById(dto.getId());
            if (existingDto != null && "4".equals(existingDto.getStatus())) {
                // 清空审批信息（强制设置为null，确保MyBatis能正确识别）
                po.setAuditRemark(null);
                po.setAcceptancePersonId(null);
                po.setAcceptancePersonName(null);
                po.setAcceptanceTime(null);
            }

            // 验证明细：合并相同物资的申请数量，并验证不超过库存数量
            validateAndMergeDetails(dto.getDetailList(), dto.getWarehouseId(), dto.getId());

            mapper.update(po);
            // 处理明细：新增、修改、删除
            saveDetailList(dto.getId(), dto.getDetailList());
        }
    }

    /**
     * 删除物资出库申请
     */
    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public void deleteById(Long id) {
        EMaterialOutApplicationPO po = new EMaterialOutApplicationPO();
        po.setId(id);
        // 先删除明细（逻辑删除）
        detailMapper.deleteByOutApplicationId(po);
        // 再删除主表（逻辑删除）
        mapper.deleteById(po);
    }

    /**
     * 保存明细列表（修改时使用）
     * 分别处理新增、修改、删除
     */
    private void saveDetailList(Long outApplicationId, List<EMaterialOutApplicationDetailDTO> detailList) {
        // 查询数据库中原有的明细ID列表
        List<EMaterialOutApplicationDetailDTO> existingDetails = detailMapper.selectListByOutApplicationId(outApplicationId);
        Set<Long> existingIds = existingDetails.stream()
                .map(EMaterialOutApplicationDetailDTO::getId)
                .collect(Collectors.toSet());

        // 获取前端传来的明细ID列表
        Set<Long> incomingIds = detailList != null ? detailList.stream()
                .filter(detail -> detail.getId() != null)
                .map(EMaterialOutApplicationDetailDTO::getId)
                .collect(Collectors.toSet()) : Set.of();

        // 找出需要删除的明细（数据库有但前端没有传）
        Set<Long> idsToDelete = existingIds.stream()
                .filter(id -> !incomingIds.contains(id))
                .collect(Collectors.toSet());

        // 删除明细（逻辑删除）
        for (Long id : idsToDelete) {
            EMaterialOutApplicationDetailDTO dto = new EMaterialOutApplicationDetailDTO();
            dto.setId(id);
            detailMapper.deleteById(dto);
        }

        // 处理前端传来的明细
        if (detailList != null && !detailList.isEmpty()) {
            for (EMaterialOutApplicationDetailDTO detailDTO : detailList) {
                EMaterialOutApplicationDetailPO detailPO = new EMaterialOutApplicationDetailPO();
                BeanUtils.copyProperties(detailDTO, detailPO);
                detailPO.setOutApplicationId(outApplicationId);
                // 转换 equipIds 和 equipNames（从数组转为逗号分隔的字符串）
                convertEquipIdsAndNames(detailPO);

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
     * 转换 equipIds（从数组转为逗号分隔的字符串）
     * 由于前端已经转换为字符串，这里主要处理异常情况
     * 如果已经是字符串，则保持不变
     */
    private void convertEquipIdsAndNames(EMaterialOutApplicationDetailPO detailPO) {
        if (detailPO.getEquipIds() != null && !(detailPO.getEquipIds() instanceof String)) {
            detailPO.setEquipIds(detailPO.getEquipIds().toString());
        }
        if (detailPO.getEquipIds() != null && detailPO.getEquipIds().trim().isEmpty()) {
            detailPO.setEquipIds(null);
        }
    }

    /**
     * 验证并合并明细：合并相同物资的申请数量，并验证不超过库存数量
     * @param detailList 明细列表
     * @param warehouseId 仓库ID
     * @param excludeOutApplicationId 排除的出库申请ID（编辑时排除当前申请）
     */
    private void validateAndMergeDetails(List<EMaterialOutApplicationDetailDTO> detailList, Long warehouseId, Long excludeOutApplicationId) {
        if (detailList == null || detailList.isEmpty()) {
            return;
        }

        // 按物资ID分组，合并相同物资的申请数量
        java.util.Map<Long, java.math.BigDecimal> materialQuantityMap = new java.util.HashMap<>();
        java.util.Map<Long, String> materialNameMap = new java.util.HashMap<>();

        for (EMaterialOutApplicationDetailDTO detail : detailList) {
            if (detail.getMaterialId() == null) {
                continue;
            }
            Long materialId = detail.getMaterialId();
            java.math.BigDecimal quantity = detail.getApplicationQuantity();
            if (quantity == null) {
                quantity = java.math.BigDecimal.ZERO;
            }

            // 累加相同物资的申请数量
            materialQuantityMap.put(materialId, materialQuantityMap.getOrDefault(materialId, java.math.BigDecimal.ZERO).add(quantity));
            if (detail.getMaterialName() != null && !materialNameMap.containsKey(materialId)) {
                materialNameMap.put(materialId, detail.getMaterialName());
            }
        }

        // 验证每个物资的申请数量 + 已出库数量 <= 库存数量
        for (java.util.Map.Entry<Long, java.math.BigDecimal> entry : materialQuantityMap.entrySet()) {
            Long materialId = entry.getKey();
            java.math.BigDecimal totalApplicationQuantity = entry.getValue();

            // 查询库存数量
            java.math.BigDecimal stockQuantity = warehouseInService.getStockQuantity(materialId, warehouseId);
            if (stockQuantity == null) {
                stockQuantity = java.math.BigDecimal.ZERO;
            }

            // 查询已出库数量（已审批通过的出库申请明细的申请数量总和）
            java.math.BigDecimal outQuantity = detailMapper.selectOutQuantityByMaterialAndWarehouse(materialId, warehouseId, excludeOutApplicationId);
            if (outQuantity == null) {
                outQuantity = java.math.BigDecimal.ZERO;
            }

            // 验证：合并后的申请数量 + 已出库数量 <= 库存数量
            java.math.BigDecimal totalQuantity = totalApplicationQuantity.add(outQuantity);
            if (totalQuantity.compareTo(stockQuantity) > 0) {
                String materialName = materialNameMap.getOrDefault(materialId, "未知物资");
                throw new BusinessRuntimeException(
                    String.format("物资【%s】的申请数量（%s）+ 已申请数量（%s）= %s，超过了库存数量（%s）",
                        materialName,
                        totalApplicationQuantity,
                        outQuantity,
                        totalQuantity,
                        stockQuantity
                    )
                );
            }
        }
    }

    /**
     * 审核物资出库申请
     */
    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public void audit(Long id, String status, String auditRemark) {
        // 验证审核状态（3-审批通过，4-驳回）
        if (status == null || (!status.equals("3") && !status.equals("4"))) {
            throw new BusinessRuntimeException("审核状态无效");
        }
        // 查询出库申请
        EMaterialOutApplicationDTO dto = mapper.selectById(id);
        if (dto == null) {
            throw new BusinessRuntimeException("出库申请不存在");
        }
        // 更新状态、审核备注和验收人信息
        EMaterialOutApplicationPO po = new EMaterialOutApplicationPO();
        po.setId(id);
        po.setStatus(status);
        po.setAuditRemark(auditRemark);
        // 设置验收人信息（审核人就是验收人）
        po.setAcceptancePersonId(securityUtils.getLoginUserId());
        po.setAcceptancePersonName(securityUtils.getLoginUserName());
        po.setAcceptanceTime(new java.util.Date());
        mapper.update(po);
    }

    /**
     * 查询物资出库申请列表（包含明细列表和库存数量，用于出库时选择）
     * 只查询审核通过的申请（状态为'3'）
     */
    @Override
    public Pages<EMaterialOutApplicationDTO> getListWithDetails(EMaterialOutApplicationSearchDTO searchDTO) {
        // 权限控制：管理员能查看全部，否则只能查看自己创建的
//        UserInfo userInfo = securityUtils.getUserInfo();
//        if (userInfo != null) {
//            // 判断是否为超级管理员
//            boolean isAdmin = "1".equals(userInfo.getIsSuperadmin());
//            // 如果不是管理员，只查询自己创建的申请
//            if (!isAdmin && userInfo.getId() != null) {
//                searchDTO.setCreateBy(userInfo.getId());
//            }
//        }

        // 只查询审核通过的申请（状态为'2'）
        searchDTO.setStatus("2");

        // 使用一个SQL查询主表和明细（包含库存数量）
        Pages<EMaterialOutApplicationDTO> result = PageHelperUtils.limit(searchDTO, () -> mapper.selectListWithDetails(searchDTO));

        return result;
    }



    /**
     * 提交发起流程
     */
    @Override
    public void submit(BpmProcessInstanceDTO dto) {
        // 调用流程实例发起
        bpmProcessInstanceService.createProcessInstance(getLoginUserId(), dto);
    }
}

