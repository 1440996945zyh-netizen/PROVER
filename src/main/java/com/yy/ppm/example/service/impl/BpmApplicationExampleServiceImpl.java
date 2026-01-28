package com.yy.ppm.example.service.impl;

import cn.hutool.core.lang.Snowflake;
import com.yy.common.log.MicroLogger;
import com.yy.common.page.Pages;
import com.yy.common.util.PageHelperUtils;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.example.bean.dto.BpmApplicationExampleDTO;
import com.yy.ppm.example.bean.dto.BpmApplicationExampleSearchDTO;
import com.yy.ppm.example.mapper.BpmApplicationExampleMapper;
import com.yy.ppm.example.service.BpmApplicationExampleService;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description BPM应用示例Service实现类
 */
@Service
public class BpmApplicationExampleServiceImpl implements BpmApplicationExampleService {

    /**
     * 日志组件
     */
    private static final MicroLogger LOGGER = new MicroLogger(BpmApplicationExampleServiceImpl.class);

    /**
     * 雪花算法
     */
    @Autowired
    private Snowflake snowflake;

    @Resource
    private BpmApplicationExampleMapper bpmApplicationExampleMapper;

    @Override
    public void insert(BpmApplicationExampleDTO dto) {
        final String methodName = "BpmApplicationExampleServiceImpl:insert";
        LOGGER.enter(methodName, "业务执行");

        // 生成ID
        dto.setId(snowflake.nextId());

        // 设置默认审批状态
        if (StringUtils.isBlank(dto.getApprovalStatus())) {
            dto.setApprovalStatus("pending");
        }

        bpmApplicationExampleMapper.insert(dto);

        LOGGER.exit(methodName, StringUtils.EMPTY);
    }

    @Override
    public void update(BpmApplicationExampleDTO dto) {
        final String methodName = "BpmApplicationExampleServiceImpl:update";
        LOGGER.enter(methodName, "业务执行");

        if (dto.getId() == null) {
            throw new BusinessRuntimeException("ID不能为空");
        }

        bpmApplicationExampleMapper.update(dto);

        LOGGER.exit(methodName, StringUtils.EMPTY);
    }

    @Override
    public void deleteById(Long id) {
        final String methodName = "BpmApplicationExampleServiceImpl:deleteById";
        LOGGER.enter(methodName, "业务执行");

        if (id == null) {
            throw new BusinessRuntimeException("ID不能为空");
        }

        Integer count = bpmApplicationExampleMapper.deleteById(id);
        if (count <= 0) {
            throw new BusinessRuntimeException("删除失败！");
        }

        LOGGER.exit(methodName, StringUtils.EMPTY);
    }

    @Override
    public BpmApplicationExampleDTO getDetail(Long id) {
        final String methodName = "BpmApplicationExampleServiceImpl:getDetail";
        LOGGER.enter(methodName, "业务执行");

        if (id == null) {
            throw new BusinessRuntimeException("ID不能为空");
        }

        BpmApplicationExampleDTO dto = bpmApplicationExampleMapper.getDetail(id);
        if (dto == null) {
            throw new BusinessRuntimeException("数据不存在");
        }

        LOGGER.exit(methodName, StringUtils.EMPTY);
        return dto;
    }

    @Override
    public Pages<BpmApplicationExampleDTO> getList(BpmApplicationExampleSearchDTO searchDTO) {
        final String methodName = "BpmApplicationExampleServiceImpl:getList";
        LOGGER.enter(methodName, "业务执行");

        Pages<BpmApplicationExampleDTO> pages = PageHelperUtils.limit(searchDTO,
                () -> bpmApplicationExampleMapper.getList(searchDTO));

        LOGGER.exit(methodName, StringUtils.EMPTY);
        return pages;
    }

    @Override
    public List<BpmApplicationExampleDTO> getAllList() {
        final String methodName = "BpmApplicationExampleServiceImpl:getAllList";
        LOGGER.enter(methodName, "业务执行");

        List<BpmApplicationExampleDTO> list = bpmApplicationExampleMapper.getAllList();

        LOGGER.exit(methodName, StringUtils.EMPTY);
        return list;
    }
}