package com.yy.ppm.equipment.service.impl;

import cn.hutool.core.lang.Snowflake;
import com.yy.common.page.Pages;
import com.yy.common.util.PageHelperUtils;
import com.yy.common.util.SecurityUtils;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.auth.bean.dto.UserInfo;
import com.yy.ppm.common.enums.SerialNumberPrefixEnum;
import com.yy.ppm.common.service.impl.CommonServiceImpl;
import com.yy.ppm.common.service.SysFileService;
import com.yy.ppm.equipment.bean.dto.EMaterialWarehouseInAcceptanceDTO;
import com.yy.ppm.equipment.bean.dto.EMaterialWarehouseInDTO;
import com.yy.ppm.equipment.bean.dto.EMaterialWarehouseInDetailDTO;
import com.yy.ppm.equipment.bean.dto.EMaterialWarehouseInSearchDTO;
import com.yy.ppm.equipment.bean.po.EMaterialWarehouseInPO;
import com.yy.ppm.equipment.bean.po.EMaterialWarehouseInDetailPO;
import com.yy.ppm.equipment.mapper.EMaterialApplicationDetailMapper;
import com.yy.ppm.equipment.mapper.EMaterialOutApplicationDetailMapper;
import com.yy.ppm.equipment.mapper.EMaterialWarehouseInDetailMapper;
import com.yy.ppm.equipment.mapper.EMaterialWarehouseInMapper;
import com.yy.ppm.equipment.service.EMaterialWarehouseInService;
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
import java.util.Map;
import java.util.Random;

/**
 * 物资入库Service业务层处理
 * @author system
 */
@RequiredArgsConstructor
@Service
public class EMaterialWarehouseInServiceImpl implements EMaterialWarehouseInService {

    @Resource
    private EMaterialWarehouseInMapper mapper;

    @Resource
    private EMaterialWarehouseInDetailMapper detailMapper;

    @Resource
    private EMaterialApplicationDetailMapper applicationDetailMapper;

    @Resource
    private com.yy.ppm.equipment.mapper.EMaterialCodeMapper materialCodeMapper;

    @Resource
    private Snowflake snowflake;

    @Resource
    private SecurityUtils securityUtils;

    @Resource
    private SysFileService sysFileService;

    @Resource
    private EMaterialOutApplicationDetailMapper eMaterialOutApplicationDetailMapper;


    /**
     * 查询物资入库列表（分页）
     */
    @Override
    public Pages<EMaterialWarehouseInDTO> getList(EMaterialWarehouseInSearchDTO searchDTO) {
        return PageHelperUtils.limit(searchDTO, () -> mapper.selectList(searchDTO));
    }

    /**
     * 根据ID查询物资入库（包含明细）
     */
    @Override
    public EMaterialWarehouseInDTO getById(Long id) {
        EMaterialWarehouseInDTO dto = mapper.selectById(id);
        if (dto != null) {
            // 查询明细列表
            List<EMaterialWarehouseInDetailDTO> detailList = detailMapper.selectListByWarehouseInId(id);
            dto.setDetailList(detailList);
            // 查询附件列表
            List<Long> fileIds = sysFileService.getBusFiles(id, "MATERIAL_WAREHOUSE_IN_FILE")
                    .stream()
                    .map(file -> file.getId())
                    .collect(java.util.stream.Collectors.toList());
            dto.setFileIds(fileIds);
        }
        return dto;
    }
    @Autowired
    CommonServiceImpl commonService;

    /**
     * 新增或修改物资入库
     */
    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public void save(EMaterialWarehouseInDTO dto) {
        EMaterialWarehouseInPO po = new EMaterialWarehouseInPO();
        BeanUtils.copyProperties(dto, po);

        // 设置登录用户信息
        po.setLoginUserId(securityUtils.getLoginUserId());
        po.setLoginUserName(securityUtils.getLoginUserName());

        if (dto.getId() == null) {
            po.setId(snowflake.nextId());
            dto.setId(po.getId());
            String warehouseInNo = commonService.generateSerialNumber(SerialNumberPrefixEnum.WAREHOUSE_IN);
            po.setWarehouseInNo(warehouseInNo);
            dto.setWarehouseInNo(warehouseInNo);

            UserInfo userInfo = securityUtils.getUserInfo();
            if (po.getDeptId() == null && userInfo != null && userInfo.getDeptId() != null) {
                po.setDeptId(userInfo.getDeptId());
                po.setDeptName(userInfo.getDeptName());
                dto.setDeptId(userInfo.getDeptId());
                dto.setDeptName(userInfo.getDeptName());
            }

            // 验证入库单号是否重复
            int count = mapper.countByWarehouseInNo(warehouseInNo, null);
            if (count > 0) {
                throw new BusinessRuntimeException("入库单号已存在，请重试");
            }

            // 插入主表
            mapper.insert(po);
            // 关联文件
            if (dto.getFileIds() != null && !dto.getFileIds().isEmpty()) {
                sysFileService.saveFileBusRelation(dto.getFileIds(), po.getId());
            }
            // 批量插入明细
            if (dto.getDetailList() != null && !dto.getDetailList().isEmpty()) {
                // 验证入库数量
                if (!"07".equals(dto.getWarehouseInTypeCode())) {
                    validateWarehouseInQuantity(dto.getDetailList(), null);
                }
                List<EMaterialWarehouseInDetailPO> detailPOList = new ArrayList<>();
                int sortNum = 1;
                for (EMaterialWarehouseInDetailDTO detailDTO : dto.getDetailList()) {
                    EMaterialWarehouseInDetailPO detailPO = new EMaterialWarehouseInDetailPO();
                    BeanUtils.copyProperties(detailDTO, detailPO);
                    detailPO.setId(snowflake.nextId());
                    detailPO.setWarehouseInId(po.getId());
                    detailPO.setSortNum(sortNum++);
                    // 如果unitCode为空，根据materialId查询物资代码获取unitCode
                    if ((detailPO.getUnitCode() == null || detailPO.getUnitCode().trim().isEmpty()) && detailPO.getMaterialId() != null) {
                        com.yy.ppm.equipment.bean.dto.EMaterialCodeDTO materialCode = materialCodeMapper.selectById(detailPO.getMaterialId());
                        if (materialCode != null && materialCode.getUnitCode() != null) {
                            detailPO.setUnitCode(materialCode.getUnitCode());
                        }
                    }
                    // 计算未出库数量（入库数量 - 已出库数量）
                    if (detailPO.getWarehouseInQuantity() != null && detailPO.getOutQuantity() != null) {
                        detailPO.setRemainingQuantity(detailPO.getWarehouseInQuantity().subtract(detailPO.getOutQuantity()));
                    } else if (detailPO.getWarehouseInQuantity() != null) {
                        detailPO.setRemainingQuantity(detailPO.getWarehouseInQuantity());
                    }
                    // 计算未开票金额（含税金额 - 已开票金额）
                    if (detailPO.getTaxIncludedAmount() != null && detailPO.getInvoicedAmount() != null) {
                        detailPO.setUninvoicedAmount(detailPO.getTaxIncludedAmount().subtract(detailPO.getInvoicedAmount()));
                    } else if (detailPO.getTaxIncludedAmount() != null) {
                        detailPO.setUninvoicedAmount(detailPO.getTaxIncludedAmount());
                    }
                    detailPOList.add(detailPO);
                }
                if (!detailPOList.isEmpty()) {
                    detailMapper.batchInsert(detailPOList);
                }
            }
        } else {
            mapper.update(po);
            // 关联文件
            if (dto.getFileIds() != null && !dto.getFileIds().isEmpty()) {
                sysFileService.saveFileBusRelation(dto.getFileIds(), po.getId());
            }

            detailMapper.deleteByWarehouseInId(po.getId());
            if (dto.getDetailList() != null && !dto.getDetailList().isEmpty()) {
                // 验证入库数量
                if (!"07".equals(dto.getWarehouseInTypeCode())) {
                    validateWarehouseInQuantity(dto.getDetailList(), po.getId());
                }
                List<EMaterialWarehouseInDetailPO> detailPOList = new ArrayList<>();
                int sortNum = 1;
                for (EMaterialWarehouseInDetailDTO detailDTO : dto.getDetailList()) {
                    EMaterialWarehouseInDetailPO detailPO = new EMaterialWarehouseInDetailPO();
                    BeanUtils.copyProperties(detailDTO, detailPO);
                    // 如果明细ID为空，说明是新增的明细
                    if (detailPO.getId() == null) {
                        detailPO.setId(snowflake.nextId());
                    }
                    detailPO.setWarehouseInId(po.getId());
                    detailPO.setSortNum(sortNum++);
                    // 如果规格描述为空，从申请单明细中获取
                    if ((detailPO.getSpecificationDesc() == null || detailPO.getSpecificationDesc().trim().isEmpty())
                            && detailPO.getApplicationId() != null) {
                        String specificationDesc = applicationDetailMapper.selectSpecificationDescById(detailPO.getApplicationId());
                        detailPO.setSpecificationDesc(specificationDesc);
                    }
                    // 如果unitCode为空，根据materialId查询物资代码获取unitCode
                    if ((detailPO.getUnitCode() == null || detailPO.getUnitCode().trim().isEmpty()) && detailPO.getMaterialId() != null) {
                        com.yy.ppm.equipment.bean.dto.EMaterialCodeDTO materialCode = materialCodeMapper.selectById(detailPO.getMaterialId());
                        if (materialCode != null && materialCode.getUnitCode() != null) {
                            detailPO.setUnitCode(materialCode.getUnitCode());
                        }
                    }
                    // 计算未出库数量
                    if (detailPO.getWarehouseInQuantity() != null && detailPO.getOutQuantity() != null) {
                        detailPO.setRemainingQuantity(detailPO.getWarehouseInQuantity().subtract(detailPO.getOutQuantity()));
                    } else if (detailPO.getWarehouseInQuantity() != null) {
                        detailPO.setRemainingQuantity(detailPO.getWarehouseInQuantity());
                    }
                    // 计算未开票金额
                    if (detailPO.getTaxIncludedAmount() != null && detailPO.getInvoicedAmount() != null) {
                        detailPO.setUninvoicedAmount(detailPO.getTaxIncludedAmount().subtract(detailPO.getInvoicedAmount()));
                    } else if (detailPO.getTaxIncludedAmount() != null) {
                        detailPO.setUninvoicedAmount(detailPO.getTaxIncludedAmount());
                    }
                    detailPOList.add(detailPO);
                }
                if (!detailPOList.isEmpty()) {
                    detailMapper.batchInsert(detailPOList);
                }
            }
        }
    }

    /**
     * 删除物资入库
     */
    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public void deleteById(Long id) {
        // 查询入库单信息
        EMaterialWarehouseInDTO dto = mapper.selectById(id);
        if (dto == null) {
            throw new BusinessRuntimeException("入库单不存在");
        }

        // 验证：只有待验收状态才能删除
        Integer acceptanceStatus = dto.getAcceptanceStatus();
        if (acceptanceStatus != null && acceptanceStatus != 0) {
            throw new BusinessRuntimeException("验收后的入库单不能删除");
        }

        // 先删除明细
        detailMapper.deleteByWarehouseInId(id);
        // 再删除主表
        mapper.deleteById(id);
    }

    /**
     * 验收物资入库
     */
    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public void acceptance(EMaterialWarehouseInAcceptanceDTO dto) {
        if (dto == null || dto.getId() == null) {
            throw new BusinessRuntimeException("入库单ID不能为空");
        }

        if (dto.getAcceptanceStatus() == null) {
            throw new BusinessRuntimeException("验收状态不能为空");
        }

        // 验证验收状态值：0-待验收，1-通过，2-不通过
        if (dto.getAcceptanceStatus() != 0 && dto.getAcceptanceStatus() != 1 && dto.getAcceptanceStatus() != 2) {
            throw new BusinessRuntimeException("验收状态值无效，只能为0（待验收）、1（通过）或2（不通过）");
        }

        // 查询入库单
        EMaterialWarehouseInPO po = mapper.selectById(dto.getId());
        if (po == null) {
            throw new BusinessRuntimeException("入库单不存在");
        }

        // 设置验收信息
        po.setAcceptanceStatus(dto.getAcceptanceStatus());
        po.setAcceptanceRemarks(dto.getAcceptanceRemarks());
        // 更新入库单
        mapper.updateStatus(po);
    }

    /**
     * 生成入库单号：RK + 时间戳 + 6位随机数
     */
    private String generateWarehouseInNo() {
        long timestamp = System.currentTimeMillis();
        Random random = new Random();
        int randomNum = random.nextInt(900000) + 100000; // 生成6位随机数（100000-999999）
        return "RK" + timestamp + randomNum;
    }

    /**
     * 验证入库数量：每条明细的总入库数（本次入库数 + 数据库中已有的入库数）不能大于采购数
     * @param detailList 入库明细列表
     * @param excludeWarehouseInId 排除的入库单ID（修改时使用，新增时为null）
     */
    private void validateWarehouseInQuantity(List<EMaterialWarehouseInDetailDTO> detailList, Long excludeWarehouseInId) {
        if (detailList == null || detailList.isEmpty()) {
            return;
        }

        for (EMaterialWarehouseInDetailDTO detailDTO : detailList) {
            if (detailDTO.getApplicationId() == null) {
                throw new BusinessRuntimeException("申报明细ID不能为空");
            }

            if (detailDTO.getWarehouseInQuantity() == null || detailDTO.getWarehouseInQuantity().compareTo(BigDecimal.ZERO) <= 0) {
                continue; // 跳过入库数量为空或小于等于0的明细
            }

            // 通过申报明细ID查询采购明细ID和采购数量
            Map<String, Object> purchaseInfo = detailMapper.getPurchaseDetailInfoByApplicationId(detailDTO.getApplicationId());
            if (purchaseInfo == null || purchaseInfo.isEmpty()) {
                throw new BusinessRuntimeException("物资【" + (detailDTO.getMaterialName() != null ? detailDTO.getMaterialName() : "") + "】未关联采购明细，无法验证入库数量");
            }

            // 获取采购明细ID
            Object purchaseDetailIdObj = purchaseInfo.get("purchaseDetailId");
            Long purchaseDetailId = null;
            if (purchaseDetailIdObj != null) {
                if (purchaseDetailIdObj instanceof Long) {
                    purchaseDetailId = (Long) purchaseDetailIdObj;
                } else if (purchaseDetailIdObj instanceof Number) {
                    purchaseDetailId = ((Number) purchaseDetailIdObj).longValue();
                }
            }

            // 获取采购数量
            Object purchaseQuantityObj = purchaseInfo.get("purchaseQuantity");
            BigDecimal purchaseQuantity = null;
            if (purchaseQuantityObj != null) {
                if (purchaseQuantityObj instanceof BigDecimal) {
                    purchaseQuantity = (BigDecimal) purchaseQuantityObj;
                } else if (purchaseQuantityObj instanceof Number) {
                    purchaseQuantity = BigDecimal.valueOf(((Number) purchaseQuantityObj).doubleValue());
                }
            }

            if (purchaseDetailId == null) {
                throw new BusinessRuntimeException("物资【" + (detailDTO.getMaterialName() != null ? detailDTO.getMaterialName() : "") + "】未关联采购明细，无法验证入库数量");
            }

            if (purchaseQuantity == null || purchaseQuantity.compareTo(BigDecimal.ZERO) <= 0) {
                throw new BusinessRuntimeException("物资【" + (detailDTO.getMaterialName() != null ? detailDTO.getMaterialName() : "") + "】的采购数量无效");
            }

            // 查询数据库中已有的入库数量总和（排除当前入库单）
            BigDecimal existingWarehouseInQuantity = detailMapper.sumWarehouseInQuantityByPurchaseDetailId(purchaseDetailId, excludeWarehouseInId);
            if (existingWarehouseInQuantity == null) {
                existingWarehouseInQuantity = BigDecimal.ZERO;
            }

            // 计算总入库数量（本次入库数量 + 数据库中已有的入库数量）
            BigDecimal totalWarehouseInQuantity = detailDTO.getWarehouseInQuantity().add(existingWarehouseInQuantity);

            // 验证：总入库数量不能大于采购数量
            if (totalWarehouseInQuantity.compareTo(purchaseQuantity) > 0) {
                throw new BusinessRuntimeException(
                    String.format("物资【%s】的入库数量超过采购数量。采购数量：%s，已入库数量：%s，本次入库数量：%s，总入库数量：%s",
                        detailDTO.getMaterialName() != null ? detailDTO.getMaterialName() : "",
                        purchaseQuantity,
                        existingWarehouseInQuantity,
                        detailDTO.getWarehouseInQuantity(),
                        totalWarehouseInQuantity
                    )
                );
            }
        }
    }

    /**
     * 查询物资库存数量（按物资ID和仓库ID）
     */
    @Override
    public BigDecimal getStockQuantity(Long materialId, Long warehouseId) {
        // warehouseId 为 null 时，查询所有仓库的库存总和
        BigDecimal stockQuantity = detailMapper.getStockQuantity(materialId, warehouseId);
        return stockQuantity != null ? stockQuantity : BigDecimal.ZERO;
    }

    /**
     * 查询物资库存数量（按物资ID和仓库ID）
     */
    @Override
    public BigDecimal getAvailableInventory(Long materialId, Long warehouseId) {

        java.math.BigDecimal availableInventory = java.math.BigDecimal.ZERO;

        // 查询库存数量
        java.math.BigDecimal stockQuantity =getStockQuantity(materialId, warehouseId);
        if (stockQuantity == null) {
            stockQuantity = java.math.BigDecimal.ZERO;
        }

        // 查询已出库数量（已审批通过的出库申请明细的申请数量总和）
        java.math.BigDecimal outQuantity = eMaterialOutApplicationDetailMapper.selectOutQuantityByMaterialAndWarehouse(materialId,warehouseId, null);
        if (outQuantity == null) {
            outQuantity = java.math.BigDecimal.ZERO;
        }
        if ((stockQuantity.subtract(outQuantity)).compareTo(java.math.BigDecimal.ZERO) > 0) {
            availableInventory = (stockQuantity.subtract(outQuantity));


        }
        return availableInventory;
    }
}

