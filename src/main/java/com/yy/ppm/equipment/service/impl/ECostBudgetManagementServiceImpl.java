package com.yy.ppm.equipment.service.impl;

import cn.hutool.core.lang.Snowflake;
import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.common.util.PageHelperUtils;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.equipment.bean.dto.ECostBudgetManagementDTO;
import com.yy.ppm.equipment.mapper.ECostBudgetManagementMapper;
import com.yy.ppm.equipment.service.ECostBudgetManagementService;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * 预算管理 Service 实现
 */
@RequiredArgsConstructor
@Service
public class ECostBudgetManagementServiceImpl implements ECostBudgetManagementService {

    @Resource
    private ECostBudgetManagementMapper mapper;

    @Resource
    private Snowflake snowflake;

    @Override
    public Pages<ECostBudgetManagementDTO> list(ECostBudgetManagementDTO searchDTO, PageParameter parameter) {
        ECostBudgetManagementDTO dto = (searchDTO == null) ? new ECostBudgetManagementDTO() : searchDTO;
        return PageHelperUtils.limit(parameter, () -> mapper.selectList(dto));
    }

    @Override
    public ECostBudgetManagementDTO get(Long id) {
        if (id == null) {
            throw new BusinessRuntimeException("id不能为空");
        }
        return mapper.selectById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public void add(ECostBudgetManagementDTO dto) {
        if (dto == null) {
            throw new BusinessRuntimeException("参数不能为空");
        }
        validate(dto, false);
        if (dto.getId() == null) {
            dto.setId(snowflake.nextId());
        }
        mapper.add(dto);
    }

    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public void update(ECostBudgetManagementDTO dto) {
        if (dto == null || dto.getId() == null) {
            throw new BusinessRuntimeException("id不能为空");
        }
        validate(dto, true);
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

    private void validate(ECostBudgetManagementDTO dto, boolean isUpdate) {
        if (StringUtils.isBlank(dto.getYear())) {
            throw new BusinessRuntimeException("年份不能为空");
        }
        if (!dto.getYear().matches("^\\d{4}$")) {
            throw new BusinessRuntimeException("年份格式不正确，请输入4位年份");
        }
        if (StringUtils.isBlank(dto.getCostType())) {
            throw new BusinessRuntimeException("费用类型不能为空");
        }
        if (dto.getAmount() == null) {
            throw new BusinessRuntimeException("预算金额不能为空");
        }
        if (dto.getAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessRuntimeException("预算金额不能小于0");
        }

        Long count = mapper.countDuplicate(dto.getYear(), dto.getCostType(), isUpdate ? dto.getId() : null);
        if (count != null && count > 0) {
            throw new BusinessRuntimeException("同一个年份下不允许费用类型相同");
        }
    }
}
