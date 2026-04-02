package com.yy.ppm.equipment.service.impl;

import cn.hutool.core.lang.Snowflake;
import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.common.util.PageHelperUtils;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.equipment.bean.dto.MEpatrolStandardDTO;
import com.yy.ppm.equipment.bean.po.MEpatrolStandardSubPO;
import com.yy.ppm.equipment.mapper.MEpatrolStandardMapper;
import com.yy.ppm.equipment.service.MEpatrolStandardService;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 巡检标准
 */
@Service
public class MEpatrolStandardServiceImpl implements MEpatrolStandardService {

    @Resource
    private MEpatrolStandardMapper patrolStandardMapper;

    @Resource
    private Snowflake snowflake;

    @Override
    public Pages<MEpatrolStandardDTO> getList(MEpatrolStandardDTO searchDTO, PageParameter parameter) {
        MEpatrolStandardDTO dto = searchDTO == null ? new MEpatrolStandardDTO() : searchDTO;
        return PageHelperUtils.limit(parameter, () -> patrolStandardMapper.selectList(dto));
    }

    @Override
    public MEpatrolStandardDTO getById(Long id) {
        if (id == null) {
            throw new BusinessRuntimeException("id不能为空");
        }
        MEpatrolStandardDTO dto = patrolStandardMapper.selectById(id);
        if (dto == null) {
            return null;
        }
        dto.setSubList(patrolStandardMapper.selectSubListByParentId(id));
        return dto;
    }

    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public void add(MEpatrolStandardDTO dto) {
        checkParam(dto, false);
        dto.setId(snowflake.nextId());
        patrolStandardMapper.add(dto);
        saveSubList(dto.getId(), dto.getSubList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public void update(MEpatrolStandardDTO dto) {
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

    private void checkParam(MEpatrolStandardDTO dto, boolean checkId) {
        if (dto == null) {
            throw new BusinessRuntimeException("参数不能为空");
        }
        if (checkId && dto.getId() == null) {
            throw new BusinessRuntimeException("id不能为空");
        }
        if (StringUtils.isBlank(dto.getStandardCode())) {
            throw new BusinessRuntimeException("标准编号不能为空");
        }
        if (!dto.getStandardCode().matches("\\d+")) {
            throw new BusinessRuntimeException("标准编号只能输入数字");
        }
        if (StringUtils.isBlank(dto.getStandardName())) {
            throw new BusinessRuntimeException("标准名称不能为空");
        }
        if (StringUtils.isBlank(dto.getEqptId())) {
            throw new BusinessRuntimeException("设备名称不能为空");
        }
        if (dto.getSubList() == null || dto.getSubList().isEmpty()) {
            throw new BusinessRuntimeException("请填写子表数据");
        }
        for (MEpatrolStandardSubPO item : dto.getSubList()) {
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

    private void saveSubList(Long parentId, List<MEpatrolStandardSubPO> subList) {
        if (subList == null || subList.isEmpty()) {
            return;
        }
        patrolStandardMapper.insertSubBatch(parentId, subList);
    }
    @Override
    public List<Map<String, Object>> getEqptOptions() {
        return patrolStandardMapper.selectEqptOptions();
    }
}

