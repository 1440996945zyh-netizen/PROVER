package com.yy.ppm.equipment.service.impl;

import cn.hutool.core.lang.Snowflake;
import com.yy.ppm.equipment.bean.dto.MEquipmentFinanceDTO;
import com.yy.ppm.equipment.bean.po.MEquipmentFinancePO;
import com.yy.ppm.equipment.mapper.MEquipmentFinanceMapper;
import com.yy.ppm.equipment.service.MEquipmentFinanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;

/**
 * 设备财务信息Service业务层处理
 * @author system
 */
@RequiredArgsConstructor
@Service
public class MEquipmentFinanceServiceImpl implements MEquipmentFinanceService {

    @Resource
    private MEquipmentFinanceMapper mapper;

    @Resource
    private Snowflake snowflake;

    /**
     * 根据设备ID查询财务信息
     */
    @Override
    public MEquipmentFinanceDTO getByEquipId(Long equipId) {
        MEquipmentFinancePO po = mapper.selectByEquipId(equipId);
        if (po == null) {
            return null;
        }
        MEquipmentFinanceDTO dto = new MEquipmentFinanceDTO();
        BeanUtils.copyProperties(po, dto);
        return dto;
    }

    /**
     * 保存设备财务信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public void save(MEquipmentFinanceDTO dto, Long equipId) {
        MEquipmentFinancePO po = new MEquipmentFinancePO();
        BeanUtils.copyProperties(dto, po);
        po.setEquipId(equipId);

        MEquipmentFinancePO existing = mapper.selectByEquipId(equipId);
        if (existing == null) {
            // 新增
            po.setId(snowflake.nextId());
            mapper.insert(po);
        } else {
            // 修改
            po.setId(existing.getId());
            mapper.update(po);
        }
    }

    /**
     * 删除设备财务信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public void deleteByEquipId(Long equipId) {
        MEquipmentFinancePO po = new MEquipmentFinancePO();
        po.setEquipId(equipId);
        mapper.deleteByEquipId(po);
    }
}

