package com.yy.ppm.equipment.service.impl;
import cn.hutool.core.lang.Snowflake;
import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.common.util.PageHelperUtils;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.equipment.bean.dto.EMaintenanceProjectQuotaDTO;
import com.yy.ppm.equipment.mapper.EMaintenanceProjectQuotaMapper;
import com.yy.ppm.equipment.service.EMaintenanceProjectQuotaService;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.github.pagehelper.Page;

import java.text.SimpleDateFormat;


import java.util.Date;


/**
 * 维修定额项目Service实现
 * 说明：
 * 1) getList 使用 PageHelperUtils.limit 做分页封装，返回 Pages
 * 2) save 新增时自动生成定额编号：DE-YYYY-MM-DD-0001，并自动写入创建/更新信息
 */

@RequiredArgsConstructor
@Service
public class EMaintenanceProjectQuotaServiceImpl implements EMaintenanceProjectQuotaService {
    @Resource
    private EMaintenanceProjectQuotaMapper mapper;
    @Resource
    private Snowflake snowflake;
    @Override
    public Pages<EMaintenanceProjectQuotaDTO> list(EMaintenanceProjectQuotaDTO searchDTO, PageParameter parameter) {
        EMaintenanceProjectQuotaDTO dto = (searchDTO == null ? new EMaintenanceProjectQuotaDTO() : searchDTO);
        return PageHelperUtils.limit(parameter, () -> (Page<EMaintenanceProjectQuotaDTO>) mapper.selectList(dto));
    }
    @Override
    public EMaintenanceProjectQuotaDTO getById(Long id) {
        if (id == null) {
            throw new BusinessRuntimeException("id不能为空");
        }
        return mapper.selectById(id);
    }
    @Override
    public void add(EMaintenanceProjectQuotaDTO dto) {
        if (dto == null) {
            throw new BusinessRuntimeException("入参不能为空");
        }
        if (dto.getProjectName() == null || dto.getProjectName().trim().isEmpty()) {
            throw new BusinessRuntimeException("维修项目名称不能为空");
        }
        if (dto.getId() == null) {
            dto.setId(snowflake.nextId());
        }
        dto.setQuotaCode(generateCode());
        mapper.insert(dto);
    }
    @Override
    public void update(EMaintenanceProjectQuotaDTO dto) {
        if (dto == null || dto.getId() == null) {
            throw new BusinessRuntimeException("id不能为空");
        }
        mapper.update(dto);
    }
    @Override
    public void delete(Long id) {
        if (id == null) {
            throw new BusinessRuntimeException("id不能为空");
        }
        mapper.delete(id);
    }
    /**
     * 生成定额编号：DE-YYYY-MM-DD-0001
     * 规则：取当天最大编号后四位+1
     */
    private String generateCode() {
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String maxCode = mapper.selectMaxCodeToday();
        int number = 1;
        if (maxCode != null && !maxCode.isBlank()) {
            int idx = maxCode.lastIndexOf('-');
            if (idx > -1 && idx < maxCode.length() - 1) {
                String seqStr = maxCode.substring(idx + 1);
                try {
                    number = Integer.parseInt(seqStr) + 1;
                } catch (NumberFormatException ignore) {
                    number = 1;
                }
            }
        }
        return "DE-" + date + "-" + String.format("%04d", number);
    }
}