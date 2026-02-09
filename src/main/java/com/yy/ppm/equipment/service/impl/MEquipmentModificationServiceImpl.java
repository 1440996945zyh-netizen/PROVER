package com.yy.ppm.equipment.service.impl;

import cn.hutool.core.lang.Snowflake;
import com.yy.common.util.PageHelperUtils;
import com.yy.common.page.Pages;
import com.yy.ppm.equipment.bean.dto.MEquipmentModificationDTO;
import com.yy.ppm.equipment.bean.dto.MEquipmentModificationSearchDTO;
import com.yy.ppm.equipment.bean.po.MEquipmentModificationPO;
import com.yy.ppm.equipment.mapper.MEquipmentModificationMapper;
import com.yy.ppm.equipment.service.MEquipmentModificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.util.List;

/**
 * 设备改造记录Service业务层处理
 * @author system
 */
@RequiredArgsConstructor
@Service
public class MEquipmentModificationServiceImpl implements MEquipmentModificationService {

    @Resource
    private MEquipmentModificationMapper mapper;

    @Resource
    private Snowflake snowflake;

    /**
     * 查询设备改造记录列表（分页）
     */
    @Override
    public Pages<MEquipmentModificationDTO> getList(MEquipmentModificationSearchDTO searchDTO) {
        return PageHelperUtils.limit(searchDTO, () -> mapper.selectList(searchDTO));
    }

    /**
     * 根据ID查询设备改造记录
     */
    @Override
    public MEquipmentModificationDTO getById(Long id) {
        MEquipmentModificationPO po = mapper.selectById(id);
        if (po == null) {
            return null;
        }
        MEquipmentModificationDTO dto = new MEquipmentModificationDTO();
        BeanUtils.copyProperties(po, dto);
        return dto;
    }

    /**
     * 新增设备改造记录
     */
    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public void add(MEquipmentModificationDTO dto) {
        MEquipmentModificationPO po = new MEquipmentModificationPO();
        BeanUtils.copyProperties(dto, po);
        po.setId(snowflake.nextId());
        mapper.insert(po);
    }

    /**
     * 修改设备改造记录
     */
    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public void update(MEquipmentModificationDTO dto) {
        MEquipmentModificationPO po = new MEquipmentModificationPO();
        BeanUtils.copyProperties(dto, po);
        mapper.update(po);
    }

    /**
     * 删除设备改造记录
     */
    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public void delete(Long id) {
        MEquipmentModificationDTO dto = new MEquipmentModificationDTO();
        dto.setId(id);
        mapper.deleteById(dto);
    }

    /**
     * 批量删除设备改造记录
     */
    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public void deleteBatch(List<Long> ids) {
        mapper.deleteBatch(ids);
    }
}

