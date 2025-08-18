package com.yy.ppm.system.service.impl;

import com.yy.common.log.MicroLogger;
import com.yy.common.page.Pages;
import com.yy.common.util.PageHelperUtils;
import com.yy.ppm.system.bean.dto.SysOperLogDTO;
import com.yy.ppm.system.bean.dto.SysOperLogSearchDTO;
import com.yy.ppm.system.mapper.SysOperLogMapper;
import com.yy.ppm.system.service.SysOperLogService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;

/**
 * @author FanQi
 * @version 1.0
 * @date 2023/5/6 11:26
 */
@Service
public class SysOperLogServiceImpl implements SysOperLogService {

    /**
     * 日志组件
     */
    private static final MicroLogger LOGGER = new MicroLogger(SysOperLogServiceImpl.class);

    @Resource
    SysOperLogMapper sysOperLogMapper;

    /**
     * 查询
     * @param sysOperLogSearchDTO
     * @return
     */
    @Override
    public Pages<SysOperLogDTO> getList(SysOperLogSearchDTO sysOperLogSearchDTO) {
        final String methodName = "SysOperLogServiceImpl:getList";
        LOGGER.enter(methodName, "业务执行");
        Pages<SysOperLogDTO> pages = PageHelperUtils.limit(sysOperLogSearchDTO, () -> {
            return sysOperLogMapper.getList(sysOperLogSearchDTO);
        });
        LOGGER.exit(methodName, StringUtils.EMPTY);
        return pages;
    }

    /**
     * 新建
     * @param sysOperLogDTO
     * @return
     */
    @Override
    @Transactional
    public int insert(SysOperLogDTO sysOperLogDTO) {
        final String methodName = "SysOperLogServiceImpl:insert";
        LOGGER.enter(methodName, "业务执行");
        int count = sysOperLogMapper.insert(sysOperLogDTO);
        LOGGER.exit(methodName, StringUtils.EMPTY);
        return count;
    }

    /**
     * 根据Id查询
     * @param operId
     * @return
     */
    @Override
    public SysOperLogDTO getById(Long operId) {
        return sysOperLogMapper.getById(operId);
    }

}
