package com.yy.ppm.equipment.service.impl;


import cn.hutool.core.lang.Snowflake;
import com.yy.common.log.MicroLogger;
import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.common.util.DateUtils;
import com.yy.common.util.PageHelperUtils;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.equipment.bean.dto.*;
import com.yy.ppm.equipment.bean.po.*;
import com.yy.ppm.equipment.mapper.EPatrolPlanMapper;
import com.yy.ppm.equipment.service.EPatrolPlanService;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
            //当前日期
            LocalDate today1 = LocalDate.now();


            if (toDate(today1).compareTo(po.getInitialDate()) == 0) {
                if (isCreateTask(po.getPatrolType(), po.getInitialDate(),po.getSetDate())){


                    //巡检任务 - 主
                    List<EPatrolTaskPO> taskList = new ArrayList<>();
                    //巡检任务 - 子
                    List<EPatrolTaskSubPO> taskItemList = new ArrayList<>();

                    // 生成巡检任务 -- 主表
                    EPatrolTaskPO patrolTaskPO = new EPatrolTaskPO();

                    //巡检任务
                    patrolTaskPO.setId(snowflake.nextId());
                    patrolTaskPO.setPlanId(po.getId().toString());
                    patrolTaskPO.setRouteId(po.getRouteId().toString());
                    patrolTaskPO.setPatrolId(po.getPatrolId());
                    patrolTaskPO.setPatrolName(po.getPatrolName());
                    patrolTaskPO.setStartDate(new Date());
                    patrolTaskPO.setEndDate(DateUtils.addDays(new Date(), Integer.parseInt(po.getTimeLimit())-1));
                    patrolTaskPO.setStatus(0);
                    patrolTaskPO.setCreateBy(1L);
                    patrolTaskPO.setCreateTime(new Date());
                    patrolTaskPO.setCreateByName("定时任务");

                    taskList.add(patrolTaskPO);

                    //巡检任务 - 子
                    List<InspectionRouteSubDTO> routeList = ePatrolPlanMapper.getrouteSubList(po.getRouteId());

                    for (InspectionRouteSubDTO item : routeList) {

                        EPatrolTaskSubPO taskItemPO = new EPatrolTaskSubPO();
                        taskItemPO.setParentId(patrolTaskPO.getId());
                        taskItemPO.setEquipId(item.getEquipId());
                        taskItemPO.setEquipName(item.getEquipName());
                        taskItemPO.setCheckContent(item.getCheckContent());
                        taskItemPO.setQualifyCondition(item.getQualifyCondition());
                        taskItemPO.setCheckMethod(item.getCheckMethod());
                        taskItemPO.setStatus(0);
                        taskItemPO.setIsAbnormal(0);
                        taskItemPO.setIsRepair(0);
                        taskItemPO.setId(snowflake.nextId());

                        taskItemList.add(taskItemPO);
                    }

                    //新增巡检任务
                    if (!CollectionUtils.isEmpty(taskList)){
                        ePatrolPlanMapper.insertTaskList(taskList);
                    }
                    //新增巡检任务 - 子
                    if (!CollectionUtils.isEmpty(taskItemList)){
                        ePatrolPlanMapper.insertTaskItemList(taskItemList);
                    }

                    //任务创建时间

                    po.setRecentlyTaskDate(new Date());

                }

            }



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
        EPatrolPlanDTO searchDTO = new EPatrolPlanDTO();
        searchDTO.setId(id);
        EPatrolPlanDTO po = ePatrolPlanMapper.getById(searchDTO);
        if (null!=po.getRecentlyTaskDate()){
            throw new BusinessRuntimeException("请先删除该计划下的任务");
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
    @Scheduled(cron="0 0 0 * * ?")
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public void patrolTimeTask () {
        LOGGER.enter("定时新增巡检任务开始");

        //查询需要轮询的巡检计划
        List<EPatrolPlanDTO> patrolPlanList = ePatrolPlanMapper.getpatrolPlanList();


        //巡检计划
        List<EPatrolPlanPO> planList = new ArrayList<>();
        //巡检任务 - 主
        List<EPatrolTaskPO> taskList = new ArrayList<>();
        //巡检任务 - 子
        List<EPatrolTaskSubPO> taskItemList = new ArrayList<>();


        for (EPatrolPlanDTO po : patrolPlanList) {

        // 判断是否生成任务
            if (isCreateTask(po.getPatrolType(), po.getInitialDate(),po.getSetDate())){

                // 生成巡检任务 -- 主表
                EPatrolTaskPO patrolTaskPO = new EPatrolTaskPO();
                //巡检计划表
                EPatrolPlanPO patrolPlanPO = new EPatrolPlanPO();


                //巡检计划
                patrolPlanPO.setId(po.getId());
                patrolPlanPO.setRecentlyTaskDate(new Date());

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
                patrolTaskPO.setCreateBy(1L);
                patrolTaskPO.setCreateTime(new Date());
                patrolTaskPO.setCreateByName("定时任务");

                taskList.add(patrolTaskPO);

                //巡检任务 - 子
                List<InspectionRouteSubDTO> routeList = ePatrolPlanMapper.getrouteSubList(po.getRouteId());

                for (InspectionRouteSubDTO item : routeList) {

                    EPatrolTaskSubPO taskItemPO = new EPatrolTaskSubPO();
                    taskItemPO.setParentId(patrolTaskPO.getId());
                    taskItemPO.setEquipId(item.getEquipId());
                    taskItemPO.setEquipName(item.getEquipName());
                    taskItemPO.setCheckContent(item.getCheckContent());
                    taskItemPO.setQualifyCondition(item.getQualifyCondition());
                    taskItemPO.setCheckMethod(item.getCheckMethod());
                    taskItemPO.setStatus(0);
                    taskItemPO.setIsAbnormal(0);
                    taskItemPO.setIsRepair(0);
                    taskItemPO.setId(snowflake.nextId());

                    taskItemList.add(taskItemPO);
                }

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


      public Boolean isCreateTask(String patrolType, Date initialDate, String setDate) {


        Date today = new Date();
        LocalDate today1 = LocalDate.now();

        Boolean flag = true;

        //判断当前时间是否小于计划开始日期 小于跳过不新增
        if (toDate(today1).compareTo(initialDate) < 0) {
            flag = false;
        }else if ("2".equals(patrolType)) {  // 周 -- 判断今天是否大于开始日期并且今天与所选择的日期（周一到周天）相同
            // 判断今天是星期几
            DayOfWeek dayOfWeek = getDayOfWeek(today);
            int day = dayOfWeek.getValue(); // 1=周一, 7=周日
            if (day != Integer.parseInt(setDate)) {
                flag = false;
            }
        }else if ("3".equals(patrolType) ) {      // 月 -- 判断今天是否大于截止日期并且今天与所选择的日期（1号到30号）相同
            // 获取今天是几号（1到31之间的数字）
            int dayOfMonth = today1.getDayOfMonth();
            if (dayOfMonth != Integer.parseInt(setDate)) {
                flag = false;
            }
        }else if ("4".equals(patrolType) ) {     // 年 判断是不是一月一号 并且当前时间大于结束时间
            // 获取月份（1-12）
            int month = today1.getMonthValue();
            // 获取日（1-31）
            int day = today1.getDayOfMonth();
            if ((month != Integer.parseInt(setDate) && day != 1)) {
                flag = false;
            }
        }

        return flag;
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
