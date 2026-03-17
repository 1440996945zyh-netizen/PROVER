package com.yy.ppm.equipment.service.impl;


import cn.hutool.core.lang.Snowflake;
import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.common.util.PageHelperUtils;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.equipment.bean.dto.EContractInfoDTO;
import com.yy.ppm.equipment.bean.dto.EMEquipRepairContractDTO;
import com.yy.ppm.equipment.bean.dto.MEquipmentInfoDTO;
import com.yy.ppm.equipment.mapper.EContractInfoMapper;
import com.yy.ppm.equipment.mapper.EMEquipRepairContractMapper;
import com.yy.ppm.equipment.mapper.MEquipmentInfoMapper;
import com.yy.ppm.equipment.service.EContractInfoService;
import com.yy.ppm.equipment.service.EMEquipRepairContractService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EContractInfoServiceImpl implements EContractInfoService {

    @Autowired
    private EContractInfoMapper mapper;

    @Resource
    private MEquipmentInfoMapper mEquipmentInfoMapper;

    @Resource
    private Snowflake snowflake;

    @Override
    public Pages<EContractInfoDTO> getList(EContractInfoDTO searchDTO, PageParameter parameter) {
        Pages<EContractInfoDTO> pages = PageHelperUtils.limit(parameter, () -> {
            return mapper.getList(searchDTO);
        });
        return pages;
    }

    @Override
    public EContractInfoDTO getById(EContractInfoDTO searchDTO) {
        EContractInfoDTO po = mapper.getById(searchDTO);
        return po;
    }


    @Override
    public void save(EContractInfoDTO po) {
        // 新增
        if (po.getId() == null) {
            po.setId(snowflake.nextId());
            mapper.insert(po);
        } else {
            mapper.update(po);
        }
    }

    @Override
    public void delete(Long id) {
        if (id == null) {
            throw new BusinessRuntimeException("请选择一条数据删除");
        }
        mapper.deleteById(id);
    }


}
