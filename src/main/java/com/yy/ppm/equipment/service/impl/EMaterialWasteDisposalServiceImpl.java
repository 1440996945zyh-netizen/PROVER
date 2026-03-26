package com.yy.ppm.equipment.service.impl;


import cn.hutool.core.lang.Snowflake;
import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.common.util.PageHelperUtils;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.common.enums.SerialNumberPrefixEnum;
import com.yy.ppm.common.service.impl.CommonServiceImpl;
import com.yy.ppm.equipment.bean.dto.EMEquipRepairContractDTO;
import com.yy.ppm.equipment.bean.dto.EMaterialWasteDisposalDTO;
import com.yy.ppm.equipment.bean.dto.MEquipmentInfoDTO;
import com.yy.ppm.equipment.mapper.EMEquipRepairContractMapper;
import com.yy.ppm.equipment.mapper.EMaterialWasteDisposalMapper;
import com.yy.ppm.equipment.mapper.MEquipmentInfoMapper;
import com.yy.ppm.equipment.service.EMaterialWasteDisposalService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EMaterialWasteDisposalServiceImpl implements EMaterialWasteDisposalService {

    @Autowired
    private EMaterialWasteDisposalMapper eMaterialWasteDisposalMapper;

    @Autowired
    CommonServiceImpl commonService;


    @Resource
    private Snowflake snowflake;

    @Override
    public Pages<EMaterialWasteDisposalDTO> getList(EMaterialWasteDisposalDTO searchDTO, PageParameter parameter) {
        Pages<EMaterialWasteDisposalDTO> pages = PageHelperUtils.limit(parameter, () -> {
            return eMaterialWasteDisposalMapper.getList(searchDTO);
        });
        return pages;
    }

    @Override
    public EMaterialWasteDisposalDTO getById(EMaterialWasteDisposalDTO searchDTO) {
        EMaterialWasteDisposalDTO po = eMaterialWasteDisposalMapper.getById(searchDTO);
        po.setList(eMaterialWasteDisposalMapper.getDetail(po.getId()));
        return po;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(EMaterialWasteDisposalDTO po) {
        // 新增
        if (po.getId() == null) {
            po.setId(snowflake.nextId());

            po.setDisposalNum(commonService.generateSerialNumber(SerialNumberPrefixEnum.WASTE_DISPOSAL));

            eMaterialWasteDisposalMapper.insert(po);

        } else {

            eMaterialWasteDisposalMapper.update(po);

            //删除子表
            eMaterialWasteDisposalMapper.deleteDetail(po.getId());
        }

        //新增子表数据
        po.getList().forEach(item -> {
            item.setId(snowflake.nextId());
            item.setParentId(po.getId());
        });
        eMaterialWasteDisposalMapper.insertDetail(po.getList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        if (id == null) {
            throw new BusinessRuntimeException("请选择一条数据删除");
        }

        eMaterialWasteDisposalMapper.deleteById(id);
        eMaterialWasteDisposalMapper.deleteDetail(id);
    }

}
