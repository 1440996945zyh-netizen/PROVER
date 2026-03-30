package com.yy.ppm.equipment.service.impl;


import cn.hutool.core.lang.Snowflake;
import com.yy.common.log.MicroLogger;
import com.yy.common.magic.FileUploadBusinessTypeEnum;
import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.common.util.DateUtils;
import com.yy.common.util.PageHelperUtils;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.common.mapper.SysFileMapper;
import com.yy.ppm.common.service.SysFileService;
import com.yy.ppm.equipment.bean.dto.*;
import com.yy.ppm.equipment.bean.po.*;
import com.yy.ppm.equipment.mapper.EMEquipRepairUserMapper;
import com.yy.ppm.equipment.mapper.EPatrolPlanMapper;
import com.yy.ppm.equipment.service.EMEquipRepairUserService;
import com.yy.ppm.equipment.service.EPatrolPlanService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

@Service
public class EPatrolPlanServiceImpl implements EPatrolPlanService {


    private static final MicroLogger LOGGER = new MicroLogger(InspectionPlanServiceImpl.class);

    @Autowired
    private EPatrolPlanMapper ePatrolPlanMapper;
    @Resource
    private Snowflake snowflake;


    @Override
    public Pages<EPatrolPlanDTO> getList(EPatrolPlanDTO searchDTO, PageParameter parameter) {
        Pages<EPatrolPlanDTO> pages = PageHelperUtils.limit(parameter, () -> {
            return ePatrolPlanMapper.getList(searchDTO);
        });
        return pages;
    }

    @Override
    public EPatrolPlanDTO getById(EPatrolPlanDTO searchDTO) {
        EPatrolPlanDTO po = ePatrolPlanMapper.getById(searchDTO);
        return po;
    }

    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)

    public void save(EPatrolPlanDTO po) {
        // 新增
        if (po.getId() == null) {
            po.setId(snowflake.nextId());
            ePatrolPlanMapper.insert(po);

        } else {
            ePatrolPlanMapper.update(po);
        }

    }

    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public void delete(Long id) {
        if (id == null) {
            throw new BusinessRuntimeException("请选择一条数据删除");
        }
        ePatrolPlanMapper.deleteById(id);
    }

    @Override
    public List<InspectionRouteDTO> getRouteList(InspectionRouteDTO searchDTO) {
        return ePatrolPlanMapper.getRouteList(searchDTO);
    }


    /**
     * 定时新增点检任务
     */
//    @Scheduled(cron="0 0 0 * * ?")
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public void patrolTimeTask () {
        LOGGER.enter("定时新增巡检任务开始");

        //查询需要轮询的巡检计划
        List<EPatrolPlanDTO> patrolPlanList = ePatrolPlanMapper.getpatrolPlanList();

        Date today = new Date();
        LocalDate today1 = LocalDate.now();
        //巡检计划
        List<EPatrolPlanPO> planList = new ArrayList<>();
        //巡检任务 - 主
        List<EPatrolTaskPO> taskList = new ArrayList<>();
        //巡检任务 - 子
        List<EPatrolTaskSubPO> taskItemList = new ArrayList<>();


        for (EPatrolPlanDTO po : patrolPlanList) {

            // 生成巡检任务 -- 主表
            EPatrolTaskPO patrolTaskPO = new EPatrolTaskPO();
            //巡检计划表
            EPatrolPlanPO patrolPlanPO = new EPatrolPlanPO();

            // 日 当前时间小于开始时间
            if ("1".equals(po.getPatrolType()) && toDate(today1).compareTo(po.getInitialDate()) <= 0) {
                continue;
            }
            // 周 -- 判断今天是否大于开始日期并且今天与所选择的日期（周一到周天）相同

            if ("2".equals(po.getPatrolType()) && toDate(today1).compareTo(po.getInitialDate()) >= 0) {
                // 判断今天是星期几
                DayOfWeek dayOfWeek = getDayOfWeek(today);
                int day = dayOfWeek.getValue(); // 1=周一, 7=周日
                if (day != Integer.parseInt(po.getSetDate())) {
                    continue;
                }
            }else {
                continue;
            }

            // 月 -- 判断今天是否大于截止日期并且今天与所选择的日期（1号到30号）相同
            if ("3".equals(po.getPatrolType()) && toDate(today1).compareTo(po.getInitialDate()) >= 0) {
                // 获取今天是几号（1到31之间的数字）
                int dayOfMonth = today1.getDayOfMonth();
                if (dayOfMonth != Integer.parseInt(po.getSetDate())) {
                    continue;
                }
            }else {
                continue;
            }

            // 年 判断是不是一月一号 并且当前时间大于结束时间
            if ("4".equals(po.getPatrolType())  && toDate(today1).compareTo(po.getInitialDate()) >= 0) {
                // 获取月份（1-12）
                int month = today1.getMonthValue();
                // 获取日（1-31）
                int day = today1.getDayOfMonth();
                if ((month != 1 && day != 1)) {
                    continue;
                }
            }else {
                continue;
            }


            //巡检计划
            patrolPlanPO.setId(po.getId());
            po.setRecentlyTaskDate(new Date());

            planList.add(patrolPlanPO);

            //巡检任务
            patrolTaskPO.setId(snowflake.nextId());
            patrolTaskPO.setPlanId(po.getId().toString());
            patrolTaskPO.setRouteId(po.getRouteId().toString());
            patrolTaskPO.setPatrolId(po.getPatrolId());
            patrolTaskPO.setPatrolName(po.getPatrolName());
            patrolTaskPO.setStartDate(new Date());
            patrolTaskPO.setEndDate(DateUtils.addDays(new Date(), Integer.parseInt(po.getTimeLimit())-1));
            patrolTaskPO.setStatus(0);

            taskList.add(patrolTaskPO);

            //巡检任务 - 子
            List<InspectionRouteSubDTO> routeList = ePatrolPlanMapper.getrouteSubList(po.getRouteId());

            for (InspectionRouteSubDTO item : routeList) {
                 EPatrolTaskSubPO taskItemPO = new EPatrolTaskSubPO();
                 taskItemPO.setParentId(patrolTaskPO.getId());
                 taskItemPO.setEquipId(item.getEquipId());
                 taskItemPO.setStatus(0);
                 taskItemPO.setIsAbnormal(0);
                 taskItemPO.setIsRepair(0);

                 taskItemList.add(taskItemPO);
            }

        }

        //回写巡检计划
        if (!CollectionUtils.isEmpty(planList)){
            ePatrolPlanMapper.updatePlanList(planList);
        }
        //新增巡检任务
        if (!CollectionUtils.isEmpty(taskList)){
            ePatrolPlanMapper.insertTaskList(taskList);
        }
        //新增巡检任务 - 子
        if (!CollectionUtils.isEmpty(taskItemList)){
            ePatrolPlanMapper.insertTaskItemList(taskItemList);
        }


        LOGGER.exit("定时新增巡检任务结束");
    }


    /**
     * LocalDate 转 Date（时间部分为 00:00:00）
     */
    public static Date toDate(LocalDate localDate) {
        if (localDate == null) {
            return null;
        }

        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    /**
     * 判断Date类型是星期几
     */
    public static DayOfWeek getDayOfWeek(Date date) {
        // 将Date转换为LocalDate
        LocalDate localDate = date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        // 获取星期几
        return localDate.getDayOfWeek();
    }
}
