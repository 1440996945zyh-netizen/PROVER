package com.yy.ppm.equipment.service.impl;

import cn.hutool.core.lang.Snowflake;
import com.yy.common.page.Pages;
import com.yy.common.util.PageHelperUtils;
import com.yy.common.util.SecurityUtils;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.common.enums.SerialNumberPrefixEnum;
import com.yy.ppm.common.service.impl.CommonServiceImpl;
import com.yy.ppm.equipment.bean.dto.EMaterialWarehouseInDetailDTO;
import com.yy.ppm.equipment.bean.dto.EMaterialWarehouseOutDTO;
import com.yy.ppm.equipment.bean.dto.EMaterialWarehouseOutDetailDTO;
import com.yy.ppm.equipment.bean.dto.EMaterialWarehouseOutSearchDTO;
import com.yy.ppm.equipment.bean.po.EMaterialWarehouseInDetailPO;
import com.yy.ppm.equipment.bean.po.EMaterialWarehouseInOutRelPO;
import com.yy.ppm.equipment.bean.po.EMaterialWarehouseOutDetailPO;
import com.yy.ppm.equipment.bean.po.EMaterialWarehouseOutPO;
import com.yy.ppm.equipment.mapper.*;
import com.yy.ppm.equipment.service.EMaterialWarehouseOutAppService;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 物资出库Service业务层处理
 * @author system
 */
@RequiredArgsConstructor
@Service
public class EMaterialWarehouseOutAppServiceImpl implements EMaterialWarehouseOutAppService {

    @Resource
    private EMaterialWarehouseOutAppMapper mapper;

    @Resource
    private EMaterialWarehouseOutDetailAppMapper detailMapper;

    @Resource
    private EMaterialWarehouseInDetailAppMapper warehouseInDetailMapper;

    @Resource
    private EMaterialWarehouseInOutRelMapper inOutRelMapper;

    @Resource
    private EMaterialOutApplicationDetailMapper outApplicationDetailMapper;

    @Resource
    private Snowflake snowflake;

    @Resource
    private SecurityUtils securityUtils;

    @Autowired
    CommonServiceImpl commonService;

    /**
     * 查询物资出库列表（分页）
     */
    @Override
    public Pages<EMaterialWarehouseOutDTO> getList(EMaterialWarehouseOutSearchDTO searchDTO) {
        return PageHelperUtils.limit(searchDTO, () -> mapper.selectList(searchDTO));
    }

    private Map<Long, BigDecimal> buildReservedQuantityMap(EMaterialWarehouseOutDetailPO outDetailPO) {
        Map<Long, BigDecimal> reservedQuantityMap = new HashMap<>();
        if (outDetailPO.getWarehouseOutId() == null || outDetailPO.getMaterialId() == null) {
            return reservedQuantityMap;
        }

        List<Map<String, Object>> reservedList = inOutRelMapper.selectReservedQuantitiesByWarehouseOutIdAndMaterial(
                outDetailPO.getWarehouseOutId(), outDetailPO.getMaterialId(), outDetailPO.getId());
        for (Map<String, Object> reserved : reservedList) {
            Long warehouseInDetailId = toLong(reserved.get("warehouseInDetailId"));
            BigDecimal reservedQuantity = toBigDecimal(reserved.get("reservedQuantity"));
            if (warehouseInDetailId == null || reservedQuantity == null) {
                continue;
            }
            reservedQuantityMap.put(warehouseInDetailId, reservedQuantity);
        }
        return reservedQuantityMap;
    }

    private Long toLong(Object value) {
        return value instanceof Number ? ((Number) value).longValue() : null;
    }

    private BigDecimal toBigDecimal(Object value) {
        if (value instanceof BigDecimal) {
            return (BigDecimal) value;
        }
        if (value instanceof Number) {
            return BigDecimal.valueOf(((Number) value).doubleValue());
        }
        return null;
    }

    /**
     * 根据ID查询物资出库（包含明细）
     */
    @Override
    public EMaterialWarehouseOutDTO getById(Long id) {
        EMaterialWarehouseOutDTO dto = mapper.selectById(id);
        if (dto != null) {
            // 查询明细列表
            List<EMaterialWarehouseOutDetailDTO> detailList = detailMapper.selectListByWarehouseOutId(id);
            dto.setDetailList(detailList);
        }
        return dto;
    }

    /**
     * 新增或修改物资出库
     */
    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public void save(EMaterialWarehouseOutDTO dto) {
        EMaterialWarehouseOutPO po = new EMaterialWarehouseOutPO();
        BeanUtils.copyProperties(dto, po);
        if (dto.getId() == null) {
            po.setId(snowflake.nextId());
            String warehouseOutNo = commonService.generateSerialNumber(SerialNumberPrefixEnum.WAREHOUSE_OUT);

            // 验证出库单号是否重复，最多重试10次
            int count = mapper.countByWarehouseOutNo(warehouseOutNo, null);
            int maxRetries = 10; // 最多重试10次
            int retries = 0;
            while (count > 0 && retries < maxRetries) {
                // 如果单号已存在，重新生成
                warehouseOutNo = commonService.generateSerialNumber(SerialNumberPrefixEnum.WAREHOUSE_OUT);
                count = mapper.countByWarehouseOutNo(warehouseOutNo, null);
                retries++;
            }
            if (count > 0) {
                throw new BusinessRuntimeException("出库单号生成失败，请重试");
            }

            po.setWarehouseOutNo(warehouseOutNo);
            dto.setWarehouseOutNo(warehouseOutNo);
            // 新增时默认状态为0（待确认）
            if (po.getStatus() == null) {
                po.setStatus(0);
            }

            // 插入主表
            mapper.insert(po);
            // 保存明细并建立入库出库关系（但不更新入库明细数量，等确认时再更新）
            if (dto.getDetailList() != null && !dto.getDetailList().isEmpty()) {
                saveDetailListWithRelationOnly(po.getId(), dto.getWarehouseId(), dto.getDetailList());
            }
        } else {
            mapper.update(po);
            // 处理明细：新增、修改、删除
            // 检查当前状态，如果已确认，需要恢复和重新建立关系；如果未确认，只保存明细
            EMaterialWarehouseOutDTO existingDto = getById(po.getId());
            if (existingDto != null && existingDto.getStatus() != null && existingDto.getStatus() == 1) {
                // 已确认状态：先恢复已删除明细对应的入库明细数量，删除旧的关系记录，然后保存明细并重新建立关系
                restoreInDetailQuantity(po.getId());
                deleteOldRelations(po.getId());
                saveDetailListWithRelation(po.getId(), dto.getWarehouseId(), dto.getDetailList());
            } else {
                // 未确认状态：保存明细并建立入库出库关系（但不更新入库明细数量）
                saveDetailListWithRelationOnlyForUpdate(po.getId(), dto.getWarehouseId(), dto.getDetailList());
            }
        }
    }

    /**
     * 保存明细列表并建立入库出库关系（但不更新入库明细数量）
     * 用于新增时保存明细并建立关系，等确认时再更新入库明细数量
     */
    private void saveDetailListWithRelationOnly(Long warehouseOutId, Long warehouseId, List<EMaterialWarehouseOutDetailDTO> detailList) {
        if (detailList == null || detailList.isEmpty()) {
            return;
        }

        // 收集需要新增的明细
        List<EMaterialWarehouseOutDetailPO> newDetailList = new ArrayList<>();

        for (EMaterialWarehouseOutDetailDTO detailDTO : detailList) {
            EMaterialWarehouseOutDetailPO detailPO = new EMaterialWarehouseOutDetailPO();
            BeanUtils.copyProperties(detailDTO, detailPO);
            detailPO.setWarehouseOutId(warehouseOutId);
            detailPO.setLoginUserId(securityUtils.getLoginUserId());
            detailPO.setLoginUserName(securityUtils.getLoginUserName());

            // 新增明细
            detailPO.setId(snowflake.nextId());
            newDetailList.add(detailPO);
        }

        // 批量插入新增的明细
        if (!newDetailList.isEmpty()) {
            detailMapper.batchInsert(newDetailList);
        }

        // 为所有明细建立入库出库关系（但不更新入库明细数量）
        for (EMaterialWarehouseOutDetailPO detailPO : newDetailList) {
            allocateInDetailsOnly(warehouseId, detailPO);
        }
    }

    /**
     * 保存明细列表（修改时使用，建立入库出库关系但不更新入库明细数量）
     * 用于未确认状态的修改，等确认时再更新数量
     */
    private void saveDetailListWithRelationOnlyForUpdate(Long warehouseOutId, Long warehouseId, List<EMaterialWarehouseOutDetailDTO> detailList) {
        if (detailList == null) {
            return;
        }

        // 查询数据库中原有的明细ID列表
        List<EMaterialWarehouseOutDetailDTO> existingDetails = detailMapper.selectListByWarehouseOutId(warehouseOutId);
        Set<Long> existingIds = existingDetails.stream()
                .map(EMaterialWarehouseOutDetailDTO::getId)
                .collect(Collectors.toSet());

        // 获取前端传来的明细ID列表
        Set<Long> incomingIds = detailList.stream()
                .filter(detail -> detail.getId() != null)
                .map(EMaterialWarehouseOutDetailDTO::getId)
                .collect(Collectors.toSet());

        // 找出需要删除的明细（数据库有但前端没有传）
        Set<Long> idsToDelete = existingIds.stream()
                .filter(id -> !incomingIds.contains(id))
                .collect(Collectors.toSet());

        // 删除明细（未确认状态，删除关系记录但不恢复入库明细数量）
        for (Long id : idsToDelete) {
            // 删除关系记录
            inOutRelMapper.deleteByWarehouseOutDetailId(id);
            // 删除出库明细
            detailMapper.deleteById(id);
        }

        // 收集需要新增和修改的明细
        List<EMaterialWarehouseOutDetailPO> newDetailList = new ArrayList<>();
        List<EMaterialWarehouseOutDetailPO> updateDetailList = new ArrayList<>();

        for (EMaterialWarehouseOutDetailDTO detailDTO : detailList) {
            EMaterialWarehouseOutDetailPO detailPO = new EMaterialWarehouseOutDetailPO();
            BeanUtils.copyProperties(detailDTO, detailPO);
            detailPO.setWarehouseOutId(warehouseOutId);
            detailPO.setLoginUserId(securityUtils.getLoginUserId());
            detailPO.setLoginUserName(securityUtils.getLoginUserName());

            if (detailPO.getId() == null) {
                // 新增明细
                detailPO.setId(snowflake.nextId());
                newDetailList.add(detailPO);
            } else {
                // 修改明细 - 删除旧的关系记录
                inOutRelMapper.deleteByWarehouseOutDetailId(detailPO.getId());
                updateDetailList.add(detailPO);
            }
        }

        // 批量插入新增的明细
        if (!newDetailList.isEmpty()) {
            detailMapper.batchInsert(newDetailList);
        }

        // 批量更新修改的明细
        for (EMaterialWarehouseOutDetailPO detailPO : updateDetailList) {
            detailMapper.update(detailPO);
        }

        // 为所有明细建立入库出库关系（但不更新入库明细数量）
        List<EMaterialWarehouseOutDetailPO> allDetailList = new ArrayList<>();
        allDetailList.addAll(newDetailList);
        allDetailList.addAll(updateDetailList);

        for (EMaterialWarehouseOutDetailPO detailPO : allDetailList) {
            allocateInDetailsOnly(warehouseId, detailPO);
        }
    }

    /**
     * 保存明细列表并建立入库出库关系，更新入库明细数量
     */
    private void saveDetailListWithRelation(Long warehouseOutId, Long warehouseId, List<EMaterialWarehouseOutDetailDTO> detailList) {
        if (detailList == null || detailList.isEmpty()) {
            return;
        }

        // 查询数据库中原有的明细ID列表（修改时使用）
        List<EMaterialWarehouseOutDetailDTO> existingDetails = detailMapper.selectListByWarehouseOutId(warehouseOutId);
        Set<Long> existingIds = existingDetails.stream()
                .map(EMaterialWarehouseOutDetailDTO::getId)
                .collect(Collectors.toSet());

        // 获取前端传来的明细ID列表
        Set<Long> incomingIds = detailList.stream()
                .filter(detail -> detail.getId() != null)
                .map(EMaterialWarehouseOutDetailDTO::getId)
                .collect(Collectors.toSet());

        // 找出需要删除的明细（数据库有但前端没有传）
        Set<Long> idsToDelete = existingIds.stream()
                .filter(id -> !incomingIds.contains(id))
                .collect(Collectors.toSet());

        // 删除明细（会触发恢复入库明细数量的逻辑）
        // 查询出库单状态，判断是否需要减少申请明细的已出库数量
        EMaterialWarehouseOutDTO warehouseOutDto = getById(warehouseOutId);
        boolean isConfirmed = warehouseOutDto != null && warehouseOutDto.getStatus() != null && warehouseOutDto.getStatus() == 1;
        
        for (Long id : idsToDelete) {
            // 从现有明细列表中查找要删除的明细
            EMaterialWarehouseOutDetailDTO outDetail = existingDetails.stream()
                .filter(d -> d.getId().equals(id))
                .findFirst()
                .orElse(null);
            
            if (outDetail != null && outDetail.getWarehouseOutAppDetailId() != null && outDetail.getOutQuantity() != null && isConfirmed) {
                // 减少申请明细的已出库数量（如果出库单已确认）
                outApplicationDetailMapper.subtractOutQuantitySum(
                    outDetail.getWarehouseOutAppDetailId(), 
                    outDetail.getOutQuantity()
                );
            }
            // 先恢复入库明细数量
            restoreInDetailQuantityByOutDetailId(id);
            // 删除关系记录
            inOutRelMapper.deleteByWarehouseOutDetailId(id);
            // 删除出库明细
            detailMapper.deleteById(id);
        }

        // 收集需要新增和修改的明细
        List<EMaterialWarehouseOutDetailPO> newDetailList = new ArrayList<>();
        List<EMaterialWarehouseOutDetailPO> updateDetailList = new ArrayList<>();

        for (EMaterialWarehouseOutDetailDTO detailDTO : detailList) {
            EMaterialWarehouseOutDetailPO detailPO = new EMaterialWarehouseOutDetailPO();
            BeanUtils.copyProperties(detailDTO, detailPO);
            detailPO.setWarehouseOutId(warehouseOutId);
            detailPO.setLoginUserId(securityUtils.getLoginUserId());
            detailPO.setLoginUserName(securityUtils.getLoginUserName());

            if (detailPO.getId() == null) {
                // 新增明细
                detailPO.setId(snowflake.nextId());
                newDetailList.add(detailPO);
            } else {
                // 修改明细 - 先恢复旧的数量
                // 从现有明细列表中查找要修改的明细
                EMaterialWarehouseOutDetailDTO existingDetail = existingDetails.stream()
                    .filter(d -> d.getId().equals(detailPO.getId()))
                    .findFirst()
                    .orElse(null);
                
                if (existingDetail != null && existingDetail.getWarehouseOutAppDetailId() != null && isConfirmed) {
                    // 如果出库单已确认，需要先减少旧的已出库数量
                    if (existingDetail.getOutQuantity() != null) {
                        outApplicationDetailMapper.subtractOutQuantitySum(
                            existingDetail.getWarehouseOutAppDetailId(), 
                            existingDetail.getOutQuantity()
                        );
                    }
                    // 然后增加新的已出库数量
                    if (detailPO.getOutQuantity() != null) {
                        outApplicationDetailMapper.addOutQuantitySum(
                            existingDetail.getWarehouseOutAppDetailId(), 
                            detailPO.getOutQuantity()
                        );
                    }
                }
                
                restoreInDetailQuantityByOutDetailId(detailPO.getId());
                // 删除旧的关系记录
                inOutRelMapper.deleteByWarehouseOutDetailId(detailPO.getId());
                updateDetailList.add(detailPO);
            }
        }

        // 批量插入新增的明细
        if (!newDetailList.isEmpty()) {
            detailMapper.batchInsert(newDetailList);
        }

        // 批量更新修改的明细
        for (EMaterialWarehouseOutDetailPO detailPO : updateDetailList) {
            detailMapper.update(detailPO);
        }

        // 为所有明细建立入库出库关系并更新入库明细数量
        List<EMaterialWarehouseOutDetailPO> allDetailList = new ArrayList<>();
        allDetailList.addAll(newDetailList);
        allDetailList.addAll(updateDetailList);

        for (EMaterialWarehouseOutDetailPO detailPO : allDetailList) {
            allocateInDetails(warehouseId, detailPO);
        }
    }

    /**
     * 为出库明细分配入库明细（FIFO原则）并建立关系（但不更新入库明细数量）
     * 用于新增时建立关系
     */
    private void allocateInDetailsOnly(Long warehouseId, EMaterialWarehouseOutDetailPO outDetailPO) {
        if (outDetailPO.getMaterialId() == null || outDetailPO.getOutQuantity() == null) {
            return;
        }

        // 查询可用的入库明细（按FIFO原则）
        List<EMaterialWarehouseInDetailDTO> availableInDetails = warehouseInDetailMapper.selectAvailableInDetails(
                outDetailPO.getMaterialId(), warehouseId);

        if (availableInDetails.isEmpty()) {
            throw new BusinessRuntimeException(
                    String.format("物资【%s】库存不足，无法出库", outDetailPO.getMaterialName()));
        }

        Map<Long, BigDecimal> reservedQuantityMap = buildReservedQuantityMap(outDetailPO);
        BigDecimal remainingOutQuantity = outDetailPO.getOutQuantity();
        List<EMaterialWarehouseInOutRelPO> relList = new ArrayList<>();

        // 按FIFO原则分配
        for (EMaterialWarehouseInDetailDTO inDetail : availableInDetails) {
            if (remainingOutQuantity.compareTo(BigDecimal.ZERO) <= 0) {
                break;
            }

            BigDecimal availableQuantity = (inDetail.getRemainingQuantity() != null ? inDetail.getRemainingQuantity() : BigDecimal.ZERO)
                    .subtract(reservedQuantityMap.getOrDefault(inDetail.getId(), BigDecimal.ZERO));
            if (availableQuantity.compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }
            BigDecimal allocateQuantity = remainingOutQuantity.min(availableQuantity);

            // 建立关系记录（但不更新入库明细数量）
            EMaterialWarehouseInOutRelPO relPO = new EMaterialWarehouseInOutRelPO();
            relPO.setId(snowflake.nextId());
            relPO.setWarehouseInDetailId(inDetail.getId());
            relPO.setWarehouseOutDetailId(outDetailPO.getId());
            relPO.setQuantity(allocateQuantity);
            relPO.setLoginUserId(securityUtils.getLoginUserId());
            relPO.setLoginUserName(securityUtils.getLoginUserName());
            relList.add(relPO);

            remainingOutQuantity = remainingOutQuantity.subtract(allocateQuantity);
        }

        // 检查是否分配完成
        if (remainingOutQuantity.compareTo(BigDecimal.ZERO) > 0) {
            throw new BusinessRuntimeException(
                    String.format("物资【%s】库存不足，无法出库", outDetailPO.getMaterialName()));
        }

        // 批量插入关系记录
        if (!relList.isEmpty()) {
            inOutRelMapper.batchInsert(relList);
        }
    }

    /**
     * 为出库明细分配入库明细（FIFO原则）并建立关系，同时更新入库明细数量
     * 用于确认时更新入库明细数量
     */
    private void allocateInDetails(Long warehouseId, EMaterialWarehouseOutDetailPO outDetailPO) {
        if (outDetailPO.getMaterialId() == null || outDetailPO.getOutQuantity() == null) {
            return;
        }

        // 查询关系记录（新增时已建立）
        List<EMaterialWarehouseInOutRelPO> relList = inOutRelMapper.selectListByWarehouseOutDetailId(outDetailPO.getId());
        if (relList.isEmpty()) {
            throw new BusinessRuntimeException(
                    String.format("物资【%s】未建立入库出库关系，无法确认", outDetailPO.getMaterialName()));
        }

        List<EMaterialWarehouseInDetailPO> updateInDetailList = new ArrayList<>();

        // 根据关系记录更新入库明细数量
        for (EMaterialWarehouseInOutRelPO rel : relList) {
            // 查询入库明细
            EMaterialWarehouseInDetailDTO inDetail = findInDetailById(rel.getWarehouseInDetailId());
            if (inDetail == null) {
                continue;
            }

            // 更新入库明细的已出库数量和未出库数量
            BigDecimal newRemainingQuantity = (inDetail.getRemainingQuantity() != null ? inDetail.getRemainingQuantity() : BigDecimal.ZERO)
                    .subtract(rel.getQuantity());
            
            // 校验库存不能减成负数
            if (newRemainingQuantity.compareTo(BigDecimal.ZERO) < 0) {
                throw new BusinessRuntimeException(
                        String.format("物资【%s】库存不足，无法确认出库。当前库存：%s，出库数量：%s", 
                                outDetailPO.getMaterialName() != null ? outDetailPO.getMaterialName() : "未知",
                                inDetail.getRemainingQuantity() != null ? inDetail.getRemainingQuantity() : BigDecimal.ZERO,
                                rel.getQuantity()));
            }
            
            EMaterialWarehouseInDetailPO updateInDetailPO = new EMaterialWarehouseInDetailPO();
            updateInDetailPO.setId(inDetail.getId());
            updateInDetailPO.setOutQuantity(
                    (inDetail.getOutQuantity() != null ? inDetail.getOutQuantity() : BigDecimal.ZERO)
                            .add(rel.getQuantity()));
            updateInDetailPO.setRemainingQuantity(newRemainingQuantity);
            updateInDetailPO.setLoginUserId(securityUtils.getLoginUserId());
            updateInDetailPO.setLoginUserName(securityUtils.getLoginUserName());
            updateInDetailList.add(updateInDetailPO);
        }

        // 批量更新入库明细数量
        if (!updateInDetailList.isEmpty()) {
            warehouseInDetailMapper.batchUpdateOutQuantity(updateInDetailList);
        }
    }

    /**
     * 恢复入库明细数量（根据出库明细ID）
     */
    private void restoreInDetailQuantityByOutDetailId(Long outDetailId) {
        // 查询关系记录
        List<EMaterialWarehouseInOutRelPO> relList = inOutRelMapper.selectListByWarehouseOutDetailId(outDetailId);
        if (relList.isEmpty()) {
            return;
        }

        // 收集需要恢复的入库明细
        List<EMaterialWarehouseInDetailPO> restoreList = new ArrayList<>();
        for (EMaterialWarehouseInOutRelPO rel : relList) {
            // 查询入库明细
            EMaterialWarehouseInDetailDTO inDetail = findInDetailById(rel.getWarehouseInDetailId());
            if (inDetail != null) {
                EMaterialWarehouseInDetailPO restorePO = new EMaterialWarehouseInDetailPO();
                restorePO.setId(inDetail.getId());
                restorePO.setOutQuantity(
                        (inDetail.getOutQuantity() != null ? inDetail.getOutQuantity() : BigDecimal.ZERO)
                                .subtract(rel.getQuantity()));
                restorePO.setRemainingQuantity(
                        (inDetail.getRemainingQuantity() != null ? inDetail.getRemainingQuantity() : BigDecimal.ZERO)
                                .add(rel.getQuantity()));
                restorePO.setLoginUserId(securityUtils.getLoginUserId());
                restorePO.setLoginUserName(securityUtils.getLoginUserName());
                restoreList.add(restorePO);
            }
        }

        // 批量更新入库明细数量
        if (!restoreList.isEmpty()) {
            warehouseInDetailMapper.batchUpdateOutQuantity(restoreList);
        }
    }

    /**
     * 根据ID查找入库明细
     */
    private EMaterialWarehouseInDetailDTO findInDetailById(Long inDetailId) {
        return warehouseInDetailMapper.selectById(inDetailId);
    }

    /**
     * 恢复入库明细数量（根据出库主表ID）
     */
    private void restoreInDetailQuantity(Long warehouseOutId) {
        // 查询所有出库明细
        List<EMaterialWarehouseOutDetailDTO> outDetailList = detailMapper.selectListByWarehouseOutId(warehouseOutId);
        for (EMaterialWarehouseOutDetailDTO outDetail : outDetailList) {
            restoreInDetailQuantityByOutDetailId(outDetail.getId());
        }
    }

    /**
     * 删除旧的关系记录（根据出库主表ID）
     */
    private void deleteOldRelations(Long warehouseOutId) {
        // 查询所有出库明细
        List<EMaterialWarehouseOutDetailDTO> outDetailList = detailMapper.selectListByWarehouseOutId(warehouseOutId);
        List<Long> outDetailIds = outDetailList.stream()
                .map(EMaterialWarehouseOutDetailDTO::getId)
                .collect(Collectors.toList());
        if (!outDetailIds.isEmpty()) {
            inOutRelMapper.deleteByWarehouseOutDetailIds(outDetailIds);
        }
    }

    /**
     * 删除物资出库
     */
    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public void deleteById(Long id) {
        // 检查出库单是否存在
        EMaterialWarehouseOutDTO dto = getById(id);
        if (dto == null) {
            throw new BusinessRuntimeException("出库单不存在");
        }
        // 已确认的出库单不能删除
        if (dto.getStatus() != null && dto.getStatus() == 1) {
            throw new BusinessRuntimeException("已确认的出库单不能删除");
        }
        
        // 未确认的出库单：只删除关系记录，不恢复入库明细数量（因为新增时没有更新入库明细数量）
        List<EMaterialWarehouseOutDetailDTO> outDetailList = detailMapper.selectListByWarehouseOutId(id);
        List<Long> outDetailIds = outDetailList.stream()
                .map(EMaterialWarehouseOutDetailDTO::getId)
                .collect(Collectors.toList());
        if (!outDetailIds.isEmpty()) {
            // 只删除关系记录，不恢复入库明细数量
            inOutRelMapper.deleteByWarehouseOutDetailIds(outDetailIds);
        }
        // 删除明细
        detailMapper.deleteByWarehouseOutId(id);
        // 再删除主表
        mapper.deleteById(id);
    }

    /**
     * 确认物资出库
     */
    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public void confirm(Long id) {
        EMaterialWarehouseOutDTO dto = getById(id);
        if (dto == null) {
            throw new BusinessRuntimeException("出库单不存在");
        }
        if (dto.getStatus() != null && dto.getStatus() == 1) {
            throw new BusinessRuntimeException("该出库单已确认，无需重复确认");
        }
        
        // 确认时更新入库明细数量（关系已在新增时建立）
        if (dto.getDetailList() != null && !dto.getDetailList().isEmpty()) {
            // 查询出库明细PO列表
            List<EMaterialWarehouseOutDetailDTO> detailList = detailMapper.selectListByWarehouseOutId(id);
            // 为所有明细更新入库明细数量（关系已在新增时建立）
            for (EMaterialWarehouseOutDetailDTO detailDTO : detailList) {
                EMaterialWarehouseOutDetailPO detailPO = new EMaterialWarehouseOutDetailPO();
                BeanUtils.copyProperties(detailDTO, detailPO);
                // 更新入库明细数量
                allocateInDetails(dto.getWarehouseId(), detailPO);
                
                // 更新申请明细的已出库数量
                if (detailDTO.getWarehouseOutAppDetailId() != null && detailDTO.getOutQuantity() != null) {
                    outApplicationDetailMapper.addOutQuantitySum(
                        detailDTO.getWarehouseOutAppDetailId(), 
                        detailDTO.getOutQuantity()
                    );
                }
            }
        }
        
        EMaterialWarehouseOutPO po = new EMaterialWarehouseOutPO();
        po.setId(id);
        po.setStatus(1); // 已确认
        po.setConfirmBy(securityUtils.getLoginUserId());
        po.setConfirmByName(securityUtils.getLoginUserName());
        po.setConfirmTime(new Date());
        
        mapper.update(po);
    }
}

