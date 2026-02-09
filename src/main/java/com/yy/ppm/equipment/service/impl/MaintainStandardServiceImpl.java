package com.yy.ppm.equipment.service.impl;

import cn.hutool.core.lang.Snowflake;
import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.common.util.PageHelperUtils;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.equipment.bean.dto.InspectionStandardDTO;
import com.yy.ppm.equipment.bean.dto.MaintainStandardDTO;
import com.yy.ppm.equipment.bean.po.InspectionStandardPO;
import com.yy.ppm.equipment.bean.po.MaintainStandardPO;
import com.yy.ppm.equipment.mapper.InspectionStandardMapper;
import com.yy.ppm.equipment.mapper.MaintainStandardMapper;
import com.yy.ppm.equipment.service.InspectionStandardService;
import com.yy.ppm.equipment.service.MaintainStandardService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
public class MaintainStandardServiceImpl implements MaintainStandardService {
    @Resource
    private Snowflake snowflake;

    @Autowired
    private MaintainStandardMapper maintainStandardMapper;

    @Override
    public List<MaintainStandardPO> queryByUnitId(MaintainStandardDTO maintainStandardDTO) {
        List<MaintainStandardPO> list = maintainStandardMapper.queryByUnitId(maintainStandardDTO);
        return list;
    }

    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public void save(MaintainStandardDTO dto) {
        // 先删后增
        maintainStandardMapper.deleteByUnitId(dto);

        // 查询父节点内容
        MaintainStandardPO maintainStandardPO = maintainStandardMapper.queryParentByUnitId(dto);
        if (maintainStandardPO == null) {
            throw new BusinessRuntimeException("未查询到该部件的上级类型");
        }

        dto.getList().stream().forEach(v -> {
            v.setId(snowflake.nextId());
            v.setEquipUnitId(dto.getEquipUnitId());
            v.setEquipUnitName(dto.getEquipUnitName());
            v.setEquipInstitutionId(maintainStandardPO.getEquipInstitutionId());
            v.setEquipInstitutionName(maintainStandardPO.getEquipInstitutionName());
            v.setEquipSmallCategoryId(maintainStandardPO.getEquipSmallCategoryId());
            v.setEquipSmallCategoryName(maintainStandardPO.getEquipSmallCategoryName());
        });
        maintainStandardMapper.save(dto.getList());
    }

    @Override
    public Pages<MaintainStandardPO> queryAll(MaintainStandardDTO maintainStandardDTO, PageParameter parameter) {
        Pages<MaintainStandardPO> pages = PageHelperUtils.limit(parameter, () -> {
            return maintainStandardMapper.queryAll(maintainStandardDTO);
        });
        return pages;
    }
}
