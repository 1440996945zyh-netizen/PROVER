package com.yy.ppm.equipment.service.impl;

import cn.hutool.core.lang.Snowflake;
import com.yy.ppm.equipment.bean.dto.MEquipmentSpecialDTO;
import com.yy.ppm.equipment.bean.po.MEquipmentSpecialPO;
import com.yy.ppm.equipment.mapper.MEquipmentSpecialMapper;
import com.yy.ppm.equipment.service.MEquipmentSpecialService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;

/**
 * 特种设备Service业务层处理
 * @author system
 */
@RequiredArgsConstructor
@Service
public class MEquipmentSpecialServiceImpl implements MEquipmentSpecialService {

    @Resource
    private MEquipmentSpecialMapper mapper;

    @Resource
    private Snowflake snowflake;

    /**
     * 根据设备ID查询特种设备信息
     */
    @Override
    public MEquipmentSpecialDTO getByEquipId(Long equipId) {
        MEquipmentSpecialPO po = mapper.selectByEquipId(equipId);
        if (po == null) {
            return null;
        }
        MEquipmentSpecialDTO dto = new MEquipmentSpecialDTO();
        BeanUtils.copyProperties(po, dto);
        return dto;
    }

    /**
     * 保存设备特种设备信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public void save(MEquipmentSpecialDTO dto, Long equipId) {
        MEquipmentSpecialPO po = new MEquipmentSpecialPO();
        BeanUtils.copyProperties(dto, po);
        po.setEquipId(equipId);

        MEquipmentSpecialPO existing = mapper.selectByEquipId(equipId);
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
     * 删除设备特种设备信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public void deleteByEquipId(Long equipId) {
        MEquipmentSpecialDTO mEquipmentSpecialDTO = new MEquipmentSpecialDTO();
        mEquipmentSpecialDTO.setEquipId(equipId);
        mapper.deleteByEquipId(mEquipmentSpecialDTO);
    }
}

