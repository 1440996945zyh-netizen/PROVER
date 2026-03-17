package com.yy.ppm.equipment.service.impl;

import cn.hutool.core.lang.Snowflake;
import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.common.util.PageHelperUtils;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.equipment.bean.dto.InspectionStandardDTO;
import com.yy.ppm.equipment.bean.dto.MEquipmentTypeDTO;
import com.yy.ppm.equipment.bean.po.InspectionPlanPO;
import com.yy.ppm.equipment.bean.po.InspectionStandardPO;
import com.yy.ppm.equipment.mapper.InspectionStandardMapper;
import com.yy.ppm.equipment.service.InspectionStandardService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
public class InspectionStandardServiceImpl implements InspectionStandardService {
    @Resource
    private Snowflake snowflake;

    @Autowired
    private InspectionStandardMapper inspectionStandardMapper;

    @Override
    public List<InspectionStandardPO> queryByUnitId(InspectionStandardDTO inspectionStandardDTO) {
        List<InspectionStandardPO> list = inspectionStandardMapper.queryByUnitId(inspectionStandardDTO);
        return list;
    }

    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public void save(InspectionStandardDTO dto) {
        // 先删后增
        inspectionStandardMapper.deleteByUnitId(dto);

        // 查询父节点内容
        InspectionStandardPO inspectionStandardPO = inspectionStandardMapper.queryParentByUnitId(dto);
        if (inspectionStandardPO == null) {
            throw new BusinessRuntimeException("未查询到该部件的上级类型");
        }

        dto.getList().stream().forEach(v -> {
            v.setId(snowflake.nextId());
            v.setEquipUnitId(dto.getEquipUnitId());
            v.setEquipUnitName(dto.getEquipUnitName());
            v.setEquipInstitutionId(inspectionStandardPO.getEquipInstitutionId());
            v.setEquipInstitutionName(inspectionStandardPO.getEquipInstitutionName());
            v.setEquipSmallCategoryId(inspectionStandardPO.getEquipSmallCategoryId());
            v.setEquipSmallCategoryName(inspectionStandardPO.getEquipSmallCategoryName());
        });
        inspectionStandardMapper.save(dto.getList());
    }

    @Override
    public Pages<InspectionStandardPO> queryAll(InspectionStandardDTO inspectionStandardDTO, PageParameter parameter) {
        Pages<InspectionStandardPO> pages = PageHelperUtils.limit(parameter, () -> {
            return inspectionStandardMapper.queryAll(inspectionStandardDTO);
        });
        return pages;
    }
}
