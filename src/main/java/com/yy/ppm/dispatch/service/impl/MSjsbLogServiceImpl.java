package com.yy.ppm.dispatch.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.google.common.collect.Maps;
import com.yy.common.log.MicroLogger;
import com.yy.common.page.Pages;
import com.yy.common.util.DateUtils;
import com.yy.common.util.PageHelperUtils;
import com.yy.common.util.UserHelper;

import com.yy.ppm.dispatch.mapper.TDisShipDynamicMapper;
import com.yy.ppm.dispatch.mapper.TDisShipVoyageMapper;
import com.yy.ppm.dispatch.service.MSjsbLogService;
import com.yy.ppm.dispatch.mapper.MSjsbLogMapper;
import com.yy.ppm.dispatch.bean.dto.MSjsbLogDTO;
import com.yy.ppm.dispatch.bean.dto.MSjsbLogSearchDTO;
import com.yy.ppm.dispatch.service.TDisShipDynamicService;
import com.yy.ppm.dispatch.service.TDisShipVoyageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.parameters.P;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import cn.hutool.core.lang.Snowflake;

import jakarta.annotation.Resource;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author makejava
 * @version 1.0.0
 * @ClassName 数据上报日志表(MSjsbLog)ServiceImpl
 * @Description
 * @createTime 2025年05月20日 10:40:00
 */
@Service
public class MSjsbLogServiceImpl implements MSjsbLogService {

    @Autowired
    private TDisShipVoyageService shipVoyageService;
    @Resource
    private TDisShipVoyageMapper tDisShipVoyageMapper;
    @Autowired
    private TDisShipDynamicService dynamicService;

    @Resource
    private MSjsbLogMapper mSjsbLogMapper;
    @Resource
    private Snowflake snowflake;

    private static final MicroLogger LOGGER = new MicroLogger(TDisShipDynamicServiceImpl.class);


    /**
     * 获取列表（翻页）
     *
     * @param searchDTO
     * @return 对象列表
     */
    @Override
    public Pages<MSjsbLogDTO> getList(MSjsbLogSearchDTO searchDTO) {

        Pages<MSjsbLogDTO> pages = PageHelperUtils.limit(searchDTO, () -> {
            return mSjsbLogMapper.getList(searchDTO);
        });

        return pages;
    }

    /**
     * 查询单条记录
     *
     * @param id
     * @return 实体
     */
    @Override
    public MSjsbLogDTO getDetail(Long id) {
        return mSjsbLogMapper.getById(id);
    }

    /**
     * 保存
     *
     * @param dto
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean doSave(MSjsbLogDTO dto) {

        // 新增
        if (dto.getId() == null) {
            dto.setId(snowflake.nextId());
            return mSjsbLogMapper.insert(dto) == 1;

            // 修改
        } else {
            return mSjsbLogMapper.update(dto) == 1;
        }

    }

    /**
     * 删除
     *
     * @param id
     * @return 是否成功
     */
    @Override
    public boolean deleteById(Long id) {

        return mSjsbLogMapper.deleteById(id) == 1;

    }

}

