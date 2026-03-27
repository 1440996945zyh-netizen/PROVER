package com.yy.ppm.equipment.service.impl;

import cn.hutool.core.lang.Snowflake;
import com.yy.common.log.MicroLogger;
import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.common.util.PageHelperUtils;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.equipment.bean.dto.InspectionRouteDTO;
import com.yy.ppm.equipment.bean.dto.InspectionRouteSubDTO;
import com.yy.ppm.equipment.mapper.InspectionRouteMapper;
import com.yy.ppm.equipment.mapper.InspectionRouteSubMapper;
import com.yy.ppm.equipment.service.InspectionRouteService;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class InspectionRouteServiceImpl implements InspectionRouteService {

    private static final MicroLogger LOGGER = new MicroLogger(InspectionRouteServiceImpl.class);

    @Resource
    private InspectionRouteMapper inspectionRouteMapper;

    @Resource
    private InspectionRouteSubMapper inspectionRouteSubMapper;

    @Autowired
    private Snowflake snowflake;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insert(InspectionRouteDTO dto) {
        final String methodName = "InspectionRouteServiceImpl:insert";
        LOGGER.enter(methodName, "业务执行");

        // 生成主表ID
        dto.setId(snowflake.nextId());

        // 插入主表
        inspectionRouteMapper.insert(dto);

        // 插入子表
        if (dto.getSubList() != null && !dto.getSubList().isEmpty()) {
            for (InspectionRouteSubDTO sub : dto.getSubList()) {
                sub.setParentId(dto.getId());
                sub.setId(snowflake.nextId());
                inspectionRouteSubMapper.insert(sub);
            }
        }

        LOGGER.exit(methodName, StringUtils.EMPTY);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(InspectionRouteDTO dto) {
        final String methodName = "InspectionRouteServiceImpl:update";
        LOGGER.enter(methodName, "业务执行");

        if (dto.getId() == null) {
            throw new BusinessRuntimeException("ID不能为空");
        }

        // 更新主表
        inspectionRouteMapper.update(dto);

        // 先删除旧子表，再插入新子表
        inspectionRouteSubMapper.deleteByParentId(dto.getId());
        if (dto.getSubList() != null && !dto.getSubList().isEmpty()) {
            for (InspectionRouteSubDTO sub : dto.getSubList()) {
                sub.setParentId(dto.getId());
                sub.setId(snowflake.nextId());
                inspectionRouteSubMapper.insert(sub);
            }
        }

        LOGGER.exit(methodName, StringUtils.EMPTY);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteById(Long id) {
        final String methodName = "InspectionRouteServiceImpl:deleteById";
        LOGGER.enter(methodName, "业务执行");

        if (id == null) {
            throw new BusinessRuntimeException("ID不能为空");
        }

        // 删除子表
        inspectionRouteSubMapper.deleteByParentId(id);

        // 删除主表
        Integer count = inspectionRouteMapper.deleteById(id);
        if (count <= 0) {
            throw new BusinessRuntimeException("删除失败！");
        }

        LOGGER.exit(methodName, StringUtils.EMPTY);
    }

    @Override
    public InspectionRouteDTO getDetail(Long id) {
        final String methodName = "InspectionRouteServiceImpl:getDetail";
        LOGGER.enter(methodName, "业务执行");

        if (id == null) {
            throw new BusinessRuntimeException("ID不能为空");
        }

        InspectionRouteDTO dto = inspectionRouteMapper.getDetail(id);
        if (dto == null) {
            throw new BusinessRuntimeException("数据不存在");
        }

        // 查询子表
        List<InspectionRouteSubDTO> subList = inspectionRouteSubMapper.listByParentId(id);
        dto.setSubList(subList);

        LOGGER.exit(methodName, StringUtils.EMPTY);
        return dto;
    }

    @Override
    public Pages<InspectionRouteDTO> getList(InspectionRouteDTO dto, PageParameter parameter) {
        final String methodName = "InspectionRouteServiceImpl:getList";
        LOGGER.enter(methodName, "业务执行");

        Pages<InspectionRouteDTO> pages = PageHelperUtils.limit(parameter,
                () -> inspectionRouteMapper.getList(dto));

        LOGGER.exit(methodName, StringUtils.EMPTY);
        return pages;
    }

    @Override
    public List<InspectionRouteDTO> getAllList() {
        final String methodName = "InspectionRouteServiceImpl:getAllList";
        LOGGER.enter(methodName, "业务执行");

        List<InspectionRouteDTO> list = inspectionRouteMapper.getAllList();

        LOGGER.exit(methodName, StringUtils.EMPTY);
        return list;
    }
}