package com.yy.ppm.produce.service.impl;

import cn.hutool.core.lang.Snowflake;
import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.common.util.PageHelperUtils;
import com.yy.common.util.SecurityUtils;
import com.yy.ppm.common.mapper.CommonMapper;
import com.yy.ppm.produce.bean.dto.TPrdVehicleReleaseDTO;
import com.yy.ppm.produce.bean.po.TPrdVehicleReleasePO;
import com.yy.ppm.produce.mapper.TPrdGroupMapper;
import com.yy.ppm.produce.mapper.TPrdVehicleReleaseMapper;
import com.yy.ppm.produce.service.TPrdVehicleReleaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;

@Service
public class TPrdVehicleReleaseServiceImpl implements TPrdVehicleReleaseService {

    @Autowired
    private TPrdVehicleReleaseMapper tPrdVehicleReleaseMapper;

    @Autowired
    private Snowflake snowflake;

    @Override
    public Pages<TPrdVehicleReleasePO> getList(TPrdVehicleReleaseDTO query, PageParameter parameter) {
        return PageHelperUtils.limit(parameter, () -> {
            return tPrdVehicleReleaseMapper.getList(query);
        });
    }

    public Pages<TPrdVehicleReleasePO> getSubList(TPrdVehicleReleaseDTO query, PageParameter parameter) {
        return PageHelperUtils.limit(parameter, () -> {
            return tPrdVehicleReleaseMapper.getSubList(query);
        });
    }

    @Override
    public boolean auditStatusById(Long id) {
        TPrdVehicleReleaseDTO dto = tPrdVehicleReleaseMapper.getById(id);
        dto.setStatus("1");
        return tPrdVehicleReleaseMapper.auditStatusById(dto) == 1;
    }

    @Override
    public boolean auditRevokeStatusById(Long id) {
        TPrdVehicleReleaseDTO dto = tPrdVehicleReleaseMapper.getById(id);
        dto.setStatus("0");
        return tPrdVehicleReleaseMapper.auditRevokeStatusById(dto)==1;
    }
}
