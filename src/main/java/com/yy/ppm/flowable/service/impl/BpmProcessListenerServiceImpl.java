package com.yy.ppm.flowable.service.impl;

import cn.hutool.core.lang.Snowflake;
import com.yy.common.log.MicroLogger;
import com.yy.common.page.Pages;
import com.yy.common.util.PageHelperUtils;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.flowable.bean.dto.BpmBusinessConfigDTO;
import com.yy.ppm.flowable.bean.dto.BpmProcessListenerDTO;
import com.yy.ppm.flowable.bean.dto.BpmProcessListenerSearchDTO;
import com.yy.ppm.flowable.mapper.BpmProcessListenerMapper;
import com.yy.ppm.flowable.service.BpmProcessListenerService;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class BpmProcessListenerServiceImpl implements BpmProcessListenerService {

    private static final MicroLogger LOGGER = new MicroLogger(BpmProcessListenerServiceImpl.class);

    @Autowired
    private Snowflake snowflake;

    @Resource
    private BpmProcessListenerMapper bpmProcessListenerMapper;

    /**
     * 分页查询列表
     */
    @Override
    public Pages<BpmProcessListenerDTO> getList(BpmProcessListenerSearchDTO searchDTO) {
        final String methodName = "BpmProcessListenerServiceImpl:getList";
        LOGGER.enter(methodName, "分页查询流程监听器");
        Pages<BpmProcessListenerDTO> pages = PageHelperUtils.limit(searchDTO,
                () -> bpmProcessListenerMapper.getList(searchDTO));

        LOGGER.exit(methodName, StringUtils.EMPTY);
        return pages;
    }
    /**
     * 新增
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insert(BpmProcessListenerDTO dto) {
        final String methodName = "BpmProcessListenerServiceImpl:insert";
        LOGGER.enter(methodName, "新增监听器");
        dto.setId(snowflake.nextId());
        dto.setCreateTime(new Date());
        bpmProcessListenerMapper.insert(dto);
        LOGGER.exit(methodName, StringUtils.EMPTY);
    }

    /**
     * 修改
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(BpmProcessListenerDTO dto) {
        final String methodName = "BpmProcessListenerServiceImpl:update";
        LOGGER.enter(methodName, "修改监听器");
        if (dto.getId() == null) {
            throw new BusinessRuntimeException("ID不能为空");
        }
        bpmProcessListenerMapper.update(dto);
        LOGGER.exit(methodName, StringUtils.EMPTY);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteById(Long id) {
        final String methodName = "BpmProcessListenerServiceImpl:deleteById";
        LOGGER.enter(methodName, "删除监听器");
        if (id == null) {
            throw new BusinessRuntimeException("ID不能为空");
        }
        Integer count = bpmProcessListenerMapper.deleteById(id);
        if (count <= 0) {
            throw new BusinessRuntimeException("删除失败，记录不存在");
        }
        LOGGER.exit(methodName, StringUtils.EMPTY);
    }

    @Override
    public BpmProcessListenerDTO getDetail(Long id) {
        final String methodName = "BpmProcessListenerServiceImpl:getDetail";
        LOGGER.enter(methodName, "查询详情");
        BpmProcessListenerDTO dto = bpmProcessListenerMapper.getDetail(id);
        if (dto == null) {
            throw new BusinessRuntimeException("记录不存在");
        }
        LOGGER.exit(methodName, StringUtils.EMPTY);
        return dto;
    }
}
