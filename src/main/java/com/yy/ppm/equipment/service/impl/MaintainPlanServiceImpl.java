package com.yy.ppm.equipment.service.impl;


import cn.hutool.core.lang.Snowflake;
import com.yy.common.log.MicroLogger;
import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.common.util.DateUtils;
import com.yy.common.util.PageHelperUtils;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.equipment.bean.dto.EquipSmallCategorySelectDTO;
import com.yy.ppm.equipment.bean.dto.MaintainPlanDTO;
import com.yy.ppm.equipment.bean.po.*;
import com.yy.ppm.equipment.mapper.MaintainPlanMapper;
import com.yy.ppm.equipment.service.MaintainPlanService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

@Component("MaintainPlanServiceImpl")
@Service
public class MaintainPlanServiceImpl implements MaintainPlanService {
    /**
     * 日志组件
     **/
    private static final MicroLogger LOGGER = new MicroLogger(MaintainPlanServiceImpl.class);
    @Autowired
    private MaintainPlanMapper mapper;
    @Resource
    private Snowflake snowflake;

    @Override
    public Pages<MaintainPlanPO> queryAll(MaintainPlanDTO maintainPlanDTO, PageParameter parameter) {
        Pages<MaintainPlanPO> pages = PageHelperUtils.limit(parameter, () -> {
            return mapper.queryAll(maintainPlanDTO);
        });
        return pages;
    }

    @Override
    public MaintainPlanPO getById(Long id) {
        MaintainPlanPO po = mapper.queryById(id);
        po.setItemList(mapper.getPlanItem(id));
        return po;
    }

    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public void save(MaintainPlanPO dto) {
        if (dto.getItemList().size() == 0) {
            throw new BusinessRuntimeException("点检标准不能为空");
        }
        if (dto.getId() == null) {
            dto.setId(snowflake.nextId());
            dto.getItemList().stream().forEach(v -> {
                v.setId(snowflake.nextId());
                v.setEquipPlanId(dto.getId());
                v.setEquipSmallCategoryId(dto.getEquipSmallCategoryId());
                v.setEquipSmallCategoryName(dto.getEquipSmallCategoryName());
            });
            dto.setDeadlineDate(DateUtils.addDays(dto.getInitialDate(),Integer.parseInt(dto.getTimeLimit())-1));

            // 新增子表
            mapper.insertPlanItem(dto.getItemList());
            // 生成保养任务 -- 主表
            MaintainTaskPO maintainTaskPO = new MaintainTaskPO();
            maintainTaskPO.setId(snowflake.nextId());
            maintainTaskPO.setEquipPlanId(dto.getId());
            maintainTaskPO.setEquipId(dto.getEquipId());
            maintainTaskPO.setEquipName(dto.getEquipName());
            maintainTaskPO.setInspectorId(dto.getInspectorId());
            maintainTaskPO.setInspectorName(dto.getInspectorName());
            boolean flag = Stream.of("1","2","3","4").anyMatch(v -> v.equals(dto.getEquipType()));
            if (flag){ // 使用时间作为判断条件
                maintainTaskPO.setStartDate(dto.getInitialDate());
            } else { // 不使用时间作为判断条件
                dto.setInitialDate(toDate(LocalDate.now()));
                maintainTaskPO.setStartDate(toDate(LocalDate.now()));
                maintainTaskPO.setInitialNumber(dto.getInitialNumber());
                maintainTaskPO.setDeadlineNumber(dto.getInitialNumber().add(new BigDecimal(dto.getCycle())));
                dto.setDeadlineNumber(maintainTaskPO.getDeadlineNumber());
            }
            maintainTaskPO.setEndDate(DateUtils.addDays(dto.getInitialDate(),Integer.parseInt(dto.getTimeLimit())-1));
            dto.setDeadlineDate(maintainTaskPO.getEndDate());

            // 生成保养任务 -- 子表
            List<MaintainTaskItemPO> taskItemList = new ArrayList<>();
            dto.getItemList().stream().forEach(v -> {
                MaintainTaskItemPO maintainTaskItemPO = new MaintainTaskItemPO();
                maintainTaskItemPO.setId(snowflake.nextId());
                maintainTaskItemPO.setStandardId(v.getStandardId());
                maintainTaskItemPO.setEquipTaskId(maintainTaskPO.getId());
                maintainTaskItemPO.setEquipPlanId(dto.getId());
                maintainTaskItemPO.setEquipPlanItemId(v.getId());
                maintainTaskItemPO.setEquipSmallCategoryId(v.getEquipSmallCategoryId());
                maintainTaskItemPO.setEquipSmallCategoryName(v.getEquipSmallCategoryName());
                maintainTaskItemPO.setEquipInstitutionId(v.getEquipInstitutionId());
                maintainTaskItemPO.setEquipInstitutionName(v.getEquipInstitutionName());
                maintainTaskItemPO.setEquipUnitId(v.getEquipUnitId());
                maintainTaskItemPO.setEquipUnitName(v.getEquipUnitName());
                maintainTaskItemPO.setContent(v.getContent());
                maintainTaskItemPO.setStandard(v.getStandard());
                maintainTaskItemPO.setEquipType(v.getEquipType());
                taskItemList.add(maintainTaskItemPO);
            });
            // 新增主表
            mapper.insert(dto);
            // 新增点检任务
            mapper.insertPlanTask(maintainTaskPO);
            // 新增点检任务子表
            mapper.insertPlanTaskItem(taskItemList);
        } else {
            boolean flag = Stream.of("1","2","3","4").anyMatch(v -> v.equals(dto.getEquipType()));
            if (flag){ // 使用时间作为判断条件
                // 获取截止日期
                dto.setDeadlineDate(DateUtils.addDays(dto.getInitialDate(),Integer.parseInt(dto.getTimeLimit())-1));
            } else { // 不使用时间作为判断条件
                dto.setDeadlineNumber(dto.getInitialNumber().add(new BigDecimal(dto.getCycle())));
                dto.setDeadlineDate(DateUtils.addDays(dto.getInitialDate(),Integer.parseInt(dto.getTimeLimit())-1));
            }
            mapper.update(dto);
            // 对保养计划子表先删后插
            mapper.deletePlanItemByPlanId(dto);
            dto.getItemList().stream().forEach(v -> {
                v.setId(snowflake.nextId());
                v.setEquipPlanId(dto.getId());
                v.setEquipSmallCategoryId(dto.getEquipSmallCategoryId());
                v.setEquipSmallCategoryName(dto.getEquipSmallCategoryName());
            });
            // 新增子表
            mapper.insertPlanItem(dto.getItemList());
        }

    }

    @Override
    public void deleteById(Long id) {
        mapper.deleteById(id);
    }

    @Override
    public List<MEquipmentInfoPO> getEquipListById(Long id) {
        List<MEquipmentInfoPO> list = mapper.getEquipListById(id);
        return list;
    }

    @Override
    @Transactional(rollbackFor = Exception.class,isolation = Isolation.READ_COMMITTED)
    public void report(MaintainPlanPO dto) {
        MaintainPlanPO po = mapper.queryById(dto.getId());
        List<MaintainPlanItemPO> list = mapper.getPlanItem(dto.getId());

        MaintainTaskPO maintainTaskPO = new MaintainTaskPO();
        maintainTaskPO.setId(snowflake.nextId());
        maintainTaskPO.setEquipPlanId(po.getId());
        maintainTaskPO.setPlanType(po.getPlanType());
        maintainTaskPO.setEquipId(po.getEquipId());
        maintainTaskPO.setEquipName(po.getEquipName());
        maintainTaskPO.setStartDate(toDate(LocalDate.now()));
        maintainTaskPO.setEndDate(DateUtils.addDays(maintainTaskPO.getStartDate(),Integer.parseInt(po.getTimeLimit())-1));
        maintainTaskPO.setInitialNumber(po.getInitialNumber());
        maintainTaskPO.setDeadlineNumber(po.getDeadlineNumber());
        maintainTaskPO.setInspectorId(po.getInspectorId());
        maintainTaskPO.setInspectorName(po.getInspectorName());
        // 任务主表
        mapper.insertPlanTask(maintainTaskPO);

        List<MaintainTaskItemPO> itemPOList = new ArrayList<>();
        list.stream().forEach(v -> {
            MaintainTaskItemPO maintainTaskItemPO = new MaintainTaskItemPO();
            maintainTaskItemPO.setId(snowflake.nextId());
            maintainTaskItemPO.setEquipTaskId(maintainTaskPO.getId());
            maintainTaskItemPO.setStandardId(v.getStandardId());
            maintainTaskItemPO.setEquipPlanId(v.getEquipPlanId());
            maintainTaskItemPO.setEquipPlanItemId(v.getId());
            maintainTaskItemPO.setEquipSmallCategoryId(v.getEquipSmallCategoryId());
            maintainTaskItemPO.setEquipSmallCategoryName(v.getEquipSmallCategoryName());
            maintainTaskItemPO.setEquipInstitutionId(v.getEquipInstitutionId());
            maintainTaskItemPO.setEquipInstitutionName(v.getEquipInstitutionName());
            maintainTaskItemPO.setEquipUnitId(v.getEquipUnitId());
            maintainTaskItemPO.setEquipUnitName(v.getEquipUnitName());
            maintainTaskItemPO.setContent(v.getContent());
            maintainTaskItemPO.setStandard(v.getStandard());
            maintainTaskItemPO.setEquipType(v.getEquipType());
            itemPOList.add(maintainTaskItemPO);
        });
        mapper.insertPlanTaskItem(itemPOList);

        // 更新计划表
        po.setRecentlyTaskDate(toDate(LocalDate.now()));
        mapper.updateById(po);
    }

    /**
     * 定时新增保养任务
     */
//    @Scheduled(cron="0 0 0 * * ?")
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public void timeTask () {
        LOGGER.enter("定时新增点检任务开始");

        // 查询需要轮询的点检计划
        List<MaintainPlanPO> list = mapper.getInspectionPlanList();
        // 查询点检计划子表信息
        List<MaintainPlanItemPO> itemPOList = mapper.getInspectionPlanItemList();
        // 获取设备运行数据
        List<MEquipmentOperationPO> equipmentList = mapper.getEquipmentOperationList();

        // 点检任务 - 主
        List<MaintainTaskPO> taskList = new ArrayList<>();
        // 点检任务 - 子
        List<MaintainTaskItemPO> taskItemList = new ArrayList<>();
        // 点检计划
        List<MaintainPlanPO> planList = new ArrayList<>();

        Date today = new Date();
        LocalDate today1 = LocalDate.now();

        for (MaintainPlanPO po : list) {
            // true 设置日期 false 设置数字
            boolean flag = Stream.of("1","2","3","4").anyMatch(j -> j.equals(po.getEquipType()));
            // 生成保养任务 -- 主表
            MaintainTaskPO maintainTaskPO = new MaintainTaskPO();
            // 润滑保养计划
            MaintainPlanPO maintainPlanPO = new MaintainPlanPO();
            // TODO 判断每个类型 决定需不需要继续走下去
            // 日 当前时间小于结束时间
            if ("1".equals(po.getEquipType()) && toDate(today1).compareTo(po.getDeadlineDate()) <= 0) {
                continue;
            }
            // 周 -- 判断今天是否大于截止日期并且今天与所选择的日期（周一到周天）相同
            if ("2".equals(po.getEquipType()) && toDate(today1).compareTo(po.getDeadlineDate()) >= 0) {
                // 判断今天是星期几
                DayOfWeek dayOfWeek = getDayOfWeek(today);
                int day = dayOfWeek.getValue(); // 1=周一, 7=周日
                if (day != Integer.parseInt(po.getSetDate()) || toDate(today1).compareTo(po.getInitialDate())==0) {
                    continue;
                }
            }
            // 月 -- 判断今天是否大于截止日期并且今天与所选择的日期（1号到30号）相同
            if ("3".equals(po.getEquipType()) && toDate(today1).compareTo(po.getDeadlineDate()) >= 0) {
                // 获取今天是几号（1到31之间的数字）
                int dayOfMonth = today1.getDayOfMonth();
                if (dayOfMonth != Integer.parseInt(po.getSetDate()) || toDate(today1).compareTo(po.getInitialDate())==0) {
                    continue;
                }
            }
            // 年 判断是不是一月一号 并且当前时间大于结束时间
            if ("4".equals(po.getEquipType())  && toDate(today1).compareTo(po.getDeadlineDate()) >= 0) {
                // 获取月份（1-12）
                int month = today1.getMonthValue();
                // 获取日（1-31）
                int day = today1.getDayOfMonth();
                if ((month != 1 && day != 1) || toDate(today1).compareTo(po.getInitialDate())==0) {
                    continue;
                }
            }
            // 在功能中获取该设备的或者其他类型的台时、里程、吨数等数据
            if (!flag) {
                // 计数器 -- 如果相同设备条件下，录入的台时、里程等小于点检截止数据跳过本次循环不生成新任务
                int num = 0;
                for (MEquipmentOperationPO equipmentOperationPO : equipmentList) {
                    // 运行台时
                    if ("5".equals(po.getEquipType()) && equipmentOperationPO.getEquipId().equals(po.getEquipId())) {
                        if (equipmentOperationPO.getRunTime().compareTo(po.getDeadlineNumber()) < 0) {
                            num = num+1;
                            break;
                        }
                    }
                    // 运行里程
                    if ("6".equals(po.getEquipType()) && equipmentOperationPO.getEquipId().equals(po.getEquipId())) {
                        if (equipmentOperationPO.getRunMileage().compareTo(po.getDeadlineNumber()) < 0) {
                            num = num+1;
                            break;
                        }
                    }
                }
                if (num>0){
                    continue;
                }
            }
            maintainTaskPO.setId(snowflake.nextId());
            maintainTaskPO.setEquipPlanId(po.getId());
            maintainTaskPO.setEquipId(po.getEquipId());
            maintainTaskPO.setEquipName(po.getEquipName());
            maintainTaskPO.setStartDate(toDate(today1));
            maintainTaskPO.setEndDate(DateUtils.addDays(toDate(today1),Integer.parseInt(po.getTimeLimit())-1));
            maintainTaskPO.setInspectorId(po.getInspectorId());
            maintainTaskPO.setInspectorName(po.getInspectorName());
            if (!flag) {
                maintainTaskPO.setInitialNumber(po.getDeadlineNumber());
                maintainTaskPO.setDeadlineNumber(po.getDeadlineNumber().add(new BigDecimal(po.getCycle())));
                maintainPlanPO.setDeadlineNumber(maintainTaskPO.getDeadlineNumber());
            }
            maintainTaskPO.setCreateBy(1L);
            maintainTaskPO.setCreateByName("定时任务新增");
            maintainTaskPO.setCreateTime(new Date());
            taskList.add(maintainTaskPO);

            // TODO 生成点检任务 -- 子表
            for (MaintainPlanItemPO itemPO : itemPOList) {
                if (po.getId().equals(itemPO.getEquipPlanId())) {
                    // 点检任务 - 子
                    MaintainTaskItemPO maintainTaskItemPO = new MaintainTaskItemPO();
                    maintainTaskItemPO.setId(snowflake.nextId());
                    maintainTaskItemPO.setStandardId(itemPO.getStandardId());
                    maintainTaskItemPO.setEquipTaskId(maintainTaskPO.getId());
                    maintainTaskItemPO.setEquipPlanId(po.getId());
                    maintainTaskItemPO.setEquipPlanItemId(itemPO.getId());
                    maintainTaskItemPO.setEquipSmallCategoryId(itemPO.getEquipSmallCategoryId());
                    maintainTaskItemPO.setEquipSmallCategoryName(itemPO.getEquipSmallCategoryName());
                    maintainTaskItemPO.setEquipInstitutionId(itemPO.getEquipInstitutionId());
                    maintainTaskItemPO.setEquipInstitutionName(itemPO.getEquipInstitutionName());
                    maintainTaskItemPO.setEquipUnitId(itemPO.getEquipUnitId());
                    maintainTaskItemPO.setEquipUnitName(itemPO.getEquipUnitName());
                    maintainTaskItemPO.setContent(itemPO.getContent());
                    maintainTaskItemPO.setStandard(itemPO.getStandard());
                    maintainTaskItemPO.setEquipType(itemPO.getEquipType());
                    maintainTaskItemPO.setCreateBy(1L);
                    maintainTaskItemPO.setCreateByName("定时任务新增");
                    maintainTaskItemPO.setCreateTime(new Date());
                    taskItemList.add(maintainTaskItemPO);
                }
            }
            // TODO 更新点击计划截止日期
            maintainPlanPO.setId(po.getId());
            maintainPlanPO.setDeadlineDate(maintainTaskPO.getEndDate());
            maintainPlanPO.setRecentlyTaskDate(new Date());
            planList.add(maintainPlanPO);
        }
        // 新增点检任务 - 主
        if (taskList != null && taskList.size() > 0) {
            mapper.insertPlanTaskList(taskList);
        }
        // 新增点检任务 - 子
        if (taskItemList != null && taskItemList.size() > 0) {
            mapper.insertPlanTaskItemList(taskItemList);
        }
        // 修改点检计划
        if (planList != null && planList.size() > 0) {
            mapper.updatePlanList(planList);
        }

        LOGGER.exit("定时新增点检任务结束");
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

    /**
     * 根据数字计算日期
     * @param date
     * @param dayNumber
     * @return
     */
    public static Date getNextMonthDate(Date date, int dayNumber) {
        // 将Date转换为LocalDate
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        // 获取下个月的年和月
        LocalDate nextMonth = localDate.plusMonths(1);
        int year = nextMonth.getYear();
        int month = nextMonth.getMonthValue();

        // 获取下个月的最大天数
        int maxDaysInMonth = YearMonth.of(year, month).lengthOfMonth();

        // 如果dayNumber大于下个月的实际天数，则使用最大天数
        int dayToUse = Math.min(dayNumber, maxDaysInMonth);

        // 计算下个月的日期
        LocalDate nextMonthDate = LocalDate.of(year, month, dayToUse);
        return Date.from(nextMonthDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    /**
     * 获取给定日期的下一年年份
     * @param date 给定日期
     * @return 下一年年份（整数）
     */
    public static int getNextYear(Date date) {
        // 将Date转换为LocalDate
        LocalDate localDate = date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        // 获取当前年份并加1
        return localDate.getYear() + 1;
    }
    /**
     * 获取给定日期下一年一月一日的日期
     */
    public static Date getNextYearDate(Date date) {
        // 将Date转换为LocalDate
        LocalDate localDate = date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        // 下一年同月同日
        LocalDate nextYearDate = localDate.plusYears(1)  // 加一年
                                          .withMonth(1)   // 设置为一月
                                          .withDayOfMonth(1);  // 设置为一号
        return Date.from(nextYearDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
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
}
