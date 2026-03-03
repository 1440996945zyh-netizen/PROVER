package com.yy.ppm.equipment.service.impl;


import cn.hutool.core.lang.Snowflake;
import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.common.util.PageHelperUtils;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.equipment.bean.dto.MEquipmentOperationDTO;
import com.yy.ppm.equipment.bean.po.MEquipmentOperationPO;
import com.yy.ppm.equipment.mapper.MEquipmentOperationMapper;
import com.yy.ppm.equipment.mapper.MEquipmentOperationSumMapper;
import com.yy.ppm.equipment.service.MEquipmentOperationService;
import com.yy.ppm.equipment.service.MEquipmentOperationSumService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MEquipmentOperationSumServiceImpl implements MEquipmentOperationSumService {

    @Autowired
    private MEquipmentOperationSumMapper mapper;
    @Resource
    private Snowflake snowflake;

    @Override
    public Pages<MEquipmentOperationDTO> getList(MEquipmentOperationDTO searchDTO, PageParameter parameter) {
        Pages<MEquipmentOperationDTO> pages = PageHelperUtils.limit(parameter, () -> {
            return mapper.getList(searchDTO);
        });

        return pages;
    }

}
