package com.yy.ppm.equipment.service.impl;


import cn.hutool.core.lang.Snowflake;
import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.common.util.PageHelperUtils;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.equipment.bean.dto.MEquipmentOperationDTO;
import com.yy.ppm.equipment.bean.po.MEquipmentOperationPO;
import com.yy.ppm.equipment.mapper.MEquipmentOperationMapper;
import com.yy.ppm.equipment.service.MEquipmentOperationService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MEquipmentOperationServiceImpl implements MEquipmentOperationService {

    @Autowired
    private MEquipmentOperationMapper mapper;
    @Resource
    private Snowflake snowflake;

    @Override
    public Pages<MEquipmentOperationPO> getList(MEquipmentOperationDTO searchDTO, PageParameter parameter) {
        Pages<MEquipmentOperationPO> pages = PageHelperUtils.limit(parameter, () -> {
            return mapper.getList(searchDTO);
        });
        return pages;
    }

    @Override
    public MEquipmentOperationPO getById(MEquipmentOperationDTO searchDTO) {
        MEquipmentOperationPO po = mapper.getById(searchDTO);
        return po;
    }

    @Override
    public void save(MEquipmentOperationPO po) {
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
