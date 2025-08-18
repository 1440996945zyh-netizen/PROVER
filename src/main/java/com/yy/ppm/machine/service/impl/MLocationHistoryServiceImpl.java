package com.yy.ppm.machine.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.github.pagehelper.Page;
import com.yy.common.page.Pages;
import com.yy.common.util.PageHelperUtils;
import com.yy.common.util.UserHelper;
import com.google.common.collect.*;
import com.yy.common.log.MicroLogger;
import com.yy.ppm.machine.bean.dto.MLocationDTO;
import com.yy.ppm.machine.controller.MLocationHistoryController;

import com.yy.ppm.machine.service.MLocationHistoryService;
import com.yy.ppm.machine.mapper.MLocationHistoryMapper;
import com.yy.ppm.machine.bean.dto.MLocationHistoryDTO;
import com.yy.ppm.machine.bean.dto.MLocationHistorySearchDTO;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import org.apache.commons.collections.CollectionUtils;

import cn.hutool.core.lang.Snowflake;

import jakarta.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;


/**
 * @author czk
 * @version 1.0.0
 * @ClassName 车辆历史表(MLocationHistory)ServiceImpl
 * @Description
 * @createTime 2023年10月25日 10:46:00
 */

@Service
public class MLocationHistoryServiceImpl implements MLocationHistoryService {

    @Resource
    private MLocationHistoryMapper mLocationHistoryMapper;

    /**
     * 日志组件
     **/
    private static final MicroLogger LOGGER = new MicroLogger(MLocationHistoryController.class);

    /**
     * 获取列表
     *
     * @param searchDTO
     * @return 对象列表
     */
    @Override
    public List<MLocationHistoryDTO> getListByCondition(MLocationHistorySearchDTO searchDTO) {
        final String methodName = "MLocationHistoryServiceImpl:getListByCondition";
        try {
            List<MLocationHistoryDTO> list = mLocationHistoryMapper.exportList(searchDTO);
            //按照创建时间倒叙排列
            list.sort(((o1, o2) -> o2.getCreateTime().compareTo(o1.getCreateTime())));
            return list;
        } catch (Exception e) {
            LOGGER.error(methodName, e.getMessage());
            return Lists.newArrayList();
        }
    }

    /**
     * 查询单条记录
     *
     * @param macId
     * @return 实体
     */
    @Override
    public MLocationHistoryDTO getDetail(String macId) {
        return mLocationHistoryMapper.getByMacId(macId);
    }

    /**
     * 保存
     *
     * @param dto
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean doSave(MLocationHistoryDTO dto) {
        MLocationHistoryDTO locationDTO = mLocationHistoryMapper.getByMacId(dto.getMacId());
        // 新增
        if (ObjectUtils.isNotEmpty(locationDTO)) {
            return mLocationHistoryMapper.insert(dto) == 1;
            // 修改
        } else {
            return mLocationHistoryMapper.update(dto) == 1;
        }

    }


    /**
     * 批量删除
     * @param
     * @return 是否成功
     */
    @Override
    public boolean delete() {
        try{
            Date date = DateUtil.lastWeek();
            MLocationHistoryDTO mLocationHistoryDTO = new MLocationHistoryDTO();
            mLocationHistoryDTO.setGpsTime(date);
            return mLocationHistoryMapper.deleteByCondition(mLocationHistoryDTO) >= 1;
        }catch (Exception e){
            LOGGER.error(e.getMessage());
            throw new RuntimeException("车辆历史表删除错误");
        }
    }
}

