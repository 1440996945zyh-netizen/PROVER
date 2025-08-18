package com.yy.ppm.dispatch.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.yy.common.enums.CommonEnum;
import com.yy.common.util.DateUtils;
import com.yy.common.util.str.StringUtil;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.dispatch.bean.dto.disShipvoyage.TDisShipvoyageDTO2;
import com.yy.ppm.dispatch.bean.po.TDisShipvoyageItemPO;
import com.yy.ppm.dispatch.service.TDisShipDaynigttplanService;
import com.yy.ppm.dispatch.mapper.TDisShipDaynigttplanMapper;
import com.yy.ppm.dispatch.bean.dto.TDisShipDaynigttplanDTO;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import cn.hutool.core.lang.Snowflake;

import jakarta.annotation.Resource;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName 船舶昼夜计划(TDisShipDaynigttplan)ServiceImpl
 * @author yy
 * @version 1.0.0
 * @Description
 * @createTime 2023年07月17日 10:31:00
 */
@Service
public class TDisShipDaynigttplanServiceImpl implements TDisShipDaynigttplanService {

    @Resource
    private TDisShipDaynigttplanMapper tDisShipDaynigttplanMapper;

    @Resource
	private Snowflake snowflake;

    @Override
    public Long getWorkNum(Long shipvoyageItemId,String planDate) {
        return tDisShipDaynigttplanMapper.getWorkNum(shipvoyageItemId,planDate);
    }

    /**
     * 按日期查询昼夜计划详情
     *
     * @param planDate
     * @return 实体
     */
    @Override
    public List<TDisShipDaynigttplanDTO> getList(String planDate) {
        return tDisShipDaynigttplanMapper.getList(planDate);
    }
    /**
     * 按日期上一天查询昼夜计划详情
     *
     * @param planDate
     * @return 实体
     */
    @Override
    public List<TDisShipDaynigttplanDTO> getList2(String planDate) {
        return tDisShipDaynigttplanMapper.getList2(planDate);
    }

    /**
      * 按日期查询昼夜计划详情
      *
      * @param list
      * @param strPlanDate
      * @return 实体
      */
     @Override
     @Transactional(rollbackFor = Exception.class)
     public boolean doSave(List<TDisShipDaynigttplanDTO> list, String strPlanDate) {

         int count = 0;
        if (StringUtil.isEmpty(strPlanDate)) {
            throw new BusinessRuntimeException("请输入昼夜计划日期~");
        }

        if (list == null || list.size() == 0) {
            count = 1;
        }

        // 字符串转日期
        Date planDate = DateUtils.parseDate(strPlanDate, CommonEnum.DateFormatType.E_1.getCode());

        // 先删除
        tDisShipDaynigttplanMapper.deleteByPlanDate(strPlanDate);



        for (TDisShipDaynigttplanDTO dto : list) {
            dto.setId(snowflake.nextId());
            dto.setPlanDate(planDate);
            count += tDisShipDaynigttplanMapper.insert(dto);
        }

        return count > 0;

    }

    /**
     * 删除
     *
     * @param  planDate
     * @return 是否成功
     */
    @Override
    public boolean deleteByPlanDate(String planDate) {

        return tDisShipDaynigttplanMapper.deleteByPlanDate(planDate) == 1;

    }

    @Override
    public List<Map<String, String>> getShipVoyage() {
        return tDisShipDaynigttplanMapper.getShipVoyage();
    }

}

