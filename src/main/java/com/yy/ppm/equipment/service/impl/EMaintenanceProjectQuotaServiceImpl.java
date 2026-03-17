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
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 维修定额项目Service实现
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
        EMaintenanceProjectQuotaDTO dto = (searchDTO == null) ? new EMaintenanceProjectQuotaDTO() : searchDTO;
        return PageHelperUtils.limit(parameter, () -> mapper.selectList(dto));
    }

    @Override
    public EMaintenanceProjectQuotaDTO get(Long id) {
        if (id == null) {
            throw new BusinessRuntimeException("id不能为空");
        }
        return mapper.selectById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public void add(EMaintenanceProjectQuotaDTO dto) {
        if (dto == null) {
            throw new BusinessRuntimeException("参数不能为空");
        }
        if (dto.getId() == null) {
            dto.setId(snowflake.nextId());
        }
        // 自动生成定额编号
        dto.setQuotaCode(generateCode());
        // 新增状态默认生效，并校验状态值
        fillAndCheckStatus(dto, true);
        mapper.add(dto);
    }

    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public void update(EMaintenanceProjectQuotaDTO dto) {
        if (dto == null || dto.getId() == null) {
            throw new BusinessRuntimeException("id不能为空");
        }
        // 修改时状态必填，并校验状态值
        fillAndCheckStatus(dto, false);
        mapper.update(dto);
    }

    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public void delete(Long id) {
        if (id == null) {
            throw new BusinessRuntimeException("id不能为空");
        }
        mapper.delete(id);
    }

    /**
     * 自动生成定额编号：DE-YYYY-MM-DD-0001
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

    /**
     * 状态兜底与合法性校验
     * 1 = 生效
     * 0 = 失效
     */
    private void fillAndCheckStatus(EMaintenanceProjectQuotaDTO dto, boolean allowDefault) {
        if (StringUtils.isBlank(dto.getStatus())) {
            if (allowDefault) {
                dto.setStatus("1");
            } else {
                throw new BusinessRuntimeException("状态不能为空");
            }
        }

        if (!"1".equals(dto.getStatus()) && !"0".equals(dto.getStatus())) {
            throw new BusinessRuntimeException("状态值非法");
        }
    }
}