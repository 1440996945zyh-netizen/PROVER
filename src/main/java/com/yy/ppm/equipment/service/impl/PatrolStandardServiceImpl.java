package com.yy.ppm.equipment.service.impl;

import cn.hutool.core.lang.Snowflake;
import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.common.util.PageHelperUtils;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.equipment.bean.dto.PatrolStandardDTO;
import com.yy.ppm.equipment.bean.po.PatrolStandardSubPO;
import com.yy.ppm.equipment.mapper.PatrolStandardMapper;
import com.yy.ppm.equipment.service.PatrolStandardService;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

/**
 * 巡检标准
 */
@Service
public class PatrolStandardServiceImpl implements PatrolStandardService {

    @Resource
    private PatrolStandardMapper patrolStandardMapper;

    @Resource
    private Snowflake snowflake;

    @Override
    public Pages<PatrolStandardDTO> getList(PatrolStandardDTO searchDTO, PageParameter parameter) {
        PatrolStandardDTO dto = searchDTO == null ? new PatrolStandardDTO() : searchDTO;
        return PageHelperUtils.limit(parameter, () -> patrolStandardMapper.selectList(dto));
    }

    @Override
    public PatrolStandardDTO getById(Long id) {
        if (id == null) {
            throw new BusinessRuntimeException("id不能为空");
        }
        PatrolStandardDTO dto = patrolStandardMapper.selectById(id);
        if (dto == null) {
            return null;
        }
        dto.setSubList(patrolStandardMapper.selectSubListByParentId(id));
        return dto;
    }

    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public void add(PatrolStandardDTO dto) {
        checkParam(dto, false);
        dto.setId(snowflake.nextId());
        patrolStandardMapper.add(dto);
        saveSubList(dto.getId(), dto.getSubList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public void update(PatrolStandardDTO dto) {
        checkParam(dto, true);
        patrolStandardMapper.update(dto);
        patrolStandardMapper.deleteSubByParentId(dto.getId());
        saveSubList(dto.getId(), dto.getSubList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public void delete(Long id) {
        if (id == null) {
            throw new BusinessRuntimeException("id不能为空");
        }
        patrolStandardMapper.deleteSubByParentId(id);
        patrolStandardMapper.delete(id);
    }

    private void checkParam(PatrolStandardDTO dto, boolean checkId) {
        if (dto == null) {
            throw new BusinessRuntimeException("参数不能为空");
        }
        if (checkId && dto.getId() == null) {
            throw new BusinessRuntimeException("id不能为空");
        }
        if (StringUtils.isBlank(dto.getStandardCode())) {
            throw new BusinessRuntimeException("标准编号不能为空");
        }
        if (StringUtils.isBlank(dto.getStandardName())) {
            throw new BusinessRuntimeException("标准名称不能为空");
        }
        if (StringUtils.isBlank(dto.getEqptId())) {
            throw new BusinessRuntimeException("设备ID不能为空");
        }
        if (dto.getSubList() == null || dto.getSubList().isEmpty()) {
            throw new BusinessRuntimeException("请填写子表数据");
        }
        for (PatrolStandardSubPO item : dto.getSubList()) {
            if (item == null) {
                throw new BusinessRuntimeException("子表数据不能为空");
            }
            if (StringUtils.isBlank(item.getCheckContent())) {
                throw new BusinessRuntimeException("检查内容不能为空");
            }
            if (StringUtils.isBlank(item.getQualifyCondition())) {
                throw new BusinessRuntimeException("合格条件不能为空");
            }
            if (StringUtils.isBlank(item.getCheckMethod())) {
                throw new BusinessRuntimeException("检查方法不能为空");
            }
        }
    }

    private void saveSubList(Long parentId, List<PatrolStandardSubPO> subList) {
        if (subList == null || subList.isEmpty()) {
            return;
        }
        patrolStandardMapper.insertSubBatch(parentId, subList);
    }
}

