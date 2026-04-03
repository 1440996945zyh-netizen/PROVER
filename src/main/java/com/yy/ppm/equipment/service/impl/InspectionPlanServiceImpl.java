package com.yy.ppm.equipment.service.impl;


import cn.hutool.core.lang.Snowflake;
import com.yy.common.log.MicroLogger;
import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.common.util.DateUtils;
import com.yy.common.util.PageHelperUtils;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.equipment.bean.dto.InspectionPlanDTO;
import com.yy.ppm.equipment.bean.dto.InspectionPlanTaskDTO;
import com.yy.ppm.equipment.bean.po.*;
import com.yy.ppm.equipment.mapper.InspectionPlanMapper;
import com.yy.ppm.equipment.service.EPatrolPlanService;
import com.yy.ppm.equipment.service.InspectionPlanService;
import jakarta.annotation.Resource;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
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
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.yy.ppm.equipment.service.impl.EPatrolPlanServiceImpl.toDate;

@Component("InspectionPlanServiceImpl")
@Service
public class InspectionPlanServiceImpl implements InspectionPlanService {
    /**
     * 日志组件
     **/
    private static final MicroLogger LOGGER = new MicroLogger(InspectionPlanServiceImpl.class);
    @Autowired
    private InspectionPlanMapper inspectionPlanMapper;
    @Resource
    private Snowflake snowflake;


    @Autowired
    private EPatrolPlanService ePatrolPlanService;

    @Override
    public Pages<InspectionPlanPO> queryAll(InspectionPlanDTO inspectionPlanDTO, PageParameter parameter) {
        Pages<InspectionPlanPO> pages = PageHelperUtils.limit(parameter, () -> {
            return inspectionPlanMapper.queryAll(inspectionPlanDTO);
        });
        return pages;
    }

    @Override
    public InspectionPlanPO getById(Long id) {
        InspectionPlanPO po = inspectionPlanMapper.queryById(id);
        po.setItemList(inspectionPlanMapper.getPlanItem(id));
        return po;
    }

    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public void save(InspectionPlanPO dto) {

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
            // 新增点检计划子表
            inspectionPlanMapper.insertPlanItem(dto.getItemList());

            //  周期类
            boolean flag = Stream.of("1","2","3","4").anyMatch(v -> v.equals(dto.getEquipType()));
            boolean isInsert = false;

            //当前时间
            LocalDate today1 = LocalDate.now();
            //判断当前时候是否等于初始时间
            if (!flag || toDate(today1).compareTo(dto.getInitialDate()) == 0  ) {

                if (flag && ePatrolPlanService.isCreateTask(dto.getEquipType(), dto.getInitialDate(),dto.getSetDate())){ // 是周期
                    isInsert = true;
                }else if (!flag && dto.getInitialNumber().divideAndRemainder(new BigDecimal(dto.getCycle()))[1].compareTo(BigDecimal.ZERO)==0){

                    isInsert = true;

                }


                if (isInsert){
                    // 生成点检任务 -- 主表
                    InspectionPlanTaskPO inspectionPlanTaskPO = new InspectionPlanTaskPO();
                    inspectionPlanTaskPO.setId(snowflake.nextId());
                    inspectionPlanTaskPO.setEquipPlanId(dto.getId());
                    inspectionPlanTaskPO.setEquipId(dto.getEquipId());
                    inspectionPlanTaskPO.setEquipName(dto.getEquipName());
                    inspectionPlanTaskPO.setInspectorId(dto.getInspectorId());
                    inspectionPlanTaskPO.setInspectorName(dto.getInspectorName());
                    if (flag){ // 使用时间作为判断条件
                        inspectionPlanTaskPO.setStartDate(dto.getInitialDate());
                    } else { // 不使用时间作为判断条件
                        dto.setInitialDate(toDate(LocalDate.now()));
                        inspectionPlanTaskPO.setStartDate(toDate(LocalDate.now()));
                        inspectionPlanTaskPO.setInitialNumber(dto.getInitialNumber());
                        inspectionPlanTaskPO.setDeadlineNumber(dto.getInitialNumber().add(new BigDecimal(dto.getCycle())));
                        dto.setDeadlineNumber(inspectionPlanTaskPO.getDeadlineNumber());
                    }
                    inspectionPlanTaskPO.setEndDate(DateUtils.addDays(dto.getInitialDate(),Integer.parseInt(dto.getTimeLimit())-1));
                    dto.setDeadlineDate(inspectionPlanTaskPO.getEndDate());
                    dto.setRecentlyTaskDate(new Date());

                    // 生成点检任务 -- 子表
                    List<InspectionPlanTaskItemPO> taskItemList = new ArrayList<>();
                    dto.getItemList().stream().forEach(v -> {
                        InspectionPlanTaskItemPO inspectionPlanTaskItemPO = new InspectionPlanTaskItemPO();
                        inspectionPlanTaskItemPO.setId(snowflake.nextId());
                        inspectionPlanTaskItemPO.setStandardId(v.getStandardId());
                        inspectionPlanTaskItemPO.setEquipTaskId(inspectionPlanTaskPO.getId());
                        inspectionPlanTaskItemPO.setEquipPlanId(dto.getId());
                        inspectionPlanTaskItemPO.setEquipPlanItemId(v.getId());
                        inspectionPlanTaskItemPO.setEquipSmallCategoryId(v.getEquipSmallCategoryId());
                        inspectionPlanTaskItemPO.setEquipSmallCategoryName(v.getEquipSmallCategoryName());
                        inspectionPlanTaskItemPO.setEquipInstitutionId(v.getEquipInstitutionId());
                        inspectionPlanTaskItemPO.setEquipInstitutionName(v.getEquipInstitutionName());
                        inspectionPlanTaskItemPO.setEquipUnitId(v.getEquipUnitId());
                        inspectionPlanTaskItemPO.setEquipUnitName(v.getEquipUnitName());
                        inspectionPlanTaskItemPO.setContent(v.getContent());
                        inspectionPlanTaskItemPO.setStandard(v.getStandard());
                        inspectionPlanTaskItemPO.setEquipType(v.getEquipType());
                        taskItemList.add(inspectionPlanTaskItemPO);
                    });
                    // 新增点检任务
                    inspectionPlanMapper.insertPlanTask(inspectionPlanTaskPO);
                    // 新增点检任务子表
                    inspectionPlanMapper.insertPlanTaskItem(taskItemList);
                }
            }


            // 新增点检计划
            inspectionPlanMapper.insert(dto);

        } else {
            boolean flag = Stream.of("1","2","3","4").anyMatch(v -> v.equals(dto.getEquipType()));
            if (flag){ // 使用时间作为判断条件
                // 获取截止日期
                dto.setDeadlineDate(DateUtils.addDays(dto.getInitialDate(),Integer.parseInt(dto.getTimeLimit())-1));
            } else { // 不使用时间作为判断条件
                dto.setDeadlineNumber(dto.getInitialNumber().add(new BigDecimal(dto.getCycle())));
                dto.setDeadlineDate(DateUtils.addDays(dto.getInitialDate(),Integer.parseInt(dto.getTimeLimit())-1));
            }
            inspectionPlanMapper.update(dto);

            // 对点检计划子表先删后插
            inspectionPlanMapper.deletePlanItemByPlanId(dto);
            dto.getItemList().stream().forEach(v -> {
                v.setId(snowflake.nextId());
                v.setEquipPlanId(dto.getId());
                v.setEquipSmallCategoryId(dto.getEquipSmallCategoryId());
                v.setEquipSmallCategoryName(dto.getEquipSmallCategoryName());
            });
            // 新增点检计划子表
            inspectionPlanMapper.insertPlanItem(dto.getItemList());
        }
    }

    /**
     * 获取日期
     * @param dto
     * @return
     */
    public static Date getEndDate(InspectionPlanPO dto) {
        Date date = null;
        if ("1".equals(dto.getEquipType())) { // 天
            if ("0".equals(dto.getCycle())){
                throw new BusinessRuntimeException("周期不能为0");
            }
            date =DateUtils.addDays(dto.getInitialDate(),Integer.parseInt(dto.getCycle())-1);
        } else if ("2".equals(dto.getEquipType())) { // 周
            // 获取给定日期是星期几
            DayOfWeek dayOfWeek = getDayOfWeek(dto.getInitialDate());
            int day = dayOfWeek.getValue(); // 1=周一, 7=周日
            // 7 - 计算出的星期 + 选择的数字 = 需要加的天数
            int days = 7-day+Integer.parseInt(dto.getSetDate());
            date = DateUtils.addDays(dto.getInitialDate(),days);
        } else if ("3".equals(dto.getEquipType())) { // 月
            date = getNextMonthDate(dto.getInitialDate(),Integer.parseInt(dto.getSetDate()));
        } else if ("4".equals(dto.getEquipType())) { // 年
            date = getNextYearDate(dto.getInitialDate());
        }
        return date;
    }

    @Override
    public void deleteById(Long id) {
        inspectionPlanMapper.deleteById(id);
    }

    @Override
    public List<MEquipmentInfoPO> getEquipListById(Long id) {
        List<MEquipmentInfoPO> list = inspectionPlanMapper.getEquipListById(id);
        return list;
    }

    @Override
    public Pages<InspectionPlanTaskPO> getTaskDetail(InspectionPlanTaskDTO inspectionPlanDTO, PageParameter parameter) {
        Pages<InspectionPlanTaskPO> pages = PageHelperUtils.limit(parameter, () -> {
            return inspectionPlanMapper.getTaskDetail(inspectionPlanDTO);
        });
        return pages;
    }

    /**
     * 定时新增点检任务
     */
//    @Scheduled(cron="0 0 0 * * ?")
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public void timeTask () {
        LOGGER.enter("定时新增点检任务开始");

        // 查询需要轮询的点检计划
        List<InspectionPlanPO> list = inspectionPlanMapper.getInspectionPlanList();
        // 查询点检计划子表信息
        List<InspectionPlanItemPO> itemPOList = inspectionPlanMapper.getInspectionPlanItemList();
        // 获取设备运行数据
        List<MEquipmentOperationPO> equipmentList = inspectionPlanMapper.getEquipmentOperationList();

        // 点检任务 - 主
        List<InspectionPlanTaskPO> taskList = new ArrayList<>();
        // 点检任务 - 子
        List<InspectionPlanTaskItemPO> taskItemList = new ArrayList<>();
        // 点检计划
        List<InspectionPlanPO> planList = new ArrayList<>();

        Date today = new Date();
        LocalDate today1 = LocalDate.now();

        for (InspectionPlanPO po : list) {
            // true 设置日期 false 设置数字
            boolean flag = Stream.of("1","2","3","4").anyMatch(j -> j.equals(po.getEquipType()));
            // 生成点检任务 -- 主表
            InspectionPlanTaskPO inspectionPlanTaskPO = new InspectionPlanTaskPO();
            // 点检计划
            InspectionPlanPO inspectionPlanPO = new InspectionPlanPO();
            // TODO 判断每个类型 决定需不需要继续走下去


            if (flag){
            //判断当前时间是否小于开始日期 小于跳过不新增
            if (toDate(today1).compareTo(po.getInitialDate()) < 0) {
                continue;
            }else if ("2".equals(po.getEquipType())) {  // 周 -- 判断今天是否大于开始日期并且今天与所选择的日期（周一到周天）相同
                // 判断今天是星期几
                DayOfWeek dayOfWeek = getDayOfWeek(today);
                int day = dayOfWeek.getValue(); // 1=周一, 7=周日
                if (day != Integer.parseInt(po.getSetDate())) {
                    continue;
                }
            }else if ("3".equals(po.getEquipType()) ) {      // 月 -- 判断今天是否大于截止日期并且今天与所选择的日期（1号到30号）相同
                // 获取今天是几号（1到31之间的数字）
                int dayOfMonth = today1.getDayOfMonth();
                if (dayOfMonth != Integer.parseInt(po.getSetDate())) {
                    continue;
                }
            }else if ("4".equals(po.getEquipType()) ) {     // 年 判断是不是一月一号 并且当前时间大于结束时间
                // 获取月份（1-12）
                int month = today1.getMonthValue();
                // 获取日（1-31）
                int day = today1.getDayOfMonth();
                if ((month != Integer.parseInt(po.getSetDate()) && day != 1)) {
                    continue;
                }
            }}
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
            inspectionPlanTaskPO.setId(snowflake.nextId());
            inspectionPlanTaskPO.setEquipPlanId(po.getId());
            inspectionPlanTaskPO.setEquipId(po.getEquipId());
            inspectionPlanTaskPO.setEquipName(po.getEquipName());
            inspectionPlanTaskPO.setStartDate(toDate(today1));
            inspectionPlanTaskPO.setEndDate(DateUtils.addDays(toDate(today1),Integer.parseInt(po.getTimeLimit())-1));
            inspectionPlanTaskPO.setInspectorId(po.getInspectorId());
            inspectionPlanTaskPO.setInspectorName(po.getInspectorName());
            if (!flag) {
                inspectionPlanTaskPO.setInitialNumber(po.getDeadlineNumber());
                inspectionPlanTaskPO.setDeadlineNumber(po.getDeadlineNumber().add(new BigDecimal(po.getCycle())));
                inspectionPlanPO.setDeadlineNumber(inspectionPlanTaskPO.getDeadlineNumber());
            }
            inspectionPlanTaskPO.setCreateBy(1L);
            inspectionPlanTaskPO.setCreateByName("定时任务新增");
            inspectionPlanTaskPO.setCreateTime(new Date());
            taskList.add(inspectionPlanTaskPO);

            // TODO 生成点检任务 -- 子表
            for (InspectionPlanItemPO itemPO : itemPOList) {
                if (po.getId().equals(itemPO.getEquipPlanId())) {
                    // 点检任务 - 子
                    InspectionPlanTaskItemPO inspectionPlanTaskItemPO = new InspectionPlanTaskItemPO();
                    inspectionPlanTaskItemPO.setId(snowflake.nextId());
                    inspectionPlanTaskItemPO.setStandardId(itemPO.getStandardId());
                    inspectionPlanTaskItemPO.setEquipTaskId(inspectionPlanTaskPO.getId());
                    inspectionPlanTaskItemPO.setEquipPlanId(po.getId());
                    inspectionPlanTaskItemPO.setEquipPlanItemId(itemPO.getId());
                    inspectionPlanTaskItemPO.setEquipSmallCategoryId(itemPO.getEquipSmallCategoryId());
                    inspectionPlanTaskItemPO.setEquipSmallCategoryName(itemPO.getEquipSmallCategoryName());
                    inspectionPlanTaskItemPO.setEquipInstitutionId(itemPO.getEquipInstitutionId());
                    inspectionPlanTaskItemPO.setEquipInstitutionName(itemPO.getEquipInstitutionName());
                    inspectionPlanTaskItemPO.setEquipUnitId(itemPO.getEquipUnitId());
                    inspectionPlanTaskItemPO.setEquipUnitName(itemPO.getEquipUnitName());
                    inspectionPlanTaskItemPO.setContent(itemPO.getContent());
                    inspectionPlanTaskItemPO.setStandard(itemPO.getStandard());
                    inspectionPlanTaskItemPO.setEquipType(itemPO.getEquipType());
                    inspectionPlanTaskItemPO.setCreateBy(1L);
                    inspectionPlanTaskItemPO.setCreateByName("定时任务新增");
                    inspectionPlanTaskItemPO.setCreateTime(new Date());
                    taskItemList.add(inspectionPlanTaskItemPO);
                }
            }
            // TODO 更新点击计划截止日期
            inspectionPlanPO.setId(po.getId());
            inspectionPlanPO.setDeadlineDate(inspectionPlanTaskPO.getEndDate());
            inspectionPlanPO.setRecentlyTaskDate(new Date());
            planList.add(inspectionPlanPO);
        }
        // 新增点检任务 - 主
        if (taskList != null && taskList.size() > 0) {
            inspectionPlanMapper.insertPlanTaskList(taskList);
        }
        // 新增点检任务 - 子
        if (taskItemList != null && taskItemList.size() > 0) {
            inspectionPlanMapper.insertPlanTaskItemList(taskItemList);
        }
        // 修改点检计划
        if (planList != null && planList.size() > 0) {
            inspectionPlanMapper.updatePlanList(planList);
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
