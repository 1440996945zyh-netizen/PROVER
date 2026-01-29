package com.yy.ppm.flowable.service.impl;


import cn.hutool.core.lang.Snowflake;
import com.yy.common.log.MicroLogger;
import com.yy.common.page.Pages;
import com.yy.common.util.PageHelperUtils;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.flowable.bean.dto.BpmBusinessConfigDTO;
import com.yy.ppm.flowable.bean.dto.BpmBusinessConfigSearchDTO;
import com.yy.ppm.flowable.mapper.BpmBusinessConfigMapper;
import com.yy.ppm.flowable.service.BpmBusinessConfigService;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @Description BPM业务配置Service实现类
 */
@Service
public class BpmBusinessConfigServiceImpl implements BpmBusinessConfigService {

    private static final MicroLogger LOGGER = new MicroLogger(BpmBusinessConfigServiceImpl.class);

    @Autowired
    private Snowflake snowflake;

    @Resource
    private BpmBusinessConfigMapper bpmBusinessConfigMapper;

    /**
     * 分页查询列表
     */
    @Override
    public Pages<BpmBusinessConfigDTO> getList(BpmBusinessConfigSearchDTO searchDTO) {
        final String methodName = "BpmBusinessConfigServiceImpl:getList";
        LOGGER.enter(methodName, "分页查询业务配置列表");

        Pages<BpmBusinessConfigDTO> pages = PageHelperUtils.limit(searchDTO,
                () -> bpmBusinessConfigMapper.getList(searchDTO));

        LOGGER.exit(methodName, StringUtils.EMPTY);
        return pages;
    }

    /**
     * 新增
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insert(BpmBusinessConfigDTO dto) {
        final String methodName = "BpmBusinessConfigServiceImpl:insert";
        LOGGER.enter(methodName, "新增业务配置");

        // 设置ID
        dto.setId(snowflake.nextId());
        dto.setProcDefId(bpmBusinessConfigMapper.getprocDefId(dto.getProcModelId()));

        // 设置默认状态
        if (StringUtils.isBlank(dto.getStatus())) {
            dto.setStatus("1");
        }

        bpmBusinessConfigMapper.insert(dto);

        LOGGER.exit(methodName, StringUtils.EMPTY);
    }

    /**
     * 修改
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(BpmBusinessConfigDTO dto) {
        final String methodName = "BpmBusinessConfigServiceImpl:update";
        LOGGER.enter(methodName, "修改业务配置");

        if (dto.getId() == null) {
            throw new BusinessRuntimeException("ID不能为空");
        }
        dto.setProcDefId(bpmBusinessConfigMapper.getprocDefId(dto.getProcModelId()));

        bpmBusinessConfigMapper.update(dto);

        LOGGER.exit(methodName, StringUtils.EMPTY);
    }

    /**
     * 根据ID删除
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteById(Long id) {
        final String methodName = "BpmBusinessConfigServiceImpl:deleteById";
        LOGGER.enter(methodName, "删除业务配置");

        if (id == null) {
            throw new BusinessRuntimeException("ID不能为空");
        }

        Integer count = bpmBusinessConfigMapper.deleteById(id);
        if (count <= 0) {
            throw new BusinessRuntimeException("删除失败，记录不存在");
        }

        LOGGER.exit(methodName, StringUtils.EMPTY);
    }

    /**
     * 根据ID查询详情
     */
    @Override
    public BpmBusinessConfigDTO getDetail(Long id) {
        final String methodName = "BpmBusinessConfigServiceImpl:getDetail";
        LOGGER.enter(methodName, "查询业务配置详情");

        if (id == null) {
            throw new BusinessRuntimeException("ID不能为空");
        }

        BpmBusinessConfigDTO dto = bpmBusinessConfigMapper.getDetail(id);
        if (dto == null) {
            throw new BusinessRuntimeException("业务配置不存在");
        }

        LOGGER.exit(methodName, StringUtils.EMPTY);
        return dto;
    }
}
