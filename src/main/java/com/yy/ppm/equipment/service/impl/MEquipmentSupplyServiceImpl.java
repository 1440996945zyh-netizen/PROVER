package com.yy.ppm.equipment.service.impl;

import cn.hutool.core.lang.Snowflake;
import com.yy.ppm.equipment.bean.dto.MEquipmentSupplyDTO;
import com.yy.ppm.equipment.bean.po.MEquipmentSupplyPO;
import com.yy.ppm.equipment.mapper.MEquipmentSupplyMapper;
import com.yy.ppm.equipment.service.MEquipmentSupplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;

/**
 * 设备供货信息Service业务层处理
 * @author system
 */
@RequiredArgsConstructor
@Service
public class MEquipmentSupplyServiceImpl implements MEquipmentSupplyService {

    @Resource
    private MEquipmentSupplyMapper mapper;

    @Resource
    private Snowflake snowflake;

    /**
     * 根据设备ID查询供货信息
     */
    @Override
    public MEquipmentSupplyDTO getByEquipId(Long equipId) {
        MEquipmentSupplyPO po = mapper.selectByEquipId(equipId);
        if (po == null) {
            return null;
        }
        MEquipmentSupplyDTO dto = new MEquipmentSupplyDTO();
        BeanUtils.copyProperties(po, dto);
        return dto;
    }

    /**
     * 保存设备供货信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public void save(MEquipmentSupplyDTO dto, Long equipId) {
        MEquipmentSupplyPO po = new MEquipmentSupplyPO();
        BeanUtils.copyProperties(dto, po);
        po.setEquipId(equipId);

        MEquipmentSupplyPO existing = mapper.selectByEquipId(equipId);
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
     * 删除设备供货信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public void deleteByEquipId(Long equipId) {
        MEquipmentSupplyDTO mEquipmentSupplyDTO = new MEquipmentSupplyDTO();
        mEquipmentSupplyDTO.setEquipId(equipId);
        mapper.deleteByEquipId(mEquipmentSupplyDTO);
    }
}

