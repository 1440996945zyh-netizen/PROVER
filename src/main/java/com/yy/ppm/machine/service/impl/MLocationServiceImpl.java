package com.yy.ppm.machine.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.github.pagehelper.Page;
import com.yy.common.page.Pages;
import com.yy.common.util.PageHelperUtils;
import com.yy.common.util.UserHelper;
import com.google.common.collect.*;
import com.yy.common.log.MicroLogger;
import com.yy.ppm.machine.bean.dto.MLocationHistoryDTO;
import com.yy.ppm.machine.controller.MLocationController;

import com.yy.ppm.machine.mapper.MLocationHistoryMapper;
import com.yy.ppm.machine.service.MLocationService;
import com.yy.ppm.machine.mapper.MLocationMapper;
import com.yy.ppm.machine.bean.dto.MLocationDTO;
import com.yy.ppm.machine.bean.dto.MLocationSearchDTO;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import org.apache.commons.collections.CollectionUtils;

import cn.hutool.core.lang.Snowflake;

import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;


/**
 * @author czk
 * @version 1.0.0
 * @ClassName 实时车辆表(MLocation)ServiceImpl
 * @Description
 * @createTime 2023年10月25日 10:21:00
 */
@Service
public class MLocationServiceImpl implements MLocationService {

    @Resource
    private MLocationMapper mLocationMapper;
    @Resource
    private MLocationHistoryMapper mLocationHistoryMapper;
    /**
     * 日志组件
     **/
    private static final MicroLogger LOGGER = new MicroLogger(MLocationController.class);

    /**
     * 获取列表
     *
     * @param searchDTO
     * @return 对象列表
     */
    @Override
    public List<MLocationDTO> getListByCondition(MLocationSearchDTO searchDTO) {
        final String methodName = "MLocationServiceImpl:getListByCondition";
        try {
            List<MLocationDTO> list = mLocationMapper.exportList(searchDTO);
            //按照创建时间倒叙排列
            list.sort(((o1, o2) -> o2.getCreateTime().compareTo(o1.getCreateTime())));
            return list;
        } catch (Exception e) {
            LOGGER.error(methodName, e.getMessage());
            return Lists.newArrayList();
        }
    }

    /**
     * 保存
     *
     * @param dto
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean doSave(MLocationDTO dto) {
        MLocationHistoryDTO mLocationHistoryDTO = new MLocationHistoryDTO();
        dto.setSpeed(dto.getSpeed().setScale(3, BigDecimal.ROUND_HALF_UP));
        BeanUtil.copyProperties(dto,mLocationHistoryDTO);
        mLocationHistoryMapper.insert(mLocationHistoryDTO);
        MLocationDTO locationDTO = mLocationMapper.getByMacId(dto.getMacId());
        // 新增
        if (ObjectUtils.isEmpty(locationDTO)) {
            return mLocationMapper.insert(dto) == 1;
            // 修改
        } else {
            return mLocationMapper.update(dto) == 1;
        }

    }

}

