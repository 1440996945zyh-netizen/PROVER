package com.yy.ppm.dispatch.service.impl;

import java.math.BigDecimal;
import java.math.MathContext;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import jakarta.annotation.Resource;

import cn.hutool.core.util.ObjectUtil;
import com.google.common.collect.Maps;
import com.yy.common.util.SecurityUtils;
import com.yy.ppm.dispatch.bean.dto.*;
import com.yy.ppm.dispatch.bean.po.TDisPortDaynightplanPO;
import com.yy.ppm.system.bean.dto.SysParameterDTO;
import com.yy.ppm.system.mapper.SysParameterMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.xmlbeans.impl.xb.xsdschema.All;
import org.checkerframework.checker.units.qual.A;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yy.common.enums.CommonEnum;
import com.yy.common.util.DateUtils;
import com.yy.common.util.str.StringUtil;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.business.mapper.TBusTrustMapper;
import com.yy.ppm.dispatch.mapper.TBusTrustLocationMapper;
import com.yy.ppm.dispatch.mapper.TDisPortDaynightplanMapper;
import com.yy.ppm.dispatch.service.TDisPortDaynightplanService;

import cn.hutool.core.lang.Snowflake;
import lombok.SneakyThrows;

/**
 * @ClassName 集疏港昼夜计划(TDisPortDaynightplan)ServiceImpl
 * @author yy
 * @version 1.0.0
 * @Description
 * @createTime 2023年11月14日 10:31:00
 */
@Service
public class TDisPortDaynightplanServiceImpl implements TDisPortDaynightplanService {

    @Resource
    private TDisPortDaynightplanMapper tDisPortDaynightplanMapper;
    @Resource
    private TBusTrustMapper tBusTrustMapper;
    @Resource
    private TBusTrustLocationMapper tBusTrustLocationMapper;
    @Resource
	private Snowflake snowflake;
    @Resource
    private SysParameterMapper sysParameterMapper;
    @Resource
    private SecurityUtils securityUtils;


    // 审核通过状态
    public static final String STATUS_APPROVED = "20";
    // 待审核/审核撤销状态
    public static final String STATUS_NOT_APPROVE = "10";

    /**
     * 按日期查询昼夜计划详情
     *
     * @param query
     * @return 实体
     */
    @Override
    public List<TDisPortDaynightplanDTO> getList(TDisPortDaynightplanSearch2DTO query) {
        List<TDisPortDaynightplanDTO> list = tDisPortDaynightplanMapper.getList(query);

        /*//获取 单车预估重量 的系统参数
        Integer forecastWeight = tDisPortDaynightplanMapper.getForecastWeight();
        // 遍历唯一的计划号
        for (TDisPortDaynightplanDTO x : list) {

            // 一次过磅的车数*单车预估数+二次过磅的总净重
            BigDecimal tonCount = new BigDecimal(0);
            tonCount = tDisPortDaynightplanMapper.getCount(x.getBusinessNo(), forecastWeight);
            //过磅量赋值
            x.setWeighCount(tonCount);

            //计算主界面计划量和 昼夜计划量
            BigDecimal  allPlanTon = new BigDecimal(0) ;
            for(TDisPortDaynightplanDTO y : list){
                if(x.getBusinessNo().equals(y.getBusinessNo())){
                    allPlanTon = allPlanTon.add(y.getPlanTon());
                }
            }

            BigDecimal totally = new BigDecimal(0) ;
            if(tonCount!=null){
                //一次过磅的车数*单车预估数+二次过磅的总净重 +昼夜计划量
                totally = allPlanTon.add(tonCount);
            }else{ }

            //获取总计划量
            BigDecimal allTon = new BigDecimal(0) ;
            allTon = tDisPortDaynightplanMapper.getCargoTon(x.getBusinessNo());
            //剩余量赋值
            x.setRemainCount(allTon.subtract(totally));
        }*/
        //获取 单车预估重量 的系统参数
        Integer forecastWeight = tDisPortDaynightplanMapper.getForecastWeight();
        // 遍历
        for (TDisPortDaynightplanDTO x : list) {

            // 一次过磅的车数*单车预估数+二次过磅的总净重(过磅量)
            BigDecimal tonCount = new BigDecimal(0);
            tonCount = tDisPortDaynightplanMapper.getCount(x.getBusinessNo(), forecastWeight);
            if(tonCount != null){
                //过磅量赋值
                x.setWeighCount(tonCount);
            }else{
                x.setWeighCount(new BigDecimal(0));
            }

           /* //计算主界面计划量和 （昼夜计划量）
            BigDecimal  allPlanTon = new BigDecimal(0) ;
            for(TDisPortDaynightplanDTO y : list){
                if(x.getBusinessNo().equals(y.getBusinessNo())){
                    allPlanTon = allPlanTon.add(y.getPlanTon());
                }
            }*/

            //获取总计划量
            BigDecimal allPlanTon = new BigDecimal(0) ;
            allPlanTon = tDisPortDaynightplanMapper.getCargoTon(x.getBusinessNo());
            if(allPlanTon != null){
                //计划总量
                x.setAllPlanCount(allPlanTon);
            }else {
                x.setAllPlanCount(new BigDecimal(0));
            }
            //计划剩余量
            if(x.getWeighCount() != null && x.getAllPlanCount() != null){
                //计划剩余量 = 计划总量-过磅量
               x.setRemainPlanCount(x.getAllPlanCount().subtract(x.getWeighCount()));
            }else{
                //若过磅量为null 则等于计划总量
                x.setRemainPlanCount(x.getAllPlanCount());
            }
            Date startDate = x.getBeginTime();
            Date endDate = x. getEndTime();
            BigDecimal dnPlanTimeCount = new BigDecimal(0);
            dnPlanTimeCount = tDisPortDaynightplanMapper.getCountByTime(x.getBusinessNo(), forecastWeight,startDate,endDate);
            if(dnPlanTimeCount != null && x.getAllPlanCount() != null){
                //昼夜计划总量-昼夜计划这段时间的过磅量
                x.setRemainCount(x.getPlanTon().subtract(dnPlanTimeCount));
            }else {
                //如果昼夜计划时间内的过磅量为null 就等于计划总量
                x.setRemainCount(x.getPlanTon());
            }


        }
        return list;
    }

    /**
     * 未作计划的票货数据
     *
     * @param dto
     * @return 实体
     */
    @Override
    public List<TDisPortDaynightplanDTO> getTrustCargoDetail(TDisPortDaynightplanDTO dto) {
        List<TDisPortDaynightplanDTO> list = tDisPortDaynightplanMapper.getTrustCargoDetail(dto);
        // 拼接船名航次
        List<Long> trustIds = list.stream().filter(v1 -> v1.getShipvoyageItemId() == null).map(TDisPortDaynightplanDTO::getTrustId).collect(Collectors.toList());
        if (!trustIds.isEmpty()) {
            List<Long> limitIdList = trustIds.stream().limit(500).collect(Collectors.toList());
            List<Map<String, Object>> shipvoyageItems = tBusTrustMapper.listShipvoyageItemByTrustIds(limitIdList);
            list.stream().filter(v1 -> limitIdList.contains(v1.getId()))
                    .forEach(v1 -> {
                        String shipNameVoyages = shipvoyageItems.stream()
                                .filter(v2 -> v1.getId().equals(Long.valueOf(String.valueOf(v2.get("trustId")))))
                                .map(v2 -> String.valueOf(v2.get("shipNameVoyage")))
                                .collect(Collectors.joining("，"));
                        v1.setShipvoyageName(shipNameVoyages);
                    });
        }
        // 查询所有作业计划的场地位置并拼接
        List<Long> allTrustIds = list.stream().map(TDisPortDaynightplanDTO::getTrustId).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(allTrustIds)) {
            List<Long> limitIdList = allTrustIds.stream().limit(500).collect(Collectors.toList());
            List<TBusTrustLocationDTO> locationList = tBusTrustLocationMapper.getListByTrustIds(limitIdList);
            Map<Long, String> idMap = new HashMap<>();
            Map<Long, String> labelMap = new HashMap<>();
            locationList.stream().forEach(item -> {
                Long trustId = item.getTrustId();
                String label = item.getStorehouseName() + "/" + item.getRegionName();
                if (labelMap.containsKey(trustId)) {
                    labelMap.put(trustId, labelMap.get(trustId) + "," + label);
                } else {
                    labelMap.put(trustId, label);
                }
                if (idMap.containsKey(trustId)) {
                    idMap.put(trustId, idMap.get(trustId) + "," + item.getId());
                } else {
                    idMap.put(trustId, String.valueOf(item.getId()));
                }
            });
            list.stream().forEach(item -> {
                if (idMap.containsKey(item.getTrustId())) {
                    item.setMassNamesTarget(idMap.get(item.getTrustId()));
                }
                if (labelMap.containsKey(item.getTrustId())) {
                    item.setMassNamesTargetLabel(labelMap.get(item.getTrustId()));
                }
            });
        }
        return list;
    }

    /**
      * 保存
      *
      * @param list
      * @param strPlanDate
      * @return 实体
      */
     @Override
     @Transactional(rollbackFor = Exception.class)
     public boolean doSave(List<TDisPortDaynightplanDTO> list, String strPlanDate) {
        int count = 0;
        if (StringUtil.isEmpty(strPlanDate)) {
            throw new BusinessRuntimeException("请输入昼夜计划日期~");
        }
        for(TDisPortDaynightplanDTO temp :list){
            if (temp.getBeginTime().compareTo(temp.getEndTime()) >= 0) {
                throw new BusinessRuntimeException(temp.getBusinessNo() + "的计划结束时间应大于计划开始时间");
            }
        }
         //传过来的时间
         Date strPlanDate1 = DateUtils.parseDate(strPlanDate, CommonEnum.DateFormatType.E_1.getCode());
         LocalDate strPlanDate2 = strPlanDate1.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
         LocalDateTime today18 = strPlanDate2.atTime(18, 0);
         LocalDate tomorrow = strPlanDate2.plusDays(1);
         LocalDateTime tomorrow18 = tomorrow.atTime(18, 0);
         for(TDisPortDaynightplanDTO x : list){
             LocalDateTime xBeginTime = x.getBeginTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
             LocalDateTime xEndTime = x.getEndTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
             if (xBeginTime.isBefore(today18) || xEndTime.isAfter(tomorrow18)) {
                 // 如果超出范围，抛出异常
                 throw new BusinessRuntimeException(x.getBusinessNo()+":"+x.getCargoOwnerName()+"   时间范围应在"+strPlanDate2+"18:00到下一天18:00");
             }
         }
         /*Date beginTime = new Date();
         Optional<Date> earliestDateOptional = list.stream()
                 .map(TDisPortDaynightplanDTO::getBeginTime)
                 .min(Date::compareTo);
         if (earliestDateOptional.isPresent()) {
             beginTime = earliestDateOptional.get();
             LocalDate beginTimeDate = beginTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
             //传过来的值是否在计划开始时间之前
             if(strPlanDate2.isBefore(beginTimeDate)) {
                 throw new BusinessRuntimeException("昼夜计划日期不应早于当前计划日期");
             }
         }*/

         Long loginUserId = securityUtils.getLoginUserId();
         List<String> roleCodeList = tDisPortDaynightplanMapper.getUserRoleById(loginUserId);
         Optional<String> JSGRoleCode = roleCodeList .stream()
                 .filter(x -> x.equals("JSGZYJHSH"))
                 .findFirst(); // 拿到第一个
         LocalTime currentTime = LocalTime.now();
            // 定义限制的时间
         LocalTime limitTime = LocalTime.of(13, 30);
         if (!JSGRoleCode.isPresent() && currentTime.isAfter(limitTime)) {
             throw new BusinessRuntimeException("13.30之后禁止修改新增操作");
         }

/*        //传过来的值是否在当前日期之前
         if(strPlanDate2.isBefore(currentDate1)){
            throw new BusinessRuntimeException("昼夜计划日期不应早于今天");
        }*/
        //判断时间段
         for (int i = 0; i < list.size(); i++) {
             TDisPortDaynightplanDTO x = list.get(i);

             for (int j = i + 1; j < list.size(); j++) {
                 TDisPortDaynightplanDTO y = list.get(j);

                 if (x.getBusinessNo().equals(y.getBusinessNo()) && x.getClassCode().equals(y.getClassCode())) {
                     // 将 Date 转换为 LocalDateTime
                     LocalDateTime xBeginTime = x.getBeginTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                     LocalDateTime xEndTime = x.getEndTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                     LocalDateTime yBeginTime = y.getBeginTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                     LocalDateTime yEndTime = y.getEndTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

                     // 检查是否存在重叠时间段
                     if ((xBeginTime.isBefore(yEndTime)) && (xEndTime.isAfter(yBeginTime))) {
                         throw new BusinessRuntimeException(x.getBusinessNo() + "_全天的时间段重叠");
                     }
                 }
             }
         }

         Date planDate = DateUtils.parseDate(strPlanDate, CommonEnum.DateFormatType.E_1.getCode());

         LocalDate currentDate = LocalDate.now();
         DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
         String formattedDate = currentDate.format(formatter);
         if(planDate != null && planDate.after(DateUtils.parseDate(formattedDate, CommonEnum.DateFormatType.E_1.getCode()))) {
             throw new BusinessRuntimeException("计划日期不能早于当前时间！");
         }
         Date planStartTime = DateUtils.setHour(planDate, 18);
         Date planEndTime = DateUtils.setHour(DateUtils.addDays(planStartTime, 1), 18);
         // 先删除
         //tDisPortDaynightplanMapper.deleteByPlanDate(strPlanDate);
         for (TDisPortDaynightplanDTO dto : list) {
             if (dto.getId() != null) {
                 count += tDisPortDaynightplanMapper.update(dto);
             } else {
                 dto.setId(snowflake.nextId());
                 dto.setPlanDate(planDate);
//                dto.setBeginTime(planStartTime);
//                dto.setEndTime(planEndTime);
                 dto.setStatus(STATUS_NOT_APPROVE);
                 count += tDisPortDaynightplanMapper.insert(dto);
             }
         }

         /*//获取唯一计划号
         Set<String> uniqueBusinessNos = list.stream()
                 .map(TDisPortDaynightplanDTO::getBusinessNo)
                 .collect(Collectors.toSet());*/
/*
         //获取 单车预估重量 的系统参数
         Integer forecastWeight = tDisPortDaynightplanMapper.getForecastWeight();*/

         SysParameterDTO sysParameter = sysParameterMapper.getByKey("DAY_NIGHT_TON_SWITCH");
         boolean oneCarPlan = ObjectUtil.isEmpty(sysParameter)?false:("Y".equals(sysParameter.getParamVal())?true:false);
         //true是开  false是关

         if(oneCarPlan) {

             List<String> businessNoList = list.stream().map(x -> x.getBusinessNo()).distinct().collect(Collectors.toList());
             if(!businessNoList.isEmpty()) {
                 //所有未过期的计划数据
                 List<TDisPortDaynightplanDTO> AllList = tDisPortDaynightplanMapper.getAllList(businessNoList);

                 Map<String, List<TDisPortDaynightplanDTO>> businessNoMap = AllList.stream()
                         .collect(Collectors.groupingBy(TDisPortDaynightplanDTO::getBusinessNo));

                 // 遍历
                 for (String businessNo : businessNoList) {

                     //获取总计划量
                     BigDecimal allTon = tDisPortDaynightplanMapper.getCargoTon(businessNo);
                     //根据计划号拿到
                     List<TDisPortDaynightplanDTO> timeList = businessNoMap.get(businessNo);

                     if (CollectionUtils.isEmpty(timeList)) {
                         BigDecimal totalExpireTon = tDisPortDaynightplanMapper.getExpireAllTon(businessNo);
                         if (allTon.subtract(totalExpireTon).compareTo(BigDecimal.ZERO) < 0) {
                             throw new BusinessRuntimeException(businessNo + "超过总计划量无法保存");
                         }
                     } else {
                         // 求未过期的昼夜计划量
                         BigDecimal totalPlanTon = timeList.stream()
                                 .map(TDisPortDaynightplanDTO::getPlanTon)
                                 .reduce(BigDecimal.ZERO, BigDecimal::add);

                         allTon = allTon.subtract(totalPlanTon);

                         //存未过期的最早时间
                         Optional<Date> earlyBeginTime = timeList.stream()
                                 .map(TDisPortDaynightplanDTO::getBeginTime) // 获取开始时间
                                 .sorted() // 排序
                                 .findFirst();
                         Date earlyBeginTimeDate = earlyBeginTime.orElse(null);

                         // 过期计划的所有过磅量(一次过榜时间小于未过期的最早的开始时间)
                         BigDecimal totalExpireTon = tDisPortDaynightplanMapper.getExpireTon(businessNo, earlyBeginTimeDate);
                         if (totalExpireTon == null) {
                             totalExpireTon = BigDecimal.ZERO;
                         }
                         if (allTon.subtract(totalExpireTon).compareTo(BigDecimal.ZERO) < 0) {
                             throw new BusinessRuntimeException(businessNo + "超过总计划量无法保存");
                         }
                     }
                 }
             }
         }

      /*  if (CollectionUtils.isEmpty(list)) {
            count = 1;
        } else {
        	Set<String> set = new HashSet<>();
        	List<String> duplicates = new ArrayList<>();
        	for (TDisPortDaynightplanDTO data : list) {
        	    if (!set.add(data.getBusinessNo() + "_" + ("01".equals(data.getClassCode())?"白班":"夜班"))) {
        	        duplicates.add(data.getBusinessNo() + "_" + ("01".equals(data.getClassCode())?"白班":"夜班"));
        	    }
        	}
            //判断重复 不需要了
        	*//*if(CollectionUtils.isNotEmpty(duplicates)) {
                throw new BusinessRuntimeException(duplicates.get(0).split("_")[0] + " 计划号" + duplicates.get(0).split("_")[1] + "重复~");
        	}*//*
        }*/
        // 字符串转日期
 /*       Date planDate = DateUtils.parseDate(strPlanDate, CommonEnum.DateFormatType.E_1.getCode());

        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedDate = currentDate.format(formatter);
        if(planDate != null && planDate.after(DateUtils.parseDate(formattedDate, CommonEnum.DateFormatType.E_1.getCode()))) {
            throw new BusinessRuntimeException("计划日期不能早于当前时间！");
        }
        Date planStartTime = DateUtils.setHour(planDate, 18);
        Date planEndTime = DateUtils.setHour(DateUtils.addDays(planStartTime, 1), 18);
        // 先删除
        //tDisPortDaynightplanMapper.deleteByPlanDate(strPlanDate);
        for (TDisPortDaynightplanDTO dto : list) {
            if (dto.getId() != null) {
                count += tDisPortDaynightplanMapper.update(dto);
            } else {
                dto.setId(snowflake.nextId());
                dto.setPlanDate(planDate);
//                dto.setBeginTime(planStartTime);
//                dto.setEndTime(planEndTime);
                dto.setStatus(STATUS_NOT_APPROVE);
                count += tDisPortDaynightplanMapper.insert(dto);
            }
        }*/
        return count > 0;
    }

    /**
     * 昨日计划导入与查询
     *
     * @param strPlanDate
     * @return 实体
     */
    @SneakyThrows
    @Override
    public List<TDisPortDaynightplanDTO> importTodayPlan(String strPlanDate) {
        Date planDate = DateUtils.parseDate(strPlanDate, CommonEnum.DateFormatType.E_1.getCode());


        Date nightPlanStartTime = DateUtils.setHour(planDate, 18);

        Date nightPlanEndTime = DateUtils.setHour(DateUtils.addDays(nightPlanStartTime, 1), 8);


        Date mornPlanStartTime = DateUtils.setHour(DateUtils.addDays(nightPlanStartTime, 1), 8);

        Date mornPlanEndTime = DateUtils.setHour(DateUtils.addDays(nightPlanStartTime, 1), 18);


        /*Date planStartTime = DateUtils.setHour(planDate, 18);
        Date planEndTime = DateUtils.setHour(DateUtils.addDays(planStartTime, 1), 18);*/

        //查询传过来的日期的昼夜计划
        TDisPortDaynightplanSearch2DTO nowDate = new TDisPortDaynightplanSearch2DTO();
        nowDate.setPlanDate(strPlanDate);
        List<TDisPortDaynightplanDTO> nowList = tDisPortDaynightplanMapper.getList(nowDate);

        String strYeaterday = DateUtils.addDay(strPlanDate, -1);
        TDisPortDaynightplanSearch2DTO tDisPortDaynightplanSearch2DTO = new TDisPortDaynightplanSearch2DTO();
        tDisPortDaynightplanSearch2DTO.setPlanDate(strYeaterday);
        tDisPortDaynightplanSearch2DTO.setPackingCode("1");
        //昨日计划数据
        List<TDisPortDaynightplanDTO> list = tDisPortDaynightplanMapper.getList(tDisPortDaynightplanSearch2DTO);


        // 从 nowList 中获取班次和计划号，并将其拼接在一起作为键，值是计划号
        Map<String, String> classCodeAndBusinessNoToBusinessNoMap = nowList.stream()
                .collect(Collectors.toMap(
                        item -> item.getClassCode() + "_" + item.getBusinessNo(), // 自定义键的拼接方式
                        TDisPortDaynightplanPO::getBusinessNo,
                        (existingValue, newValue) -> existingValue // 合并函数，保留已存在的值
                ));

        // 过滤掉与 classCodeAndBusinessNoToBusinessNoMap 中键相同的项
        List<TDisPortDaynightplanDTO> filteredList = list.stream()
                .filter(item -> !classCodeAndBusinessNoToBusinessNoMap.containsKey(item.getClassCode() + "_" + item.getBusinessNo()))
                .collect(Collectors.toList());

         /*// 从 nowList 中获取计划号列表
        List<String> businessNoList = nowList.stream()
                .map(TDisPortDaynightplanPO::getBusinessNo)
                .collect(Collectors.toList());*/

        /*List<TDisPortDaynightplanDTO> filteredList = list.stream()
                .filter(item -> !businessNoList.contains(item.getBusinessNo())) // 过滤掉已经存在于 nowList 中的数据
                .collect(Collectors.toList());*/


        filteredList.stream().forEach(item -> {
            item.setId(snowflake.nextId());
            item.setStatus(STATUS_NOT_APPROVE);
            item.setPlanDate(planDate);
            if(item.getClassCode().equals("01")){
                item.setBeginTime(mornPlanStartTime);
                item.setEndTime(mornPlanEndTime);
            }else if(item.getClassCode().equals("02")){
                item.setBeginTime(nightPlanStartTime);
                item.setEndTime(nightPlanEndTime);
            } else if(item.getClassCode().equals("03")){
                item.setBeginTime(nightPlanStartTime);
                item.setEndTime(mornPlanEndTime);
            }
            tDisPortDaynightplanMapper.insert(item);
        });
        return list;
    }
    /**
     * 审批通过
     * @Param id
     * @return 是否成功
     */
    @Override
    public boolean approveById(Long id) {
        TDisPortDaynightplanDTO dto = tDisPortDaynightplanMapper.getById(id);
        if (STATUS_APPROVED.equals(dto.getStatus())) {
            throw new BusinessRuntimeException("计划已审核，不允许重复审核");
        }
        dto.setStatus(STATUS_APPROVED);
        return tDisPortDaynightplanMapper.approveById(dto) == 1;
    }
    /**
     * 审批撤销
     * @Param id
     * @return 是否成功
     */
    @Override
    public boolean revokeById(Long id) {
        TDisPortDaynightplanDTO dto = tDisPortDaynightplanMapper.getById(id);
        if (STATUS_NOT_APPROVE.equals(dto.getStatus())) {
            throw new BusinessRuntimeException("计划未审核，不需要撤销");
        }
        dto.setStatus(STATUS_NOT_APPROVE);
        return tDisPortDaynightplanMapper.approveRevokeById(dto) == 1;
    }

    /**
     * 根据id删除
     * @Param id
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteById(Long id) {
        TDisPortDaynightplanDTO dto = tDisPortDaynightplanMapper.getById(id);
        int count = 0;
        count = tDisPortDaynightplanMapper.getEntrustById(id);
        if(count>0){
            throw new BusinessRuntimeException("已有委托，不允许删除");
        }
        if (STATUS_APPROVED.equals(dto.getStatus())) {
            throw new BusinessRuntimeException("计划已审核，不允许删除");
        }
        Long loginUserId = securityUtils.getLoginUserId();
        List<String> roleCodeList = tDisPortDaynightplanMapper.getUserRoleById(loginUserId);
        Optional<String> JSGRoleCode = roleCodeList.stream()
                .filter(x -> x.equals("JSGZYJHSH"))
                .findFirst(); // 拿到第一个
        int countCode = 0;
        if (JSGRoleCode.isPresent()) {
            String roleCode = JSGRoleCode.get();
            countCode = tDisPortDaynightplanMapper.deleteById(id);

        } else {
            if(dto.getCreateBy().equals(loginUserId)){
                countCode = tDisPortDaynightplanMapper.deleteById(id);
            }else{
                throw new BusinessRuntimeException("只能删除自己创建的计划");
            }
        }
        return countCode > 0;
        /*return tDisPortDaynightplanMapper.deleteById(id) == 1;*/
    }


	@Override
    @Transactional(rollbackFor = Exception.class)
	public void tosToBoHaiTongDayNightPlanTask(Long id) {
//		if(id != null) {
//			TDisPortDaynightplanDTO data = tDisPortDaynightplanMapper.getById(id);
//			if(!"20".equals(data.getStatus())) {
//	            throw new BusinessRuntimeException("当前计划尚未审核，无法同步！");
//			}
//			if(data != null && data.getBeginTime() != null && data.getEndTime() != null) {
//				// 调用渤海通接口
//				try {
//		            String url = bhtConfig.getShipForecastUrl() + "/basedOnTheCode/updateCarWorkDate";
//		            Request<List<Map<String, Object>>> request = new Request<List<Map<String, Object>>>();
//		            request.setCustomerCode("test");
//		            request.setSignature("test");
//		            request.setTimestamp(String.valueOf(System.currentTimeMillis()));
//		            List<Map<String, Object>> dataList = Lists.newArrayList();
//		            Map<String, Object> dataMap = Maps.newHashMap();
//		            dataMap.put("planNo", data.getBusinessNo());
//		            dataMap.put("planStartTime", new SimpleDateFormat("yyyy-MM-dd HH:mm").format(data.getBeginTime()));
//		            dataMap.put("planEndTime", new SimpleDateFormat("yyyy-MM-dd HH:mm").format(data.getEndTime()));
//		            dataList.add(dataMap);
//
//		            request.setData(dataList);
//		            String res = HttpUtils.postJson(ApiEnum.HTTPS, url, JSONUtil.toJsonStr(request),Maps.newHashMap());
//		            //return ExternalInterface.SUCCESS.newBuilder().toResult(res);
//		        }catch (Exception e){
//		            throw new BusinessRuntimeException("调用渤海通接口失败！");
//		        }
//				// 修改
//				tDisPortDaynightplanMapper.updateVehicleReservation(data);
//
//			}
//		} else {
//			String param = tDisPortDaynightplanMapper.getSysParameter();
//
//			if("Y".equals(param)) {
//				LocalDateTime now = LocalDateTime.now();
//		        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//		        String currentTimeString = now.format(formatter);
//
//				List<TDisPortDaynightplanDTO> daynightplanList = tDisPortDaynightplanMapper.getListToBoHaiTong(currentTimeString);
//				if(CollectionUtils.isNotEmpty(daynightplanList)) {
//
//		            List<Map<String, Object>> dataList = Lists.newArrayList();
//
//					for (TDisPortDaynightplanDTO data : daynightplanList) {
//
//						if(data.getBeginTime() != null && data.getEndTime() != null) {
//							// 修改
//							tDisPortDaynightplanMapper.updateVehicleReservation(data);
//
//				            Map<String, Object> dataMap = Maps.newHashMap();
//				            dataMap.put("planNo", data.getBusinessNo());
//				            dataMap.put("planStartTime", new SimpleDateFormat("yyyy-MM-dd HH:mm").format(data.getBeginTime()));
//				            dataMap.put("planEndTime", new SimpleDateFormat("yyyy-MM-dd HH:mm").format(data.getEndTime()));
//
//				            dataList.add(dataMap);
//						}
//					}
//
//					// 调用渤海通接口
//					try {
//			            String url = bhtConfig.getShipForecastUrl() + "/basedOnTheCode/updateCarWorkDate";
//			            Request<List<Map<String, Object>>> request = new Request<List<Map<String, Object>>>();
//			            request.setCustomerCode("test");
//			            request.setSignature("test");
//			            request.setTimestamp(String.valueOf(System.currentTimeMillis()));
//
//			            request.setData(dataList);
//			            String res = HttpUtils.postJson(ApiEnum.HTTPS, url, JSONUtil.toJsonStr(request),Maps.newHashMap());
//			            //return ExternalInterface.SUCCESS.newBuilder().toResult(res);
//			        }catch (Exception e){
//			            throw new BusinessRuntimeException("调用渤海通接口失败！");
//			        }
//				}
//			}
//		}
	}

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approveList(List<TDisPortDaynightplanDTO> list) {

    /*    //传过来的时间
        Date strPlanDate1 =list.get(0).getPlanDate();
        LocalDate strPlanDate2 = strPlanDate1.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDateTime today18 = strPlanDate2.atTime(18, 0);
        LocalDate tomorrow = strPlanDate2.plusDays(1);
        LocalDateTime tomorrow18 = tomorrow.atTime(18, 0);
        for(TDisPortDaynightplanDTO x : list){
            LocalDateTime xBeginTime = x.getBeginTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            LocalDateTime xEndTime = x.getEndTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            if (xBeginTime.isBefore(today18) || xEndTime.isAfter(tomorrow18)) {
                // 如果超出范围，抛出异常
                throw new BusinessRuntimeException(x.getBusinessNo()+":"+x.getCargoOwnerName()+"   时间范围应在"+strPlanDate2+"18:00到下一天18:00");
            }
        }
*/
        String planDate = DateUtils.formatDate(list.get(0).getPlanDate(),"yyyy-MM-dd");
        TDisPortDaynightplanSearch2DTO query = new TDisPortDaynightplanSearch2DTO();
        query.setPlanDate(planDate);
        List<TDisPortDaynightplanDTO> list1 = tDisPortDaynightplanMapper.getList(query);


        for (TDisPortDaynightplanDTO x : list) {
            for (TDisPortDaynightplanDTO y : list1) {
                // 跳过自身的比较
                if (x.getId().equals(y.getId())) {
                    continue;
                }
                if (x.getBusinessNo().equals(y.getBusinessNo())) {
                    LocalDateTime xBeginTime = x.getBeginTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                    LocalDateTime xEndTime = x.getEndTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                    LocalDateTime yBeginTime = y.getBeginTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                    LocalDateTime yEndTime = y.getEndTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                    // 检查是否存在重叠时间段
                    if (xBeginTime.isBefore(yEndTime) && xEndTime.isAfter(yBeginTime)) {
                        throw new BusinessRuntimeException(x.getBusinessNo() + "计划号存在时间段重叠，无法审核");
                    }
                }
            }
        }
        List<Long> ids = list.stream().map(x->x.getId()).collect(Collectors.toList());
        TDisPortDaynightplanDTO dto = new TDisPortDaynightplanDTO();
        dto.setIds(ids);
        dto.setStatus(STATUS_APPROVED);
        //批量审核
        tDisPortDaynightplanMapper.updateApproveList(dto);
    }

    @Override
    public void approveListRevoke(List<TDisPortDaynightplanDTO> list) {

        List<Long> ids = list.stream().map(x->x.getId()).collect(Collectors.toList());
        TDisPortDaynightplanDTO dto = new TDisPortDaynightplanDTO();
        dto.setIds(ids);
        dto.setStatus(STATUS_NOT_APPROVE);
        //批量销审
        tDisPortDaynightplanMapper.cancelApproveListRevoke(dto);
    }

    @Override
    public List<TDisPortDaynightplanExportDTO> exportPlan(String planDate) {
        List<TDisPortDaynightplanExportDTO> result = new ArrayList<>();
        int i = 0;
        for(i = 0;i < 2; i++) {
            TDisPortDaynightplanExportDTO tDisPortDaynightplanExportDTO = new TDisPortDaynightplanExportDTO();
            tDisPortDaynightplanExportDTO.setSheetName(i==0?"散货":"件货");
            TDisPortDaynightplanSearch2DTO query = new TDisPortDaynightplanSearch2DTO();
            query.setPlanDate(planDate);
            query.setPackingCode(i==0?"1":"2");
            List<TDisPortDaynightplanDTO> list = this.getList(query);
            /*if (list == null || CollectionUtils.isEmpty(list)) {
                throw new BusinessRuntimeException("没有查询到今天信息");
            }*/
            //东
            List<TDisPortDaynightplanEastExportDTO> detailList1 = new ArrayList<>();
            List<TDisPortDaynightplanDTO> collect1 = list.stream()
                    .filter(x -> Optional.ofNullable(x.getPortCode()).orElse("").equals("01"))
                    .collect(Collectors.toList());
            if (collect1 == null || CollectionUtils.isEmpty(collect1)) {
                tDisPortDaynightplanExportDTO.setEastAllTon(BigDecimal.ZERO);
            } else {
                BigDecimal totalPlanTon = BigDecimal.ZERO;
                for (TDisPortDaynightplanDTO x : collect1) {
                    TDisPortDaynightplanEastExportDTO tDisPortDaynightplanEastExportDTO = new TDisPortDaynightplanEastExportDTO();
                    tDisPortDaynightplanEastExportDTO.setBusinessNo(x.getBusinessNo());
                    tDisPortDaynightplanEastExportDTO.setShipName(x.getShipvoyageName());
                    tDisPortDaynightplanEastExportDTO.setScn(x.getScn());
                    tDisPortDaynightplanEastExportDTO.setNoticeType(x.getNoticeType());
                    tDisPortDaynightplanEastExportDTO.setCargoOwnerName(x.getCargoOwnerName());
                    tDisPortDaynightplanEastExportDTO.setPackingName(x.getPackingName());
                    tDisPortDaynightplanEastExportDTO.setTradeType(x.getTradeType());
                    tDisPortDaynightplanEastExportDTO.setCargoName(x.getCargoName());
                    tDisPortDaynightplanEastExportDTO.setPlanType(x.getPlanType().equals("1") ? "出入库" : "直取");
                    tDisPortDaynightplanEastExportDTO.setPlanTon(x.getPlanTon());
                    tDisPortDaynightplanEastExportDTO.setRemainCount(x.getRemainCount());
                    tDisPortDaynightplanEastExportDTO.setAllPlanCount(x.getAllPlanCount());
                    tDisPortDaynightplanEastExportDTO.setWeighCount(x.getWeighCount());
                    tDisPortDaynightplanEastExportDTO.setRemainPlanCount(x.getRemainPlanCount());
                    tDisPortDaynightplanEastExportDTO.setMassNamesTargetLabel(x.getMassNamesTargetLabel());
                    tDisPortDaynightplanEastExportDTO.setCreateByName(x.getCreateByName());
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    tDisPortDaynightplanEastExportDTO.setCreateTime(dateFormat.format(x.getCreateTime()));
                    tDisPortDaynightplanEastExportDTO.setExamineByName(x.getExamineByName());

                    Optional<Date> examineTimeOptional = Optional.ofNullable(x.getExamineTime());
                    String examineTimeString = examineTimeOptional.map(dateFormat::format).orElse("");
                    tDisPortDaynightplanEastExportDTO.setExamineTime(examineTimeString);

                    String startEndPlanTime = dateFormat.format(x.getBeginTime()) + " - " + dateFormat.format(x.getEndTime());
                    tDisPortDaynightplanEastExportDTO.setStartEndPlanTime(startEndPlanTime);

                    totalPlanTon = totalPlanTon.add(x.getPlanTon());
                /*if(x.getClassCode().equals("01")){
                    tDisPortDaynightplanEastExportDTO.setPlanTon(x.getPlanTon());
                    totalPlanTon = totalPlanTon.add(x.getPlanTon());
                }else if(x.getClassCode().equals("02")){
                    tDisPortDaynightplanEastExportDTO.setNightPlanTon(x.getPlanTon());
                    totalPlanTon = totalPlanTon.add(x.getPlanTon());
                }else if(x.getClassCode().equals("03")){
                    tDisPortDaynightplanEastExportDTO.setDayPlanTon(x.getPlanTon().divide(new BigDecimal(2), MathContext.DECIMAL128));
                    tDisPortDaynightplanEastExportDTO.setNightPlanTon(x.getPlanTon().divide(new BigDecimal(2),MathContext.DECIMAL128));
                    totalPlanTon = totalPlanTon.add(x.getPlanTon());
                }*/
                    detailList1.add(tDisPortDaynightplanEastExportDTO);
                }
                tDisPortDaynightplanExportDTO.setEastAllTon(totalPlanTon);
            }
            tDisPortDaynightplanExportDTO.setDetailList1(detailList1);

            //中
            List<TDisPortDaynightplanEastExportDTO> detailList2 = new ArrayList<>();
            List<TDisPortDaynightplanDTO> collect2 = list.stream()
                    .filter(x -> Optional.ofNullable(x.getPortCode()).orElse("").equals("02"))
                    .collect(Collectors.toList());
            if (collect2 == null || CollectionUtils.isEmpty(collect2)) {
                tDisPortDaynightplanExportDTO.setMidAllTon(BigDecimal.ZERO);
            } else {
                BigDecimal totalPlanTon = BigDecimal.ZERO;
                for (TDisPortDaynightplanDTO x : collect2) {
                    TDisPortDaynightplanEastExportDTO tDisPortDaynightplanEastExportDTO = new TDisPortDaynightplanEastExportDTO();
                    tDisPortDaynightplanEastExportDTO.setBusinessNo(x.getBusinessNo());
                    tDisPortDaynightplanEastExportDTO.setShipName(x.getShipvoyageName());
                    tDisPortDaynightplanEastExportDTO.setScn(x.getScn());
                    tDisPortDaynightplanEastExportDTO.setNoticeType(x.getNoticeType());
                    tDisPortDaynightplanEastExportDTO.setCargoOwnerName(x.getCargoOwnerName());
                    tDisPortDaynightplanEastExportDTO.setPackingName(x.getPackingName());
                    tDisPortDaynightplanEastExportDTO.setTradeType(x.getTradeType());
                    tDisPortDaynightplanEastExportDTO.setCargoName(x.getCargoName());
                    tDisPortDaynightplanEastExportDTO.setPlanType(x.getPlanType().equals("1") ? "出入库" : "直取");
                    tDisPortDaynightplanEastExportDTO.setPlanTon(x.getPlanTon());
                    tDisPortDaynightplanEastExportDTO.setRemainCount(x.getRemainCount());
                    tDisPortDaynightplanEastExportDTO.setAllPlanCount(x.getAllPlanCount());
                    tDisPortDaynightplanEastExportDTO.setWeighCount(x.getWeighCount());
                    tDisPortDaynightplanEastExportDTO.setRemainPlanCount(x.getRemainPlanCount());
                    tDisPortDaynightplanEastExportDTO.setMassNamesTargetLabel(x.getMassNamesTargetLabel());
                    tDisPortDaynightplanEastExportDTO.setCreateByName(x.getCreateByName());
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    tDisPortDaynightplanEastExportDTO.setCreateTime(dateFormat.format(x.getCreateTime()));
                    tDisPortDaynightplanEastExportDTO.setExamineByName(x.getExamineByName());

                    Optional<Date> examineTimeOptional = Optional.ofNullable(x.getExamineTime());
                    String examineTimeString = examineTimeOptional.map(dateFormat::format).orElse("");
                    tDisPortDaynightplanEastExportDTO.setExamineTime(examineTimeString);

                    String startEndPlanTime = dateFormat.format(x.getBeginTime()) + " - " + dateFormat.format(x.getEndTime());
                    tDisPortDaynightplanEastExportDTO.setStartEndPlanTime(startEndPlanTime);
                    totalPlanTon = totalPlanTon.add(x.getPlanTon());
                    detailList2.add(tDisPortDaynightplanEastExportDTO);
                }
                tDisPortDaynightplanExportDTO.setMidAllTon(totalPlanTon);
            }
            tDisPortDaynightplanExportDTO.setDetailList2(detailList2);

            //西
            List<TDisPortDaynightplanEastExportDTO> detailList3 = new ArrayList<>();
            List<TDisPortDaynightplanDTO> collect3 = list.stream()
                    .filter(x -> Optional.ofNullable(x.getPortCode()).orElse("").equals("03"))
                    .collect(Collectors.toList());
            if (collect3 == null || CollectionUtils.isEmpty(collect3)) {
                tDisPortDaynightplanExportDTO.setWestAllTon(BigDecimal.ZERO);
            } else {
                BigDecimal totalPlanTon = BigDecimal.ZERO;
                for (TDisPortDaynightplanDTO x : collect3) {
                    TDisPortDaynightplanEastExportDTO tDisPortDaynightplanEastExportDTO = new TDisPortDaynightplanEastExportDTO();
                    tDisPortDaynightplanEastExportDTO.setBusinessNo(x.getBusinessNo());
                    tDisPortDaynightplanEastExportDTO.setShipName(x.getShipvoyageName());
                    tDisPortDaynightplanEastExportDTO.setScn(x.getScn());
                    tDisPortDaynightplanEastExportDTO.setNoticeType(x.getNoticeType());
                    tDisPortDaynightplanEastExportDTO.setCargoOwnerName(x.getCargoOwnerName());
                    tDisPortDaynightplanEastExportDTO.setPackingName(x.getPackingName());
                    tDisPortDaynightplanEastExportDTO.setTradeType(x.getTradeType());
                    tDisPortDaynightplanEastExportDTO.setCargoName(x.getCargoName());
                    tDisPortDaynightplanEastExportDTO.setPlanType(x.getPlanType().equals("1") ? "出入库" : "直取");
                    tDisPortDaynightplanEastExportDTO.setPlanTon(x.getPlanTon());
                    tDisPortDaynightplanEastExportDTO.setRemainCount(x.getRemainCount());
                    tDisPortDaynightplanEastExportDTO.setAllPlanCount(x.getAllPlanCount());
                    tDisPortDaynightplanEastExportDTO.setWeighCount(x.getWeighCount());
                    tDisPortDaynightplanEastExportDTO.setRemainPlanCount(x.getRemainPlanCount());
                    tDisPortDaynightplanEastExportDTO.setMassNamesTargetLabel(x.getMassNamesTargetLabel());
                    tDisPortDaynightplanEastExportDTO.setCreateByName(x.getCreateByName());
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    tDisPortDaynightplanEastExportDTO.setCreateTime(dateFormat.format(x.getCreateTime()));
                    tDisPortDaynightplanEastExportDTO.setExamineByName(x.getExamineByName());

                    Optional<Date> examineTimeOptional = Optional.ofNullable(x.getExamineTime());
                    String examineTimeString = examineTimeOptional.map(dateFormat::format).orElse("");
                    tDisPortDaynightplanEastExportDTO.setExamineTime(examineTimeString);

                    String startEndPlanTime = dateFormat.format(x.getBeginTime()) + " - " + dateFormat.format(x.getEndTime());
                    tDisPortDaynightplanEastExportDTO.setStartEndPlanTime(startEndPlanTime);
                    totalPlanTon = totalPlanTon.add(x.getPlanTon());
                    detailList3.add(tDisPortDaynightplanEastExportDTO);
                }
                tDisPortDaynightplanExportDTO.setWestAllTon(totalPlanTon);
            }
            tDisPortDaynightplanExportDTO.setBulkAllTon(tDisPortDaynightplanExportDTO.getMidAllTon().add(tDisPortDaynightplanExportDTO.getWestAllTon()).add(tDisPortDaynightplanExportDTO.getEastAllTon()));
            tDisPortDaynightplanExportDTO.setDetailList3(detailList3);
            result.add(tDisPortDaynightplanExportDTO);
        }
        return result;
    }

    @Override
    public TDisPortDaynightplanDTO getCount(String businessNo) {
        TDisPortDaynightplanDTO dto = new TDisPortDaynightplanDTO();
        //获取过磅量
        BigDecimal tonCount = new BigDecimal(0);
        tonCount = tDisPortDaynightplanMapper.getWeighCount(businessNo);
        if(tonCount != null){
            //过磅量赋值
            dto.setWeighCount(tonCount);
        }else{
            dto.setWeighCount(new BigDecimal(0));
        }

        //获取总计划量
        BigDecimal allPlanTon = new BigDecimal(0) ;
        allPlanTon = tDisPortDaynightplanMapper.getCargoTon(businessNo);
        if(allPlanTon != null){
            //计划总量
            dto.setAllPlanCount(allPlanTon);
        }else {
            dto.setAllPlanCount(new BigDecimal(0));
        }

        //计划剩余量
        if(dto.getWeighCount() != null && dto.getAllPlanCount() != null){
            //计划剩余量 = 计划总量-过磅量
            dto.setRemainPlanCount(dto.getAllPlanCount().subtract(dto.getWeighCount()));
        }else{
            //若过磅量为null 则等于计划总量
            dto.setRemainPlanCount(dto.getAllPlanCount());
        }
        return dto;
    }

    @SneakyThrows
    @Override
    public List<TDisPortDaynightplanDTO> importYesterdayPlan(String strPlanDate,String businessNo) {

        Date planDate = DateUtils.parseDate(strPlanDate, CommonEnum.DateFormatType.E_1.getCode());


        Date nightPlanStartTime = DateUtils.setHour(planDate, 18);

        Date nightPlanEndTime = DateUtils.setHour(DateUtils.addDays(nightPlanStartTime, 1), 8);


        Date mornPlanStartTime = DateUtils.setHour(DateUtils.addDays(nightPlanStartTime, 1), 8);

        Date mornPlanEndTime = DateUtils.setHour(DateUtils.addDays(nightPlanStartTime, 1), 18);


        /*Date planStartTime = DateUtils.setHour(planDate, 18);
        Date planEndTime = DateUtils.setHour(DateUtils.addDays(planStartTime, 1), 18);*/

        //查询传过来的日期的昼夜计划
/*        TDisPortDaynightplanSearch2DTO nowDate = new TDisPortDaynightplanSearch2DTO();
        nowDate.setPlanDate(strPlanDate);
        List<TDisPortDaynightplanDTO> nowList = tDisPortDaynightplanMapper.getList(nowDate);*/

        String strYeaterday = DateUtils.addDay(strPlanDate, -1);
        TDisPortDaynightplanSearch2DTO tDisPortDaynightplanSearch2DTO = new TDisPortDaynightplanSearch2DTO();
        tDisPortDaynightplanSearch2DTO.setPlanDate(strYeaterday);
//        tDisPortDaynightplanSearch2DTO.setPackingCode("1");
        tDisPortDaynightplanSearch2DTO.setBusinessNo(businessNo);
        //昨日计划数据
        List<TDisPortDaynightplanDTO> list = tDisPortDaynightplanMapper.getList(tDisPortDaynightplanSearch2DTO);


  /*      // 从 nowList 中获取班次和计划号，并将其拼接在一起作为键，值是计划号
        Map<String, String> classCodeAndBusinessNoToBusinessNoMap = nowList.stream()
                .collect(Collectors.toMap(
                        item -> item.getClassCode() + "_" + item.getBusinessNo(), // 自定义键的拼接方式
                        TDisPortDaynightplanPO::getBusinessNo,
                        (existingValue, newValue) -> existingValue // 合并函数，保留已存在的值
                ));

        // 过滤掉与 classCodeAndBusinessNoToBusinessNoMap 中键相同的项
        List<TDisPortDaynightplanDTO> filteredList = list.stream()
                .filter(item -> !classCodeAndBusinessNoToBusinessNoMap.containsKey(item.getClassCode() + "_" + item.getBusinessNo()))
                .collect(Collectors.toList());*/

         /*// 从 nowList 中获取计划号列表
        List<String> businessNoList = nowList.stream()
                .map(TDisPortDaynightplanPO::getBusinessNo)
                .collect(Collectors.toList());*/

        /*List<TDisPortDaynightplanDTO> filteredList = list.stream()
                .filter(item -> !businessNoList.contains(item.getBusinessNo())) // 过滤掉已经存在于 nowList 中的数据
                .collect(Collectors.toList());*/

/*
        list.stream().forEach(item -> {
            item.setId(snowflake.nextId());
            item.setStatus(STATUS_NOT_APPROVE);
            item.setPlanDate(planDate);
            if(item.getClassCode().equals("01")){
                item.setBeginTime(mornPlanStartTime);
                item.setEndTime(mornPlanEndTime);
            }else if(item.getClassCode().equals("02")){
                item.setBeginTime(nightPlanStartTime);
                item.setEndTime(nightPlanEndTime);
            } else if(item.getClassCode().equals("03")){
                item.setBeginTime(nightPlanStartTime);
                item.setEndTime(mornPlanEndTime);
            }
        });*/
        return list;
    }

}
