package com.yy.ppm.equipment.service.impl;


import cn.hutool.core.lang.Snowflake;
import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.common.util.PageHelperUtils;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.equipment.bean.dto.EMEquipRepairContractDTO;
import com.yy.ppm.equipment.bean.dto.MEquipmentOperationDTO;
import com.yy.ppm.equipment.bean.po.MEquipmentOperationPO;
import com.yy.ppm.equipment.mapper.EMEquipRepairContractMapper;
import com.yy.ppm.equipment.mapper.MEquipmentOperationMapper;
import com.yy.ppm.equipment.service.EMEquipRepairContractService;
import com.yy.ppm.equipment.service.MEquipmentOperationService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EMEquipRepairContractServiceImpl implements EMEquipRepairContractService {

    @Autowired
    private EMEquipRepairContractMapper mapper;
    @Resource
    private Snowflake snowflake;

    @Override
    public Pages<EMEquipRepairContractDTO> getList(EMEquipRepairContractDTO searchDTO, PageParameter parameter) {
        Pages<EMEquipRepairContractDTO> pages = PageHelperUtils.limit(parameter, () -> {
            return mapper.getList(searchDTO);
        });
        return pages;
    }

    @Override
    public EMEquipRepairContractDTO getById(EMEquipRepairContractDTO searchDTO) {
        EMEquipRepairContractDTO po = mapper.getById(searchDTO);
        return po;
    }

    @Override
    public void save(EMEquipRepairContractDTO po) {
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
