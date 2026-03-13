package com.yy.ppm.equipment.service.impl;


import cn.hutool.core.lang.Snowflake;
import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.common.util.PageHelperUtils;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.equipment.bean.dto.EMEquipRepairContractDTO;
import com.yy.ppm.equipment.bean.dto.MEquipmentInfoDTO;
import com.yy.ppm.equipment.bean.dto.MEquipmentOperationDTO;
import com.yy.ppm.equipment.bean.po.MEquipmentOperationPO;
import com.yy.ppm.equipment.mapper.EMEquipRepairContractMapper;
import com.yy.ppm.equipment.mapper.MEquipmentInfoMapper;
import com.yy.ppm.equipment.mapper.MEquipmentOperationMapper;
import com.yy.ppm.equipment.service.EMEquipRepairContractService;
import com.yy.ppm.equipment.service.MEquipmentOperationService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EMEquipRepairContractServiceImpl implements EMEquipRepairContractService {

    @Autowired
    private EMEquipRepairContractMapper mapper;

    @Resource
    private MEquipmentInfoMapper mEquipmentInfoMapper;

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
    public EMEquipRepairContractDTO getOutTypeNum() {
        EMEquipRepairContractDTO po = mapper.getOutTypeNum();
        return po;
    }

    @Override
    public List<EMEquipRepairContractDTO> queryUnitName(EMEquipRepairContractDTO searchDTO) {

        List<EMEquipRepairContractDTO> list = mapper.queryUnitName(searchDTO);
        return list;
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
        int count =mapper.getUser( id);
        if (count >0) {
            throw new BusinessRuntimeException("已有维修人员，不可删除");
        }
        mapper.deleteById(id);
    }

    @Override
    public List<EMEquipRepairContractDTO> getRepairContractByEquipId(Long equipId, String outType) {
        // 1. 查询设备表获取使用单位ID
        MEquipmentInfoDTO equipmentInfo = mEquipmentInfoMapper.selectById(equipId);
        if (equipmentInfo == null) {
            throw new BusinessRuntimeException("设备不存在");
        }
        Long useCompanyId = equipmentInfo.getUseCompanyId();
        if (useCompanyId == null) {
            throw new BusinessRuntimeException("设备未配置使用单位");
        }

        // 2. 根据所属单位ID和outType查询维修单位列表
        return mapper.getByCompanyIdAndOutType(useCompanyId, outType);
    }
}
