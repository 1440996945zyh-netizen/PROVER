package com.yy.ppm.equipment.service.impl;

import com.yy.common.page.Pages;
import com.yy.common.util.PageHelperUtils;
import com.yy.ppm.equipment.bean.dto.MEquipmentChangeLogDTO;
import com.yy.ppm.equipment.bean.dto.MEquipmentChangeLogSearchDTO;
import com.yy.ppm.equipment.mapper.MEquipmentChangeLogDetailMapper;
import com.yy.ppm.equipment.mapper.MEquipmentChangeLogMapper;
import com.yy.ppm.equipment.service.MEquipmentChangeLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.List;

/**
 * 设备变更记录Service业务层处理
 * @author system
 */
@RequiredArgsConstructor
@Service
public class MEquipmentChangeLogServiceImpl implements MEquipmentChangeLogService {

    @Resource
    private MEquipmentChangeLogMapper mapper;

    @Resource
    private MEquipmentChangeLogDetailMapper detailMapper;

    /**
     * 查询变更记录列表（分页）
     */
    @Override
    public Pages<MEquipmentChangeLogDTO> getList(MEquipmentChangeLogSearchDTO searchDTO) {
        return PageHelperUtils.limit(searchDTO, () -> mapper.selectList(searchDTO.getEquipId(), searchDTO.getChangeType()));
    }

    /**
     * 根据ID查询变更记录详情（包含子表数据）
     */
    @Override
    public MEquipmentChangeLogDTO getById(Long id) {
        MEquipmentChangeLogDTO dto = mapper.selectById(id);
        if (dto != null) {
            // 查询子表数据
            List<com.yy.ppm.equipment.bean.dto.MEquipmentChangeLogDetailDTO> detailList = 
                detailMapper.selectByChangeLogId(id);
            dto.setDetailList(detailList);
        }
        return dto;
    }
}

