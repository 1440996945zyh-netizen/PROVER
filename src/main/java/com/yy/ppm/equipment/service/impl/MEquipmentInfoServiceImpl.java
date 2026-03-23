package com.yy.ppm.equipment.service.impl;

import cn.hutool.core.lang.Snowflake;
import com.yy.common.page.Pages;
import com.yy.common.util.PageHelperUtils;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.equipment.bean.dto.EquipmentSelectDTO;
import com.yy.ppm.equipment.bean.dto.MEquipmentFinanceDTO;
import com.yy.ppm.equipment.bean.dto.MEquipmentInfoDTO;
import com.yy.ppm.equipment.bean.dto.MEquipmentInfoSearchDTO;
import com.yy.ppm.equipment.bean.dto.MEquipmentSpecialDTO;
import com.yy.ppm.equipment.bean.dto.MEquipmentSupplyDTO;
import com.yy.ppm.equipment.bean.po.MEquipmentInfoPO;
import com.yy.ppm.equipment.mapper.MEquipmentInfoMapper;
import com.yy.ppm.equipment.mapper.EMaterialWarehouseInDetailMapper;
import com.yy.ppm.equipment.service.MEquipmentFinanceService;
import com.yy.ppm.equipment.service.MEquipmentInfoService;
import com.yy.ppm.equipment.service.MEquipmentSpecialService;
import com.yy.ppm.equipment.service.MEquipmentSupplyService;
import com.yy.ppm.common.service.SysFileService;
import com.yy.ppm.equipment.util.ChangeLogUtil;
import com.yy.ppm.system.bean.dto.SysUserDTO;
import com.yy.ppm.system.service.SysUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 设备台账信息Service业务层处理
 * @author system
 */
@RequiredArgsConstructor
@Service
public class MEquipmentInfoServiceImpl implements MEquipmentInfoService {

    @Resource
    private MEquipmentInfoMapper mapper;

    @Resource
    private EMaterialWarehouseInDetailMapper warehouseInDetailMapper;

    @Resource
    private Snowflake snowflake;

    @Resource
    private MEquipmentSupplyService supplyService;

    @Resource
    private MEquipmentFinanceService financeService;

    @Resource
    private MEquipmentSpecialService specialService;

    @Resource
    private SysFileService sysFileService;

    @Resource
    private SysUserService sysUserService;

    @Resource
    private ChangeLogUtil changeLogUtil;

    /**
     * 查询设备台账信息列表（分页）
     */
    @Override
    public Pages<MEquipmentInfoDTO> getList(MEquipmentInfoSearchDTO searchDTO) {
        Pages<MEquipmentInfoDTO> pages = PageHelperUtils.limit(searchDTO, () -> {
            return mapper.selectList(searchDTO);
        });
        return pages;
    }

    /**
     * 根据ID查询设备台账信息
     */
    @Override
    public MEquipmentInfoDTO getById(Long id) {
        MEquipmentInfoDTO dto = mapper.selectById(id);
        if (dto == null) {
            return null;
        }
        // 查询负责人名称
        if (dto.getResponsiCode() != null) {
            SysUserDTO sysUserDTO = sysUserService.getById(dto.getResponsiCode());
            if (sysUserDTO != null) {
                dto.setResponsiName(sysUserDTO.getUserName());
            }
        }
        // 查询供货信息并映射到DTO（字段映射：productionCode->factoryNumber, equipBuyDate->purchaseTime, equipUseDate->usageTime, supplyCompany->supplierUnit）
        MEquipmentSupplyDTO supplyDTO = supplyService.getByEquipId(id);
        if (supplyDTO != null) {
            dto.setFactoryNumber(supplyDTO.getProductionCode());
            if (supplyDTO.getEquipWeight() != null) {
                try {
                    dto.setEquipWeight(new java.math.BigDecimal(supplyDTO.getEquipWeight()));
                } catch (Exception e) {
                    // 忽略转换错误
                }
            }
            if (supplyDTO.getEnginePower() != null) {
                try {
                    dto.setEnginePower(new java.math.BigDecimal(supplyDTO.getEnginePower()));
                } catch (Exception e) {
                    // 忽略转换错误
                }
            }
            // 直接使用Date类型，数据库是DATE类型
            dto.setPurchaseTime(supplyDTO.getEquipBuyDate());
            dto.setUsageTime(supplyDTO.getEquipUseDate());
            dto.setSupplierUnit(supplyDTO.getSupplyCompany());
            dto.setManufacturer(supplyDTO.getManufacturer());
            dto.setEmissionStandard(supplyDTO.getEmissionStandard());
        }

        // 查询财务信息并映射到DTO（字段映射：equipAssetsCode->assetsNo, price->originalValue, depreciationLimit->depreciationPeriod, alreadyLimit->depreciatedPeriod）
        MEquipmentFinanceDTO financeDTO = financeService.getByEquipId(id);
        if (financeDTO != null) {
            dto.setAssetsNo(financeDTO.getEquipAssetsCode());
            dto.setOriginalValue(financeDTO.getPrice());
            dto.setNetValue(financeDTO.getNetValue() != null ? financeDTO.getNetValue().toString() : null);
            dto.setDepreciationPeriod(financeDTO.getDepreciationLimit() != null ? financeDTO.getDepreciationLimit().intValue() : null);
            dto.setDepreciatedPeriod(financeDTO.getAlreadyLimit() != null ? financeDTO.getAlreadyLimit().intValue() : null);
        }

        // 查询特种设备信息并映射到DTO
        MEquipmentSpecialDTO specialDTO = specialService.getByEquipId(id);
        if (specialDTO != null) {
            dto.setParticularRegistrationCode(specialDTO.getParticularRegistrationCode());
            dto.setSpecialDiscoverCycle(specialDTO.getSpecialDiscoverCycle());
            dto.setCertifiType(specialDTO.getCertifiType());
            dto.setCertifiTypeName(specialDTO.getCertifiTypeName());
            dto.setCertifiCode(specialDTO.getCertifiCode());
            dto.setReleaseDate(specialDTO.getReleaseDate());
            dto.setCertifiUser(specialDTO.getCertifiUser());
            dto.setExpireDate(specialDTO.getExpireDate());
            dto.setValidDate(specialDTO.getValidDate());
            dto.setCertifiState(specialDTO.getCertifiState());
            dto.setSpecialRemark(specialDTO.getRemark());
        }

        return dto;
    }

    /**
     * 新增或修改设备台账信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public void save(MEquipmentInfoDTO dto) {
        // 验证必填字段
        if (dto.getEquipName() == null || dto.getEquipName().trim().isEmpty()) {
            throw new BusinessRuntimeException("设备名称不能为空");
        }
        if (dto.getEquipCode() == null || dto.getEquipCode().trim().isEmpty()) {
            throw new BusinessRuntimeException("设备编码不能为空");
        }
        if (dto.getEquipBigCategoryId() == null) {
            throw new BusinessRuntimeException("设备大类不能为空");
        }
        if (dto.getEquipMiddleCategoryId() == null) {
            throw new BusinessRuntimeException("设备中类不能为空");
        }
        if (dto.getEquipSmallCategoryId() == null) {
            throw new BusinessRuntimeException("设备小类不能为空");
        }
        if (dto.getEquipTechState() == null) {
            throw new BusinessRuntimeException("设备技术状态不能为空");
        }
        if (dto.getEquipState() == null) {
            throw new BusinessRuntimeException("设备状态不能为空");
        }
//        if (dto.getUseCompanyId() == null) {
//            throw new BusinessRuntimeException("所属单位不能为空");
//        }
        if (dto.getUseOrgId() == null) {
            throw new BusinessRuntimeException("所属部门不能为空");
        }

        // 验证设备编码唯一性
        int count = mapper.countByEquipCode(dto.getEquipCode(), dto.getId());
        if (count > 0) {
            throw new BusinessRuntimeException("设备编码已存在，不能重复");
        }

        MEquipmentInfoPO po = new MEquipmentInfoPO();
        BeanUtils.copyProperties(dto, po);

        Long equipId;
//        if (dto.getId() == null) {
//            // 新增
//            equipId = snowflake.nextId();
//            po.setId(equipId);
//            mapper.insert(po);
//        }
//        else {
//            // 修改
//            equipId = dto.getId();
//            mapper.update(po);
//        }

        equipId = snowflake.nextId();
        po.setId(equipId);
        mapper.insert(po);

        // 保存供货信息（字段映射：factoryNumber->productionCode, purchaseTime->equipBuyDate, usageTime->equipUseDate, supplierUnit->supplyCompany）
        MEquipmentSupplyDTO supplyDTO = new MEquipmentSupplyDTO();
        supplyDTO.setProductionCode(dto.getFactoryNumber());
        supplyDTO.setEquipWeight(dto.getEquipWeight() != null ? dto.getEquipWeight().toString() : null);
        supplyDTO.setEnginePower(dto.getEnginePower() != null ? dto.getEnginePower().toString() : null);
        // 直接使用Date类型，数据库是DATE类型
        supplyDTO.setEquipBuyDate(dto.getPurchaseTime());
        supplyDTO.setEquipUseDate(dto.getUsageTime());
        supplyDTO.setSupplyCompany(dto.getSupplierUnit());
        supplyDTO.setManufacturer(dto.getManufacturer());
        supplyDTO.setEmissionStandard(dto.getEmissionStandard());
        supplyService.save(supplyDTO, equipId);

        // 保存财务信息（字段映射：assetsNo->equipAssetsCode, originalValue->price, depreciationPeriod->depreciationLimit, depreciatedPeriod->alreadyLimit）
        MEquipmentFinanceDTO financeDTO = new MEquipmentFinanceDTO();
        financeDTO.setEquipAssetsCode(dto.getAssetsNo());
        financeDTO.setPrice(dto.getOriginalValue());
        // 新增时不保存资产净值
        financeDTO.setNetValue(null);
        financeDTO.setDepreciationLimit(dto.getDepreciationPeriod() != null ? dto.getDepreciationPeriod().longValue() : null);
        financeDTO.setAlreadyLimit(dto.getDepreciatedPeriod() != null ? dto.getDepreciatedPeriod().longValue() : null);
        financeService.save(financeDTO, equipId);

        // 保存特种设备信息（仅在isParticular为1时保存）
        if ("1".equals( dto.getIsParticular())) {
            MEquipmentSpecialDTO specialDTO = new MEquipmentSpecialDTO();
            specialDTO.setParticularRegistrationCode(dto.getParticularRegistrationCode());
            specialDTO.setSpecialDiscoverCycle(dto.getSpecialDiscoverCycle());
            specialDTO.setCertifiType(dto.getCertifiType());
            specialDTO.setCertifiTypeName(dto.getCertifiTypeName());
            specialDTO.setCertifiCode(dto.getCertifiCode());
            specialDTO.setReleaseDate(dto.getReleaseDate());
            specialDTO.setCertifiUser(dto.getCertifiUser());
            specialDTO.setExpireDate(dto.getExpireDate());
            specialDTO.setValidDate(dto.getValidDate());
            specialDTO.setCertifiState("0");
            specialDTO.setRemark(dto.getSpecialRemark());
            specialService.save(specialDTO, equipId);
        }
//        else {
//            // 如果从"是"改为"否"，删除特种设备信息
//            specialService.deleteByEquipId(equipId);
//        }

        // 保存设备图片文件关联关系
        List<Long> fileIds = new ArrayList<>();
        if (dto.getPanoramaImageIds() != null && !dto.getPanoramaImageIds().isEmpty()) {
            fileIds.addAll(dto.getPanoramaImageIds());
        }
        if (dto.getOrientationImageIds() != null && !dto.getOrientationImageIds().isEmpty()) {
            fileIds.addAll(dto.getOrientationImageIds());
        }
        if (dto.getAccessoryImageIds() != null && !dto.getAccessoryImageIds().isEmpty()) {
            fileIds.addAll(dto.getAccessoryImageIds());
        }
        if (!fileIds.isEmpty()) {
            sysFileService.saveFileBusRelation(fileIds, equipId);
        }
    }

    /**
     * 删除设备台账信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public void deleteById(Long id) {
        MEquipmentInfoPO po = new MEquipmentInfoPO();
        po.setId(id);
        mapper.deleteById(po);

        // 删除关联的供货信息
        supplyService.deleteByEquipId(id);

        // 删除关联的财务信息
        financeService.deleteByEquipId(id);

        // 删除关联的特种设备信息
        specialService.deleteByEquipId(id);
    }

    /**
     * 修改设备基本信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public void updateBasicInfo(MEquipmentInfoDTO dto) {
        if (dto.getId() == null) {
            throw new BusinessRuntimeException("设备ID不能为空");
        }

        // 验证必填字段
        if (dto.getEquipName() == null || dto.getEquipName().trim().isEmpty()) {
            throw new BusinessRuntimeException("设备名称不能为空");
        }
        if (dto.getEquipCode() == null || dto.getEquipCode().trim().isEmpty()) {
            throw new BusinessRuntimeException("设备编码不能为空");
        }
        if (dto.getEquipBigCategoryId() == null) {
            throw new BusinessRuntimeException("设备大类不能为空");
        }
        if (dto.getEquipMiddleCategoryId() == null) {
            throw new BusinessRuntimeException("设备中类不能为空");
        }
        if (dto.getEquipSmallCategoryId() == null) {
            throw new BusinessRuntimeException("设备小类不能为空");
        }
        if (dto.getEquipTechState() == null) {
            throw new BusinessRuntimeException("设备技术状态不能为空");
        }
        if (dto.getEquipState() == null) {
            throw new BusinessRuntimeException("设备状态不能为空");
        }
        if (dto.getUseOrgId() == null) {
            throw new BusinessRuntimeException("所属部门不能为空");
        }

        // 验证设备编码唯一性
        int count = mapper.countByEquipCode(dto.getEquipCode(), dto.getId());
        if (count > 0) {
            throw new BusinessRuntimeException("设备编码已存在，不能重复");
        }

        // 只更新基本信息字段
        MEquipmentInfoPO po = new MEquipmentInfoPO();
        po.setId(dto.getId());
        po.setEquipCode(dto.getEquipCode());
        po.setEquipName(dto.getEquipName());
        po.setEquipBigCategoryId(dto.getEquipBigCategoryId());
        po.setEquipBigCategoryName(dto.getEquipBigCategoryName());
        po.setEquipMiddleCategoryId(dto.getEquipMiddleCategoryId());
        po.setEquipMiddleCategoryName(dto.getEquipMiddleCategoryName());
        po.setEquipSmallCategoryId(dto.getEquipSmallCategoryId());
        po.setEquipSmallCategoryName(dto.getEquipSmallCategoryName());
        po.setSpecificCode(dto.getSpecificCode());
        po.setModelNumber(dto.getModelNumber());
        po.setEquipTechState(dto.getEquipTechState());
        po.setEquipTechStateName(dto.getEquipTechStateName());
        po.setEquipState(dto.getEquipState());
        po.setEquipStateName(dto.getEquipStateName());
        po.setUnit(dto.getUnit());
        po.setUnitName(dto.getUnitName());
        po.setInsuranceDate(dto.getInsuranceDate());
        po.setUseOrgId(dto.getUseOrgId());
        po.setResponsiCode(dto.getResponsiCode());
        po.setIsParticular(dto.getIsParticular());
        po.setRemark(dto.getRemark());
        po.setSourceType(dto.getSourceType());
        po.setSourceTypeName(dto.getSourceTypeName());
        po.setEquipSystemCode(dto.getEquipSystemCode());

        // 获取旧数据用于比较
        MEquipmentInfoDTO oldData = getById(dto.getId());
        oldData.setResponsiName(null);
        // 更新数据
        mapper.update(po);

        // 记录变更
        recordBasicInfoChange(dto.getId(), oldData, dto);
    }

    /**
     * 记录设备基本信息变更
     */

    @Override
    public void recordBasicInfoChange(Long equipId, MEquipmentInfoDTO oldData, MEquipmentInfoDTO newData) {
        if (oldData == null || newData == null) {
            return;
        }

        java.util.Map<String, Object[]> changes = new java.util.HashMap<>();

        // 基本信息字段映射
        addChangeIfDifferent(changes, "equipName", oldData.getEquipName(), newData.getEquipName(), "设备名称");
        addChangeIfDifferent(changes, "equipCode", oldData.getEquipCode(), newData.getEquipCode(), "设备编码");
        addChangeIfDifferent(changes, "equipBigCategoryName", oldData.getEquipBigCategoryName(), newData.getEquipBigCategoryName(), "设备大类");
        addChangeIfDifferent(changes, "equipMiddleCategoryName", oldData.getEquipMiddleCategoryName(), newData.getEquipMiddleCategoryName(), "设备中类");
        addChangeIfDifferent(changes, "equipSmallCategoryName", oldData.getEquipSmallCategoryName(), newData.getEquipSmallCategoryName(), "设备小类");
        addChangeIfDifferent(changes, "specificCode", oldData.getSpecificCode(), newData.getSpecificCode(), "规格编号");
        addChangeIfDifferent(changes, "modelNumber", oldData.getModelNumber(), newData.getModelNumber(), "设备型号");
        addChangeIfDifferent(changes, "equipSystemCode", oldData.getEquipSystemCode(), newData.getEquipSystemCode(), "设备系统编码");
        addChangeIfDifferent(changes, "equipTechStateName", oldData.getEquipTechStateName(), newData.getEquipTechStateName(), "设备技术状况");
        addChangeIfDifferent(changes, "equipStateName", oldData.getEquipStateName(), newData.getEquipStateName(), "设备状态");
        addChangeIfDifferent(changes, "unitName", oldData.getUnitName(), newData.getUnitName(), "计量单位");
        addChangeIfDifferent(changes, "insuranceDate", oldData.getInsuranceDate(), newData.getInsuranceDate(), "保险期限");
        addChangeIfDifferent(changes, "sourceTypeName", oldData.getSourceTypeName(), newData.getSourceTypeName(), "能源类型");
        addChangeIfDifferent(changes, "remark", oldData.getRemark(), newData.getRemark(), "备注");
        addChangeIfDifferent(changes, "useOrgName", oldData.getUseOrgName(), newData.getUseOrgName(), "所属部门");
        addChangeIfDifferent(changes, "useCompanyName", oldData.getUseCompanyName(), newData.getUseCompanyName(), "所属单位");
        addChangeIfDifferent(changes, "equipStateName", oldData.getEquipStateName(), newData.getEquipStateName(), "设备状态");
        addChangeIfDifferent(changes, "responsiName", oldData.getResponsiName(), newData.getResponsiName(), "负责人");

        if (!changes.isEmpty()) {
            changeLogUtil.recordChange(equipId, "BASIC_INFO", changes);
        }
    }

    /**
     * 如果值不同则添加到变更Map
     */
    private void addChangeIfDifferent(java.util.Map<String, Object[]> changes, String field, Object oldValue, Object newValue, String fieldName) {
        String oldStr = oldValue != null ? String.valueOf(oldValue) : null;
        String newStr = newValue != null ? String.valueOf(newValue) : null;
        if (!equalsValue(oldStr, newStr)) {
            changes.put(field, new Object[]{oldStr, newStr, fieldName});
        }
    }

    /**
     * 比较两个值是否相等
     */
    private boolean equalsValue(String oldValue, String newValue) {
        if (oldValue == null && newValue == null) {
            return true;
        }
        if (oldValue == null || newValue == null) {
            return false;
        }
        return oldValue.equals(newValue);
    }

    /**
     * 修改财务/供货信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public void updateFinanceSupply(MEquipmentInfoDTO dto) {
        if (dto.getId() == null) {
            throw new BusinessRuntimeException("设备ID不能为空");
        }

        Long equipId = dto.getId();

        // 保存供货信息
        MEquipmentSupplyDTO supplyDTO = new MEquipmentSupplyDTO();
        supplyDTO.setProductionCode(dto.getFactoryNumber());
        supplyDTO.setEquipWeight(dto.getEquipWeight() != null ? dto.getEquipWeight().toString() : null);
        supplyDTO.setEnginePower(dto.getEnginePower() != null ? dto.getEnginePower().toString() : null);
        supplyDTO.setEquipBuyDate(dto.getPurchaseTime());
        supplyDTO.setEquipUseDate(dto.getUsageTime());
        supplyDTO.setSupplyCompany(dto.getSupplierUnit());
        supplyDTO.setManufacturer(dto.getManufacturer());
        supplyDTO.setEmissionStandard(dto.getEmissionStandard());

        // 保存财务信息
        MEquipmentFinanceDTO financeDTO = new MEquipmentFinanceDTO();
        financeDTO.setEquipAssetsCode(dto.getAssetsNo());
        financeDTO.setPrice(dto.getOriginalValue());
        // 资产净值：如果前端传入了计算好的值，则使用；否则根据原值、折旧期限和已折旧期限计算
        if (dto.getNetValue() != null && !dto.getNetValue().isEmpty()) {
            financeDTO.setNetValue(new java.math.BigDecimal(dto.getNetValue()));
        } else if (dto.getOriginalValue() != null && dto.getDepreciationPeriod() != null && dto.getDepreciatedPeriod() != null && dto.getDepreciationPeriod() > 0) {
            // 计算资产净值：资产净值 = 设备原值 - (设备原值 / 折旧期限) * 已折旧期限
            java.math.BigDecimal originalValue = dto.getOriginalValue();
            int depreciationPeriod = dto.getDepreciationPeriod();
            int depreciatedPeriod = dto.getDepreciatedPeriod();
            java.math.BigDecimal monthlyDepreciation = originalValue.divide(new java.math.BigDecimal(depreciationPeriod), 2, java.math.RoundingMode.HALF_UP);
            java.math.BigDecimal totalDepreciation = monthlyDepreciation.multiply(new java.math.BigDecimal(depreciatedPeriod));
            java.math.BigDecimal netValue = originalValue.subtract(totalDepreciation);
            financeDTO.setNetValue(netValue);
        }
        financeDTO.setDepreciationLimit(dto.getDepreciationPeriod() != null ? dto.getDepreciationPeriod().longValue() : null);
        financeDTO.setAlreadyLimit(dto.getDepreciatedPeriod() != null ? dto.getDepreciatedPeriod().longValue() : null);

        // 获取旧数据用于比较（在保存之前）
        MEquipmentSupplyDTO oldSupplyDTO = supplyService.getByEquipId(equipId);
        MEquipmentFinanceDTO oldFinanceDTO = financeService.getByEquipId(equipId);

        // 保存数据
        supplyService.save(supplyDTO, equipId);
        financeService.save(financeDTO, equipId);

        // 分别记录供货信息和财务信息的变更
        recordSupplyChange(equipId, oldSupplyDTO, supplyDTO);
        recordFinanceChange(equipId, oldFinanceDTO, financeDTO);
    }

    /**
     * 记录供货信息变更
     */
    private void recordSupplyChange(Long equipId, MEquipmentSupplyDTO oldSupplyDTO, MEquipmentSupplyDTO newSupplyDTO) {
        java.util.Map<String, Object[]> changes = new java.util.HashMap<>();

        // 供货信息字段
        if (oldSupplyDTO != null && newSupplyDTO != null) {
            addChangeIfDifferent(changes, "factoryNumber", oldSupplyDTO.getProductionCode(), newSupplyDTO.getProductionCode(), "出厂编号");
            addChangeIfDifferent(changes, "equipWeight", oldSupplyDTO.getEquipWeight(), newSupplyDTO.getEquipWeight(), "设备自重(T)");
            addChangeIfDifferent(changes, "enginePower", oldSupplyDTO.getEnginePower(), newSupplyDTO.getEnginePower(), "发动机功率");
            addChangeIfDifferent(changes, "purchaseTime", formatDate(oldSupplyDTO.getEquipBuyDate()), formatDate(newSupplyDTO.getEquipBuyDate()), "设备购置时间");
            addChangeIfDifferent(changes, "usageTime", formatDate(oldSupplyDTO.getEquipUseDate()), formatDate(newSupplyDTO.getEquipUseDate()), "设备使用时间");
            addChangeIfDifferent(changes, "supplierUnit", oldSupplyDTO.getSupplyCompany(), newSupplyDTO.getSupplyCompany(), "供应商单位");
            addChangeIfDifferent(changes, "manufacturer", oldSupplyDTO.getManufacturer(), newSupplyDTO.getManufacturer(), "生产厂家");
            addChangeIfDifferent(changes, "emissionStandard", oldSupplyDTO.getEmissionStandard(), newSupplyDTO.getEmissionStandard(), "排放标准");
        }

        if (!changes.isEmpty()) {
            changeLogUtil.recordChange(equipId, "SUPPLY", changes);
        }
    }

    /**
     * 记录财务信息变更
     */
    private void recordFinanceChange(Long equipId, MEquipmentFinanceDTO oldFinanceDTO, MEquipmentFinanceDTO newFinanceDTO) {
        java.util.Map<String, Object[]> changes = new java.util.HashMap<>();

        // 财务信息字段
        if (oldFinanceDTO != null && newFinanceDTO != null) {
            addChangeIfDifferent(changes, "assetsNo", oldFinanceDTO.getEquipAssetsCode(), newFinanceDTO.getEquipAssetsCode(), "设备资产编号");
            addChangeIfDifferent(changes, "originalValue", oldFinanceDTO.getPrice() != null ? oldFinanceDTO.getPrice().toString() : null,
                                newFinanceDTO.getPrice() != null ? newFinanceDTO.getPrice().toString() : null, "设备原值");
//            addChangeIfDifferent(changes, "netValue", oldFinanceDTO.getNetValue() != null ? oldFinanceDTO.getNetValue().toString() : null,
//                                newFinanceDTO.getNetValue() != null ? newFinanceDTO.getNetValue().toString() : null, "资产净值");
            addChangeIfDifferent(changes, "depreciationPeriod", oldFinanceDTO.getDepreciationLimit() != null ? oldFinanceDTO.getDepreciationLimit().toString() : null,
                                newFinanceDTO.getDepreciationLimit() != null ? newFinanceDTO.getDepreciationLimit().toString() : null, "折旧期限");
            addChangeIfDifferent(changes, "depreciatedPeriod", oldFinanceDTO.getAlreadyLimit() != null ? oldFinanceDTO.getAlreadyLimit().toString() : null,
                                newFinanceDTO.getAlreadyLimit() != null ? newFinanceDTO.getAlreadyLimit().toString() : null, "已折旧期限");
        }

        if (!changes.isEmpty()) {
            changeLogUtil.recordChange(equipId, "FINANCE", changes);
        }
    }

    /**
     * 格式化日期
     */
    private String formatDate(java.util.Date date) {
        if (date == null) {
            return null;
        }
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(date);
    }

    /**
     * 修改特种设备信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public void updateSpecialInfo(MEquipmentInfoDTO dto) {
        if (dto.getId() == null) {
            throw new BusinessRuntimeException("设备ID不能为空");
        }

        Long equipId = dto.getId();

        // 保存特种设备信息（仅在isParticular为1时保存）
        if ("1".equals(dto.getIsParticular())) {
            MEquipmentSpecialDTO specialDTO = new MEquipmentSpecialDTO();
            specialDTO.setParticularRegistrationCode(dto.getParticularRegistrationCode());
            specialDTO.setSpecialDiscoverCycle(dto.getSpecialDiscoverCycle());
            specialDTO.setCertifiType(dto.getCertifiType());
            specialDTO.setCertifiTypeName(dto.getCertifiTypeName());
            specialDTO.setCertifiCode(dto.getCertifiCode());
            specialDTO.setReleaseDate(dto.getReleaseDate());
            specialDTO.setCertifiUser(dto.getCertifiUser());
            specialDTO.setExpireDate(dto.getExpireDate());
            specialDTO.setValidDate(dto.getValidDate());
            specialDTO.setCertifiState("0");
            specialDTO.setRemark(dto.getSpecialRemark());

            // 获取旧数据用于比较
            MEquipmentSpecialDTO oldSpecialDTO = specialService.getByEquipId(equipId);

            // 保存数据
            specialService.save(specialDTO, equipId);

            // 记录变更
            recordSpecialInfoChange(equipId, oldSpecialDTO, specialDTO);
        } else {
            // 如果从"是"改为"否"，删除特种设备信息
            specialService.deleteByEquipId(equipId);
        }
    }

    /**
     * 记录特种设备信息变更
     */
    private void recordSpecialInfoChange(Long equipId, MEquipmentSpecialDTO oldData, MEquipmentSpecialDTO newData) {
        if (oldData == null || newData == null) {
            return;
        }

        java.util.Map<String, Object[]> changes = new java.util.HashMap<>();

        // 特种设备信息字段
        addChangeIfDifferent(changes, "particularRegistrationCode", oldData.getParticularRegistrationCode(), newData.getParticularRegistrationCode(), "特种设备注册码");
        addChangeIfDifferent(changes, "specialDiscoverCycle", oldData.getSpecialDiscoverCycle() != null ? oldData.getSpecialDiscoverCycle().toString() : null,
                            newData.getSpecialDiscoverCycle() != null ? newData.getSpecialDiscoverCycle().toString() : null, "检查周期(月)");
        addChangeIfDifferent(changes, "certifiTypeName", oldData.getCertifiTypeName(), newData.getCertifiTypeName(), "证书类别");
        addChangeIfDifferent(changes, "certifiCode", oldData.getCertifiCode(), newData.getCertifiCode(), "证书编号");
        addChangeIfDifferent(changes, "releaseDate", formatDate(oldData.getReleaseDate()), formatDate(newData.getReleaseDate()), "发布时间");
        addChangeIfDifferent(changes, "certifiUser", oldData.getCertifiUser(), newData.getCertifiUser(), "证书所属人");
        addChangeIfDifferent(changes, "expireDate", formatDate(oldData.getExpireDate()), formatDate(newData.getExpireDate()), "到期时间");
        addChangeIfDifferent(changes, "validDate", oldData.getValidDate() != null ? oldData.getValidDate().toString() : null,
                            newData.getValidDate() != null ? newData.getValidDate().toString() : null, "有效期（月）");
        addChangeIfDifferent(changes, "specialRemark", oldData.getRemark(), newData.getRemark(), "备注");

        if (!changes.isEmpty()) {
            changeLogUtil.recordChange(equipId, "SPECIAL_INFO", changes);
        }
    }

    /**
     * 保存设备照片
     */
    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public void updateEquipmentImages(MEquipmentInfoDTO dto) {
        Long equipId = dto.getId();
        if (equipId == null) {
            throw new BusinessRuntimeException("设备ID不能为空");
        }

        // 合并所有三种类型的图片ID，一次性保存（saveFileBusRelation内部会先删除后插入）
        List<Long> allFileIds = new ArrayList<>();
        if (dto.getPanoramaImageIds() != null && !dto.getPanoramaImageIds().isEmpty()) {
            allFileIds.addAll(dto.getPanoramaImageIds());
        }
        if (dto.getOrientationImageIds() != null && !dto.getOrientationImageIds().isEmpty()) {
            allFileIds.addAll(dto.getOrientationImageIds());
        }
        if (dto.getAccessoryImageIds() != null && !dto.getAccessoryImageIds().isEmpty()) {
            allFileIds.addAll(dto.getAccessoryImageIds());
        }

        // 调用saveFileBusRelation，内部会先删除该业务ID的所有关联，然后插入新的关联
        sysFileService.saveFileBusRelation(allFileIds, equipId);
    }

    /**
     * 查询设备选择列表（用于下拉框）
     */
    @Override
    public List<EquipmentSelectDTO> getEquipmentSelectList(String keyword) {
        return mapper.selectEquipmentList(keyword);
    }

    /**
     * 根据设备ID查询备品备件列表
     */
    @Override
    public List<com.yy.ppm.equipment.bean.dto.EquipmentSpareDTO> getSpareList(String equipId, String materialName, String warehouseName) {
        return warehouseInDetailMapper.selectSpareListByEquipId(equipId, materialName, warehouseName);
    }
}

