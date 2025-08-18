package com.yy.ppm.common.service.impl;

import com.yy.common.enums.CommonEnum;
import com.yy.common.log.MicroLogger;
import com.yy.common.util.DateUtils;
import com.yy.common.util.SecurityUtils;
import com.yy.common.util.str.StringUtil;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.common.enums.ShiftClassEnum;
import com.yy.ppm.common.mapper.PublicMapper;
import com.yy.ppm.common.service.PublicService;
import com.yy.ppm.master.bean.po.MDictDataPO;
import com.yy.ppm.master.bean.po.MWorkSchedulePO;
import com.yy.ppm.system.bean.dto.SysDeptDTO;
import com.yy.ppm.system.bean.dto.SysParameterDTO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.yy.common.util.str.StringUtil.getString;

/**
 * 公共服务接口实现类
 */
@Service
public class PublicServiceImpl implements PublicService {

    /**
     * 日志组件
     **/
    private static final MicroLogger LOGGER = new MicroLogger(PublicServiceImpl.class);

    @Resource
    public PublicMapper publicMapper;

	@Resource
	private SecurityUtils securityUtils;

    /**
     * 根据字典类型获取字典值
     * */
    @Override
    public Map<String, Object> getDictList(List<String> dictTypeList) {
        final String methodName = "PublicServiceImpl:getDictList";
        LOGGER.enter(methodName, "查询字典值");

        List<Map<String, Object>> dictList = publicMapper.getDictList(dictTypeList);
        Map<String, Object> dictMap = new HashMap<>();
        // 处理多个字典类型情况
        for (String v1 : dictTypeList) {
            dictMap.put(v1, dictList.stream().filter(v2 -> v1.equals(getString(v2.get("dictType"))))
                    .collect(Collectors.toList()));
        }
        LOGGER.exit(methodName, StringUtils.EMPTY);
        return dictMap;
    }

    /**
     * 通过单个字典类型获取字典数据
     * @param type
     * @return
     */
    @Override
    public List<Map<String, Object>> getDictListByType(String type) {

        List<Map<String, Object>> resList = publicMapper.getDictListByType(type);

        return resList;
    }

    /**
     * 根据常量类型查询常量
     */
    @Override
    public Map<String, Object> getConstantList(List<String> typeList) {
        final String methodName = "PublicServiceImpl:getConstantList";
        LOGGER.enter(methodName, "查询常量值");

        List<Map<String, Object>> list = publicMapper.getConstantList(typeList);
        Map<String, Object> tempMap = new HashMap<>();
        for (String v1 : typeList) {
            tempMap.put(v1, list.stream().filter(v2 -> v1.toUpperCase().equals(getString(v2.get("typeCd")).toUpperCase()))
                    .collect(Collectors.toList()));
        }

        LOGGER.exit(methodName, StringUtils.EMPTY);
        return tempMap;
    }

    /**
     * 获取系统参数
     */
    @Override
    public SysParameterDTO getSysParamByCode(String code) {

        final String methodName = "PublicServiceImpl:getSysParamByCode";
        LOGGER.enter(methodName, "获取系统参数");

        SysParameterDTO dto = publicMapper.getSysParamByCode(code);

        LOGGER.exit(methodName, StringUtils.EMPTY);
        return dto;
    }

    /**
     * 传入计划日期，返回计划的执行开始结束时间
     * @param planDte
     * @return
     */
    @Override
    public String getShiftClassInfoByPlanDate(String planDte) {

        final String methodName = "PublicServiceImpl:getWorkScheduleByPlanDate";
        LOGGER.enter(methodName, "业务执行");

        if (StringUtil.isEmpty(planDte)) {
            throw new BusinessRuntimeException("请输入计划日期~");
        }

        List<Map<String, Object>> result = publicMapper.getScheduleTypeList();

        if (CollectionUtils.isEmpty(result)) {
            return "";
        }

        String res = "";

        // 开始
        res += StringUtil.getString(result.get(0).get("startDayName"));
        res += (" " + StringUtil.getString(result.get(0).get("endTimeHm")));

        res += " ~ ";
        // 结束
        res += StringUtil.getString(result.get(result.size() - 1).get("endDayName"));
        res += (" " + StringUtil.getString(result.get(result.size() - 1).get("startTimeHm")));
        return res;

    }

    /**
     * 获取时间戳的班次 TODO
     * @param
     * @return
     */
    public Map<String, Object> getCurrentShiftClassInfo(String time) {
        HashMap<String,Object> res = new HashMap<>();

        String day = "";

        // 日期为空时默认系统当前日期，格式话：HHmm 和YYYYMMDD
        if (StringUtil.isEmpty(time)) {
            time = DateUtils.formatDate(new Date(), CommonEnum.DateFormatType.E_91.getCode());
            day = DateUtils.formatDate(new Date(), CommonEnum.DateFormatType.E_1.getCode());
        } else {
            time = DateUtils.formatDate(DateUtils.parseDate(time, CommonEnum.DateFormatType.E_71.getCode()), CommonEnum.DateFormatType.E_91.getCode());
            day = DateUtils.formatDate(DateUtils.parseDate(time, CommonEnum.DateFormatType.E_1.getCode()), CommonEnum.DateFormatType.E_1.getCode());
        }

        List<Map<String, Object>> result = publicMapper.getScheduleTypeList();
        Map<String, Object> resultMap = null;
        Map<String, Object> tempMap = new HashMap<String, Object>();

        if (result == null || result.size() == 0) {
            throw new BusinessRuntimeException("无数据！");
        }

        int  thisMinute = Integer.parseInt(time);
        int  startMinute;
        int  endMinute;

        for (int i = 0; i < result.size(); i++) {
            startMinute = StringUtil.getInt(result.get(i).get("startTimeHm"));
            endMinute = StringUtil.getInt(result.get(i).get("endTimeHm"));

            // 跨日的场合
            if (startMinute > endMinute) {
                tempMap = result.get(i);

                // 同日的场合
            } else {
               // if (thisMinute >= startMinute && thisMinute < endMinute) {
                    resultMap =  result.get(i);
                    break;
               // }
            }
        }

        // 开始结束日期在同一天的
        String dayType = "";
        if (resultMap != null) {
            dayType = StringUtil.getString(resultMap.get("startDayType"));
            res.put("classCode", resultMap.get("workScheduleCode"));
            res.put("className", resultMap.get("workScheduleName"));

            // 开始结束日期不在同一天
        } else {

            startMinute = StringUtil.getInt(tempMap.get("startTimeHm"));

            if (thisMinute >= startMinute && thisMinute <=2359) {
                dayType = StringUtil.getString(resultMap.get("startDayType"));
            } else {
                dayType = StringUtil.getString(resultMap.get("endDayType"));
            }
        }

        if (ShiftClassEnum.YESTERDAY.getCode().equals(dayType)) {
            res.put("workDate", DateUtils.addDay(day, 1));
            // 本日
        } else if (ShiftClassEnum.TODAY.getCode().equals(dayType)) {
            res.put("workDate", day);
            // 明日
        } else if (ShiftClassEnum.TOMORROW.getCode().equals(dayType)) {
            res.put("workDate", DateUtils.addDay(day, -1));
        }

        return res;

    }

    /**
     * 获取机械列表
     * @param map
     * @return
     */
    @Override
    public List<Map<String, Object>> getMachineList(Map<String, Object> map) {

    	// 查询当前登陆人所在部门的部门CODE add by zcc 23/10/20
    	List<Map<String, Object>> userInfoAndDeptInfoList = publicMapper.getUserInfoAndDeptInfo(securityUtils.getLoginUserId());
    	if(CollectionUtils.isNotEmpty(userInfoAndDeptInfoList)) {
    		if(userInfoAndDeptInfoList.get(0).get("isSuperadmin") != null
    				&& "1".equals(userInfoAndDeptInfoList.get(0).get("isSuperadmin").toString())) {

    		} else {
    			if(userInfoAndDeptInfoList.get(0).get("canDispatchDept") != null) {
    				String canDispatchDept = "";
    				for (String data : userInfoAndDeptInfoList.get(0).get("canDispatchDept").toString().split(",")) {
    					canDispatchDept += ("OR SD.DEPT_NO like '" + data + "%'");
					}

                    map.put("canDispatchDept", canDispatchDept);
    			}
            	map.put("deptNo", userInfoAndDeptInfoList.get(0).get("deptNo"));
                if("0001000100050006".equals(userInfoAndDeptInfoList.get(0).get("deptNo"))){
                    //特殊处理，固机队维修班的权限用固机队的
                    map.put("deptNo","000100010005");
                }
    		}
    	}

        return publicMapper.getMachineList(map);
    }
    /**
     * 获取机械列表
     * @param map
     * @return
     */
    @Override
    public List<Map<String, Object>> getMachineList2(Map<String, Object> map) {
        return publicMapper.getMachineList2(map);
    }

    /**** 组织架构 ****/
    /**
     * 获取全部组织部门信息
     * */
    @Override
    public List<SysDeptDTO> getDeptList(SysDeptDTO deptDTO) {
        return publicMapper.getDeptList(deptDTO);
    }

    @Override
    public List<Map<String, Object>> listMass(Long regionId) {
        return publicMapper.listMass(regionId);
    }

    @Override
    public Map<String, Object> getDateAndShift(LocalDateTime dateTime) {
        dateTime = dateTime == null ? LocalDateTime.now() : dateTime;
        LocalDate date = dateTime.toLocalDate();
        LocalTime time = dateTime.toLocalTime();

        List<MDictDataPO> dictates = publicMapper.listDictData();
        List<MWorkSchedulePO> tempWorkSchedules = publicMapper.listWorkSchedule();

        Triple<Boolean, Integer, String> tuple = dictates.stream()
                .map(v1 -> {
                    MWorkSchedulePO workSchedule = tempWorkSchedules.stream()
                            .filter(v2 -> v1.getDictValue().equals(v2.getWorkScheduleCode()))
                            .findFirst()
                            .orElseThrow(() -> new BusinessRuntimeException("找不到匹配班次的工班设置"));
                    LocalTime startTime = workSchedule.getStartTime().toInstant().atZone(ZoneId.systemDefault()).toLocalTime();
                    LocalTime endTime = workSchedule.getEndTime().toInstant().atZone(ZoneId.systemDefault()).toLocalTime();

                    boolean bool = false;
                    int dateOffset = 0;
                    if (startTime.isBefore(endTime)) {
                        if (!startTime.isAfter(time) && time.isBefore(endTime)) {
                            bool = true;
                            if ("0".equals(workSchedule.getStartDayType())) {
                                dateOffset = 1;
                            }
                            if ("2".equals(workSchedule.getStartDayType())) {
                                dateOffset = -1;
                            }
                        }
                    } else if (startTime.isAfter(endTime)) {
                        if (!startTime.isAfter(time)) {
                            bool = true;
                            if ("0".equals(workSchedule.getStartDayType())) {
                                dateOffset = 1;
                            }
                            if ("2".equals(workSchedule.getStartDayType())) {
                                dateOffset = -1;
                            }
                        } else if (time.isBefore(endTime)) {
                            bool = true;
                            if ("0".equals(workSchedule.getStartDayType())) {
                                dateOffset = -2;
                            }
                            if ("1".equals(workSchedule.getStartDayType())) {
                                dateOffset = -1;
                            }
                        }
                    }
                    return Triple.of(bool, dateOffset, workSchedule.getWorkScheduleCode());
                })
                .filter(Triple::getLeft)
                .findFirst()
                .orElseThrow(() -> new BusinessRuntimeException("错误的工班设置"));

        LocalDate workDate = date.plusDays(tuple.getMiddle());

        Map<String, Object> result = new HashMap<>();
        result.put("workDate", workDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        result.put("classCode", tuple.getRight());
        result.put("className", dictates.stream().filter(v1 -> tuple.getRight().equals(v1.getDictValue())).map(MDictDataPO::getDictLabel).findFirst().orElseThrow(null));
        return result;
    }
}
