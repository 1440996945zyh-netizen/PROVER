package com.yy.ppm.equipment.service.impl;

import cn.hutool.core.lang.Snowflake;
import com.yy.common.page.Pages;
import com.yy.common.util.PageHelperUtils;
import com.yy.common.util.SecurityUtils;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.auth.bean.dto.UserInfo;
import com.yy.ppm.common.enums.SerialNumberPrefixEnum;
import com.yy.ppm.common.service.impl.CommonServiceImpl;
import com.yy.ppm.equipment.bean.dto.EMaterialPurchaseDTO;
import com.yy.ppm.equipment.bean.dto.EMaterialPurchaseDetailDTO;
import com.yy.ppm.equipment.bean.dto.EMaterialPurchaseSearchDTO;
import com.yy.ppm.equipment.bean.dto.EMaterialPurchaseComparisonDTO;
import com.yy.ppm.equipment.bean.po.EMaterialPurchasePO;
import com.yy.ppm.equipment.bean.po.EMaterialPurchaseDetailPO;
import com.yy.ppm.equipment.bean.po.EMaterialPurchaseComparisonPO;
import com.yy.ppm.equipment.mapper.EMaterialPurchaseDetailMapper;
import com.yy.ppm.equipment.mapper.EMaterialPurchaseMapper;
import com.yy.ppm.equipment.mapper.EMaterialApplicationDetailMapper;
import com.yy.ppm.equipment.mapper.EMaterialPurchaseComparisonMapper;
import com.yy.ppm.equipment.mapper.EMaterialWarehouseInDetailMapper;
import com.yy.ppm.equipment.service.EMaterialPurchaseService;
import com.yy.ppm.flowable.bean.dto.BpmProcessInstanceDTO;
import com.yy.ppm.flowable.service.BpmProcessInstanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import static com.yy.common.util.SecurityUtils.getLoginUserId;

/**
 * 物资采购Service业务层处理
 * @author system
 */
@RequiredArgsConstructor
@Service
public class EMaterialPurchaseServiceImpl implements EMaterialPurchaseService {

    @Resource
    private EMaterialPurchaseMapper mapper;

    @Resource
    private EMaterialPurchaseDetailMapper detailMapper;

    @Autowired
    private SecurityUtils securityUtils;

    @Resource
    private EMaterialApplicationDetailMapper applicationDetailMapper;

    @Resource
    private EMaterialPurchaseComparisonMapper comparisonMapper;

    @Resource
    private EMaterialWarehouseInDetailMapper warehouseInDetailMapper;

    @Resource
    BpmProcessInstanceService bpmProcessInstanceService;

    @Resource
    private Snowflake snowflake;

    /**
     * 查询物资采购列表（分页）
     */
    @Override
    public Pages<EMaterialPurchaseDTO> getList(EMaterialPurchaseSearchDTO searchDTO) {
        return PageHelperUtils.limit(searchDTO, () -> mapper.selectList(searchDTO));
    }

    /**
     * 根据ID查询物资采购（包含明细）
     */
    @Override
    public EMaterialPurchaseDTO getById(Long id) {
        EMaterialPurchaseDTO dto = mapper.selectById(id);
        if (dto != null) {
            // 查询明细列表
            List<EMaterialPurchaseDetailDTO> detailList = detailMapper.selectListByPurchaseId(id);
            dto.setDetailList(detailList);
            // 查询比价信息列表（仅当采购类型为比价时）
            if ("01".equals(dto.getPurchaseTypeCode())) {
                List<EMaterialPurchaseComparisonDTO> comparisonList = comparisonMapper.selectListByPurchaseId(id);
                dto.setComparisonList(comparisonList);
            }
        }
        return dto;
    }
    @Autowired
    CommonServiceImpl commonService;

    /**
     * 新增或修改物资采购
     */
    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public void save(EMaterialPurchaseDTO dto) {
        EMaterialPurchasePO po = new EMaterialPurchasePO();
        BeanUtils.copyProperties(dto, po);

        // 设置采购状态（如果前端没有传，新增时默认为0-待审核）
        if (dto.getPurchaseStatus() == null) {
            po.setPurchaseStatus(0);
        } else {
            po.setPurchaseStatus(dto.getPurchaseStatus());
        }

        if (dto.getId() == null) {
            // 新增
            // 自动生成采购单号
            String purchaseNo = commonService.generateSerialNumber(SerialNumberPrefixEnum.PURCHASE);

            // 验证采购单号是否重复，最多重试10次
            int count = mapper.countByPurchaseNo(purchaseNo, null);
            int maxRetries = 10; // 最多重试10次
            int retries = 0;
            while (count > 0 && retries < maxRetries) {
                // 如果单号已存在，重新生成
                purchaseNo = commonService.generateSerialNumber(SerialNumberPrefixEnum.PURCHASE);
                count = mapper.countByPurchaseNo(purchaseNo, null);
                retries++;
            }
            if (count > 0) {
                throw new BusinessRuntimeException("采购单号生成失败，请重试");
            }

            po.setPurchaseNo(purchaseNo);
            dto.setPurchaseNo(purchaseNo);
            // 验证采购单主题
            if (dto.getPurchaseTitle() == null || dto.getPurchaseTitle().trim().isEmpty()) {
                throw new BusinessRuntimeException("采购单主题不能为空");
            }
            po.setId(snowflake.nextId());
            mapper.insert(po);
            dto.setId(po.getId());
            // 新增时保存明细
            if (dto.getDetailList() != null && !dto.getDetailList().isEmpty()) {
                List<EMaterialPurchaseDetailPO> detailPOList = new ArrayList<>();
                for (EMaterialPurchaseDetailDTO detailDTO : dto.getDetailList()) {
                    EMaterialPurchaseDetailPO detailPO = new EMaterialPurchaseDetailPO();
                    BeanUtils.copyProperties(detailDTO, detailPO);
                    detailPO.setPurchaseId(dto.getId());
                    // 新增明细
                    detailPO.setId(snowflake.nextId());
                    detailPOList.add(detailPO);
                }
                // 批量插入明细
                if (!detailPOList.isEmpty()) {
                    detailMapper.batchInsert(detailPOList);
                    // 注意：更新申报明细的采购明细ID操作已移到审核通过时执行
                }
            }
            // 新增时保存比价信息（仅当采购类型为比价时）
            if ("01".equals(dto.getPurchaseTypeCode()) && dto.getComparisonList() != null && !dto.getComparisonList().isEmpty()) {
                List<EMaterialPurchaseComparisonPO> comparisonPOList = new ArrayList<>();
                for (EMaterialPurchaseComparisonDTO comparisonDTO : dto.getComparisonList()) {
                    EMaterialPurchaseComparisonPO comparisonPO = new EMaterialPurchaseComparisonPO();
                    BeanUtils.copyProperties(comparisonDTO, comparisonPO);
                    comparisonPO.setPurchaseId(dto.getId());
                    comparisonPO.setId(snowflake.nextId());
                    comparisonPOList.add(comparisonPO);
                }
                // 批量插入比价信息
                if (!comparisonPOList.isEmpty()) {
                    comparisonMapper.batchInsert(comparisonPOList);
                }
            }
        } else {
            // 修改
            // 验证采购单主题
            if (dto.getPurchaseTitle() == null || dto.getPurchaseTitle().trim().isEmpty()) {
                throw new BusinessRuntimeException("采购单主题不能为空");
            }
            // 验证采购单号是否重复
            if (dto.getPurchaseNo() != null && mapper.countByPurchaseNo(dto.getPurchaseNo(), dto.getId()) > 0) {
                throw new BusinessRuntimeException("采购单号已存在");
            }
            // 校验：如果采购明细有入库记录，不允许编辑
            validatePurchaseDetailsForEdit(dto.getId(), dto.getDetailList());
            // 查询数据库中的当前记录，获取当前状态
            EMaterialPurchaseDTO existingDto = mapper.selectById(dto.getId());
            if (existingDto == null) {
                throw new BusinessRuntimeException("采购单不存在");
            }
            // 如果当前状态是驳回（2），则清空审核信息并将状态改为待审核（0）
            if (existingDto.getProcessStatus() != null && "3".equals(existingDto.getProcessStatus())) {
                po.setApprovalBy(null);
                po.setApprovalByName(null);
                po.setApprovalTime(null);
                po.setApprovalRemark(null);
                po.setPurchaseStatus(0); // 修改为待审核状态
            }

            mapper.update(po);
            // 处理明细：新增、修改、删除
            saveDetailList(dto.getId(), dto.getDetailList());
            // 处理比价信息：新增、修改、删除（仅当采购类型为比价时）
            if ("01".equals(dto.getPurchaseTypeCode())) {
                saveComparisonList(dto.getId(), dto.getComparisonList());
            } else {
                // 如果采购类型不是比价，删除所有比价信息
                comparisonMapper.deleteByPurchaseId(dto.getId());
            }
        }


        //修改申请子表关联采购订单子表id
        approve(dto.getId(), null, null);




    }

    /**
     * 删除物资采购
     */
    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public void deleteById(Long id) {
        // 查询所有明细，用于校验和清空申报明细的关联
        List<EMaterialPurchaseDetailDTO> detailList = detailMapper.selectListByPurchaseId(id);
        // 校验：如果采购明细有入库记录，不允许删除
        validatePurchaseDetailsForDelete(detailList);
        // 先清空申报明细的采购明细ID关联
        for (EMaterialPurchaseDetailDTO detail : detailList) {
            if (detail.getId() != null) {
                applicationDetailMapper.clearPurchaseDetailIdByPurchaseDetailId(detail.getId());
            }
        }
        // 再删除采购明细
        detailMapper.deleteByPurchaseId(id);
        // 最后删除主表
        mapper.deleteById(id);
    }

    /**
     * 标记采购失败
     */
    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public void markAsFailed(Long id, String failureReason) {
        // 查询所有明细，用于校验
        List<EMaterialPurchaseDetailDTO> detailList = detailMapper.selectListByPurchaseId(id);
        // 校验：如果采购明细有入库记录，不允许标记失败
        validatePurchaseDetailsForDelete(detailList);
        EMaterialPurchasePO po = new EMaterialPurchasePO();
        po.setId(id);
        po.setPurchaseStatus(1); // 1-采购失败（保留旧逻辑，但状态值已变更）
        po.setFailureReason(failureReason);
        mapper.update(po);
    }

    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public void approve(Long id, Integer status, String approvalRemark) {
            // 查询采购明细列表
            List<EMaterialPurchaseDetailDTO> detailList = detailMapper.selectListByPurchaseId(id);
            // 构建批量更新列表
            List<java.util.Map<String, Object>> updateList = new ArrayList<>();
            for (EMaterialPurchaseDetailDTO detailDTO : detailList) {
                if (detailDTO.getApplicationDetailId() != null && detailDTO.getId() != null) {
                    java.util.Map<String, Object> updateItem = new java.util.HashMap<>();
                    updateItem.put("id", detailDTO.getApplicationDetailId());
                    updateItem.put("purchaseDetailId", detailDTO.getId());
                    updateList.add(updateItem);
                }
            }
            // 批量更新申报明细的采购明细ID
            if (!updateList.isEmpty()) {
                applicationDetailMapper.batchUpdatePurchaseDetailId(updateList);
            }

    }

    /**
     * 保存明细列表（修改时使用）
     * 分别处理新增、修改、删除
     */
    private void saveDetailList(Long purchaseId, List<EMaterialPurchaseDetailDTO> detailList) {
        // 查询数据库中原有的明细ID列表
        List<EMaterialPurchaseDetailDTO> existingDetails = detailMapper.selectListByPurchaseId(purchaseId);
        Set<Long> existingIds = existingDetails.stream()
                .map(EMaterialPurchaseDetailDTO::getId)
                .collect(Collectors.toSet());

        // 获取前端传来的明细ID列表
        Set<Long> incomingIds = detailList != null ? detailList.stream()
                .filter(detail -> detail.getId() != null)
                .map(EMaterialPurchaseDetailDTO::getId)
                .collect(Collectors.toSet()) : Set.of();

        // 找出需要删除的明细（数据库有但前端没有传）
        Set<Long> idsToDelete = existingIds.stream()
                .filter(id -> !incomingIds.contains(id))
                .collect(Collectors.toSet());

        // 删除明细（先清空申报明细的采购明细ID关联，再删除采购明细）
        for (Long id : idsToDelete) {
            // 清空申报明细的采购明细ID关联
            applicationDetailMapper.clearPurchaseDetailIdByPurchaseDetailId(id);
            // 删除采购明细
            detailMapper.deleteById(id);
        }

        // 处理前端传来的明细
        if (detailList != null && !detailList.isEmpty()) {
            List<EMaterialPurchaseDetailPO> newDetailList = new ArrayList<>();
            List<EMaterialPurchaseDetailDTO> newDetailDTOList = new ArrayList<>(); // 用于批量插入后更新申报明细关联

            for (EMaterialPurchaseDetailDTO detailDTO : detailList) {
                EMaterialPurchaseDetailPO detailPO = new EMaterialPurchaseDetailPO();
                BeanUtils.copyProperties(detailDTO, detailPO);
                detailPO.setPurchaseId(purchaseId);

                if (detailPO.getId() == null) {
                    // 新增明细，收集到列表中批量插入
                    detailPO.setId(snowflake.nextId());
                    newDetailList.add(detailPO);
                    newDetailDTOList.add(detailDTO); // 保存对应的DTO，用于后续更新关联
                } else {
                    // 修改明细
                    // 先清空旧的申报明细关联（如果申报明细ID发生变化）
                    EMaterialPurchaseDetailDTO existingDetail = existingDetails.stream()
                            .filter(d -> d.getId().equals(detailPO.getId()))
                            .findFirst()
                            .orElse(null);
                    if (existingDetail != null) {
                        // 如果旧的申报明细ID存在且与新ID不同，清空旧的关联
                        if (existingDetail.getApplicationDetailId() != null) {
                            if (detailDTO.getApplicationDetailId() == null
                                    || !existingDetail.getApplicationDetailId().equals(detailDTO.getApplicationDetailId())) {
                                // 申报明细ID发生变化或被清空，清空旧的关联
                                applicationDetailMapper.updatePurchaseDetailId(existingDetail.getApplicationDetailId(), null);
                            }
                        }
                    }
                    detailMapper.update(detailPO);
                    // 注意：更新申报明细的采购明细ID操作已移到审核通过时执行
                }
            }

            // 批量插入新增的明细
            if (!newDetailList.isEmpty()) {
                detailMapper.batchInsert(newDetailList);
                // 注意：更新申报明细的采购明细ID操作已移到审核通过时执行
            }
        }

    }

    /**
     * 计算明细的含税金额、不含税金额和税额
     */
    private void calculateDetailAmounts(EMaterialPurchaseDetailPO detailPO) {
        if (detailPO.getPurchaseQuantity() == null || detailPO.getPurchaseQuantity().compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }

        BigDecimal quantity = detailPO.getPurchaseQuantity();
        BigDecimal taxRate = detailPO.getTaxRate() != null ? detailPO.getTaxRate() : BigDecimal.ZERO;

        // 如果含税单价存在，计算含税金额和不含税金额
        if (detailPO.getTaxIncludedUnitPrice() != null && detailPO.getTaxIncludedUnitPrice().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal taxIncludedAmount = detailPO.getTaxIncludedUnitPrice().multiply(quantity);
            detailPO.setTaxIncludedAmount(taxIncludedAmount);

            // 计算不含税单价和金额
            BigDecimal taxExcludedUnitPrice = taxIncludedAmount.divide(BigDecimal.ONE.add(taxRate.divide(new BigDecimal("100"))), 2, BigDecimal.ROUND_HALF_UP).divide(quantity, 2, BigDecimal.ROUND_HALF_UP);
            detailPO.setTaxExcludedUnitPrice(taxExcludedUnitPrice);
            BigDecimal taxExcludedAmount = taxExcludedUnitPrice.multiply(quantity);
            detailPO.setTaxExcludedAmount(taxExcludedAmount);

            // 计算税额
            BigDecimal taxAmount = taxIncludedAmount.subtract(taxExcludedAmount);
            detailPO.setTaxAmount(taxAmount);
        }
        // 如果不含税单价存在，计算不含税金额和含税金额
        else if (detailPO.getTaxExcludedUnitPrice() != null && detailPO.getTaxExcludedUnitPrice().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal taxExcludedAmount = detailPO.getTaxExcludedUnitPrice().multiply(quantity);
            detailPO.setTaxExcludedAmount(taxExcludedAmount);

            // 计算含税单价和金额
            BigDecimal taxIncludedUnitPrice = taxExcludedAmount.multiply(BigDecimal.ONE.add(taxRate.divide(new BigDecimal("100")))).divide(quantity, 2, BigDecimal.ROUND_HALF_UP);
            detailPO.setTaxIncludedUnitPrice(taxIncludedUnitPrice);
            BigDecimal taxIncludedAmount = taxIncludedUnitPrice.multiply(quantity);
            detailPO.setTaxIncludedAmount(taxIncludedAmount);

            // 计算税额
            BigDecimal taxAmount = taxIncludedAmount.subtract(taxExcludedAmount);
            detailPO.setTaxAmount(taxAmount);
        }
    }

    /**
     * 计算主表的含税金额和不含税金额（从明细汇总）
     */
    private void calculateMainAmounts(List<EMaterialPurchaseDetailDTO> detailList, EMaterialPurchasePO po) {
        BigDecimal totalTaxIncludedAmount = BigDecimal.ZERO;
        BigDecimal totalTaxExcludedAmount = BigDecimal.ZERO;

        if (detailList != null && !detailList.isEmpty()) {
            for (EMaterialPurchaseDetailDTO detail : detailList) {
                if (detail.getTaxIncludedAmount() != null) {
                    totalTaxIncludedAmount = totalTaxIncludedAmount.add(detail.getTaxIncludedAmount());
                }
                if (detail.getTaxExcludedAmount() != null) {
                    totalTaxExcludedAmount = totalTaxExcludedAmount.add(detail.getTaxExcludedAmount());
                }
            }
        }

        po.setTaxIncludedAmount(totalTaxIncludedAmount);
        po.setTaxExcludedAmount(totalTaxExcludedAmount);
    }

    /**
     * 保存比价信息列表（修改时使用）
     * 分别处理新增、修改、删除
     */
    private void saveComparisonList(Long purchaseId, List<EMaterialPurchaseComparisonDTO> comparisonList) {
        // 查询数据库中原有的比价信息ID列表
        List<EMaterialPurchaseComparisonDTO> existingComparisons = comparisonMapper.selectListByPurchaseId(purchaseId);
        Set<Long> existingIds = existingComparisons.stream()
                .map(EMaterialPurchaseComparisonDTO::getId)
                .collect(Collectors.toSet());

        // 获取前端传来的比价信息ID列表
        Set<Long> incomingIds = comparisonList != null ? comparisonList.stream()
                .filter(comparison -> comparison.getId() != null)
                .map(EMaterialPurchaseComparisonDTO::getId)
                .collect(Collectors.toSet()) : Set.of();

        // 找出需要删除的比价信息（数据库有但前端没有传）
        Set<Long> idsToDelete = existingIds.stream()
                .filter(id -> !incomingIds.contains(id))
                .collect(Collectors.toSet());

        // 删除比价信息
        for (Long id : idsToDelete) {
            comparisonMapper.deleteById(id);
        }

        // 处理前端传来的比价信息
        if (comparisonList != null && !comparisonList.isEmpty()) {
            List<EMaterialPurchaseComparisonPO> newComparisonList = new ArrayList<>();
            for (EMaterialPurchaseComparisonDTO comparisonDTO : comparisonList) {
                EMaterialPurchaseComparisonPO comparisonPO = new EMaterialPurchaseComparisonPO();
                BeanUtils.copyProperties(comparisonDTO, comparisonPO);
                comparisonPO.setPurchaseId(purchaseId);

                if (comparisonPO.getId() == null) {
                    // 新增比价信息，收集到列表中批量插入
                    comparisonPO.setId(snowflake.nextId());
                    newComparisonList.add(comparisonPO);
                } else {
                    // 修改比价信息
                    comparisonMapper.update(comparisonPO);
                }
            }
            // 批量插入新增的比价信息
            if (!newComparisonList.isEmpty()) {
                comparisonMapper.batchInsert(newComparisonList);
            }
        }
    }

    /**
     * 生成采购单号：CG + 时间戳 + 6位随机数
     */
    private String generatePurchaseNo() {
        long timestamp = System.currentTimeMillis();
        Random random = new Random();
        int randomNum = random.nextInt(900000) + 100000; // 生成6位随机数（100000-999999）
        return "CG" + timestamp + randomNum;
    }

    /**
     * 校验采购明细是否有入库记录（用于编辑操作）
     * @param purchaseId 采购单ID
     * @param detailList 要保存的明细列表
     */
    private void validatePurchaseDetailsForEdit(Long purchaseId, List<EMaterialPurchaseDetailDTO> detailList) {
        if (detailList == null || detailList.isEmpty()) {
            return;
        }

        // 查询数据库中原有的明细
        List<EMaterialPurchaseDetailDTO> existingDetails = detailMapper.selectListByPurchaseId(purchaseId);
        Set<Long> existingIds = existingDetails.stream()
                .map(EMaterialPurchaseDetailDTO::getId)
                .collect(Collectors.toSet());

        // 获取前端传来的明细ID列表
        Set<Long> incomingIds = detailList.stream()
                .filter(detail -> detail.getId() != null)
                .map(EMaterialPurchaseDetailDTO::getId)
                .collect(Collectors.toSet());

        // 找出需要删除的明细（数据库有但前端没有传）
        Set<Long> idsToDelete = existingIds.stream()
                .filter(id -> !incomingIds.contains(id))
                .collect(Collectors.toSet());

        // 检查要删除的明细是否有入库记录
        for (Long detailId : idsToDelete) {
            int count = warehouseInDetailMapper.countByPurchaseDetailId(detailId);
            if (count > 0) {
                EMaterialPurchaseDetailDTO detail = existingDetails.stream()
                        .filter(d -> d.getId().equals(detailId))
                        .findFirst()
                        .orElse(null);
                String materialName = detail != null && detail.getMaterialName() != null ? detail.getMaterialName() : "";
                throw new BusinessRuntimeException("物资【" + materialName + "】已存在入库记录，不允许删除");
            }
        }

        // 检查要修改的明细是否有入库记录（如果明细ID存在且被修改）
        for (EMaterialPurchaseDetailDTO detailDTO : detailList) {
            if (detailDTO.getId() != null && existingIds.contains(detailDTO.getId())) {
                // 这是一个已存在的明细，检查是否有入库记录
                int count = warehouseInDetailMapper.countByPurchaseDetailId(detailDTO.getId());
                if (count > 0) {
                    String materialName = detailDTO.getMaterialName() != null ? detailDTO.getMaterialName() : "";
                    throw new BusinessRuntimeException("物资【" + materialName + "】已存在入库记录，不允许修改");
                }
            }
        }
    }

    /**
     * 校验采购明细是否有入库记录（用于删除和标记失败操作）
     * @param detailList 采购明细列表
     */
    private void validatePurchaseDetailsForDelete(List<EMaterialPurchaseDetailDTO> detailList) {
        if (detailList == null || detailList.isEmpty()) {
            return;
        }

        for (EMaterialPurchaseDetailDTO detail : detailList) {
            if (detail.getId() != null) {
                int count = warehouseInDetailMapper.countByPurchaseDetailId(detail.getId());
                if (count > 0) {
                    String materialName = detail.getMaterialName() != null ? detail.getMaterialName() : "";
                    throw new BusinessRuntimeException("物资【" + materialName + "】已存在入库记录，不允许进行此操作");
                }
            }
        }
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

