package com.yy.ppm.equipment.service.impl;


import cn.hutool.core.lang.Snowflake;
import com.yy.common.log.MicroLogger;
import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.common.util.DateUtils;
import com.yy.common.util.PageHelperUtils;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.equipment.bean.dto.EPatrolPlanDTO;
import com.yy.ppm.equipment.bean.dto.InspectionRouteDTO;
import com.yy.ppm.equipment.bean.dto.InspectionRouteSubDTO;
import com.yy.ppm.equipment.bean.po.EPatrolPlanPO;
import com.yy.ppm.equipment.bean.po.EPatrolTaskPO;
import com.yy.ppm.equipment.bean.po.EPatrolTaskSubPO;
import com.yy.ppm.equipment.mapper.EPatrolPlanMapper;
import com.yy.ppm.equipment.mapper.HomeMapper;
import com.yy.ppm.equipment.service.EPatrolPlanService;
import com.yy.ppm.equipment.service.HomeService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

@Service
public class HomeServiceImpl implements HomeService {


    private static final MicroLogger LOGGER = new MicroLogger(InspectionPlanServiceImpl.class);

    @Autowired
    private HomeMapper homeMapper;






    @Override
    public Map<String, Object> getList() {

        Map<String, Object> homeMap = new HashMap<>();


        //设备维修派工单map
        Map<String, Object> eMaintInfo =homeMapper.getMaintIfon();

        //巡检任务
        Map<String, Object> ePatrolTask =homeMapper.getPatrolTask();


        //点检任务
        Map<String, Object> eCheckPlan =homeMapper.getCheckPlan();

        //润滑保养任务
        Map<String, Object> ePmMaintainTask =homeMapper.ePmMaintainTask();

        //派工单类型统计
        Map<String, Object> eMaintIfonType =homeMapper.eMaintIfonType();

        //派工单今日统计
        Map<String, Object> eMaintIfonToday =homeMapper.eMaintIfonToday(new Date());

        //获取设备统计
        Map<String, Object> mEqptInfo =homeMapper.getEqptInfo();

        //获取设备状态统计
        Map<String, Object> mEqptStatus =homeMapper.getEqptStatus();

        homeMap.put("eMaintInfo",eMaintInfo);
        homeMap.put("ePatrolTask",ePatrolTask);
        homeMap.put("eCheckPlan",eCheckPlan);
        homeMap.put("ePmMaintainTask",ePmMaintainTask);
        homeMap.put("eMaintIfonType",eMaintIfonType);
        homeMap.put("eMaintIfonToday",eMaintIfonToday);
        homeMap.put("mEqptInfo",mEqptInfo);
        homeMap.put("mEqptStatus",mEqptStatus);



        return homeMap;
    }

}
