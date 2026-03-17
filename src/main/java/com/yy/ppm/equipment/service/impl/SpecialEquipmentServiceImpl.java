package com.yy.ppm.equipment.service.impl;

import com.yy.common.page.Pages;
import com.yy.common.util.PageHelperUtils;
import com.yy.ppm.equipment.bean.dto.SpecialEquipmentDTO;
import com.yy.ppm.equipment.bean.dto.SpecialEquipmentSearchDTO;
import com.yy.ppm.equipment.mapper.SpecialEquipmentMapper;
import com.yy.ppm.equipment.service.SpecialEquipmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

/**
 * 特种设备查询Service业务层处理
 * @author system
 */
@RequiredArgsConstructor
@Service
public class SpecialEquipmentServiceImpl implements SpecialEquipmentService {

    @Resource
    private SpecialEquipmentMapper mapper;

    /**
     * 查询特种设备列表（分页）
     */
    @Override
    public Pages<SpecialEquipmentDTO> getList(SpecialEquipmentSearchDTO searchDTO) {
        Pages<SpecialEquipmentDTO> pages = PageHelperUtils.limit(searchDTO, () -> {
            return mapper.selectList(searchDTO);
        });
        return pages;
    }
}

