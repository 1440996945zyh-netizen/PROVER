package com.yy.ppm.produce.service.impl;

import cn.hutool.core.lang.Snowflake;
import com.yy.common.log.MicroLogger;
import com.yy.common.util.DateUtils;
import com.yy.common.util.SecurityUtils;
import com.yy.common.util.SpringContextUtils;
import com.yy.common.util.SpringUtils;
import com.yy.framework.concurrent.DistributedLock;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.appWork.mapper.TallyMapper;
import com.yy.ppm.business.mapper.TBusTrustMapper;
import com.yy.ppm.common.enums.DispatchEnum;
import com.yy.ppm.common.enums.DistributedLockKeyPrefixEnum;
import com.yy.ppm.common.enums.WorkTicketStatusEnum;
import com.yy.ppm.common.mapper.PublicMapper;
import com.yy.ppm.common.service.BusinessCommonService;
import com.yy.ppm.master.bean.dto.MWorkProcessDTO;
import com.yy.ppm.master.mapper.MWorkScheduleMapper;
import com.yy.ppm.produce.bean.dto.TPrdDispatchSecondaryDTO;
import com.yy.ppm.produce.bean.dto.workTicket.*;
import com.yy.ppm.produce.bean.po.*;
import com.yy.ppm.produce.mapper.TPrdTicketSecondAllotMapper;
import com.yy.ppm.produce.mapper.TPrdWorkPlanMapper;
import com.yy.ppm.produce.mapper.TPrdWorkTicketNewMapper;
import com.yy.ppm.produce.service.TPrdTicketSecondAllotService;
import com.yy.ppm.produce.service.TPrdWorkTicketService;
import com.yy.ppm.system.bean.dto.SysDeptDTO;
import com.yy.ppm.system.bean.dto.SysParameterDTO;
import com.yy.ppm.system.mapper.SysParameterMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.weaver.ast.Var;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Auther linqi
 * @Description
 * @Date 2023-08-14 15:37
 */
@Service
@Slf4j
public class TPrdTicketSecondAllotServiceImpl implements TPrdTicketSecondAllotService {
    private static final MicroLogger LOGGER = new MicroLogger(TPrdWorkTicketService.class);

    @Resource
    private TPrdTicketSecondAllotMapper ticketSecondAllotMapper;
    @Autowired
    private TPrdWorkTicketNewMapper ticketNewMapper;
    @Resource
    private TPrdWorkPlanMapper tPrdWorkPlanMapper;

    @Autowired
    private Snowflake snowflake;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Resource
    private SysParameterMapper sysParameterMapper;
    @Resource
    private TallyMapper tallyMapper;


    @Resource
    private SecurityUtils securityUtils;


    private static final int CURSOR_LIMIT = 5_000;

    private static final String DO_TICKET = "1";
    private static final String FOR_MACHINE = "2";//分配类型是机械
    private static final String FOR_LABOR = "3";//分配类型是人工
    private static final String DISP_FOR_MACHINE = "1";//分配类型是机械
    private static final String DISP_FOR_LABOR = "2";//分配类型是人工
    private static final String JSG_PLAN_TYPE = "2";
    @Autowired
    private SpringContextUtils springContextUtils;

    @Override
    public List<TPrdWorkTicketResDTO> listTicket(TPrdTicketSeconAllotQuery query) {
        List<TPrdWorkTicketResDTO> ticketList = ticketSecondAllotMapper.getTicketList(query);
        for (TPrdWorkTicketResDTO tmpResultDto : ticketList) {
            if(StringUtils.isNotBlank(tmpResultDto.getShipVoyageItemIds())){
                String[] split = tmpResultDto.getShipVoyageItemIds().split(",");
                if(split.length>0){
                    tmpResultDto.setBerthName(ticketSecondAllotMapper.getShipBerth(new ArrayList<>(Arrays.asList(split))));
                }
            }
        }
        //回显配工状态
        if (!ticketList.isEmpty()) {
            List<Long> idList = ticketList.stream().map(TPrdWorkTicketResDTO::getWorkPlanId).distinct().collect(Collectors.toList());
            if (!idList.isEmpty()){

                List<Map<String, Object>> flowStatusList = tPrdWorkPlanMapper.getFlowStatus(idList);
                List<Map<String, Object>> fixedStatusList = tPrdWorkPlanMapper.getFixedStatus(idList);
                List<Map<String, Object>> laborStatusList = tPrdWorkPlanMapper.getLaborStatus(idList);

                for (TPrdWorkTicketResDTO data : ticketList) {

                    for (Map<String, Object> flowStatus : flowStatusList) {
                        if (data.getWorkPlanId().toString().equals(flowStatus.get("id").toString())) {
                            data.setFlowStatus("1");
                            break;
                        }
                    }
                    for (Map<String, Object> fixedStatus : fixedStatusList) {
                        if (data.getWorkPlanId().toString().equals(fixedStatus.get("id").toString())) {
                            data.setFixedStatus("1");
                            break;
                        }
                    }
                    for (Map<String, Object> laborStatus : laborStatusList) {
                        if (data.getWorkPlanId().toString().equals(laborStatus.get("id").toString())) {
                            data.setLaborStatus("1");
                            break;
                        }
                    }
                }
            }
        }

        if(!ticketList.isEmpty()&&StringUtils.isNotBlank(query.getIsTicket())){
            ticketList = ticketList.stream().filter(o -> query.getIsTicket().equals(o.getAllotTypeLabel())).collect(Collectors.toList());
        }
        if(!ticketList.isEmpty()&&StringUtils.isNotBlank(query.getFlowStatus())){
            if("1".equals(query.getFlowStatus())){
                ticketList = ticketList.stream().filter(o -> query.getFlowStatus().equals(o.getFlowStatus())).collect(Collectors.toList());
            }else {
                ticketList = ticketList.stream().filter(o -> !"1".equals(o.getFlowStatus())).collect(Collectors.toList());
            }
        }
        if(!ticketList.isEmpty()&&StringUtils.isNotBlank(query.getLaborStatus())){
            if("1".equals(query.getLaborStatus())){
                ticketList = ticketList.stream().filter(o -> query.getLaborStatus().equals(o.getLaborStatus())).collect(Collectors.toList());
            }else{
                ticketList = ticketList.stream().filter(o -> !"1".equals(o.getLaborStatus())).collect(Collectors.toList());

            }
        }

        if ("2".equals(query.getPlanType())) {
            for (TPrdWorkTicketResDTO dto : ticketList) {
                if ("2".equals(dto.getPlanType())) {
                    //集疏港计划显示船名
                    List<Map<String, Object>> listSh = tallyMapper.getShipName(dto.getTrustId());
                    if (listSh != null && listSh.size() != 0) {
                        if (listSh.get(0).get("shipNameVoyage").toString() != null)
                            dto.setShipvoyageLabel(listSh.get(0).get("shipNameVoyage").toString());
                        if (listSh.get(0).get("scn") != null) {
                            dto.setScn(listSh.get(0).get("scn").toString());
                        }

                    }

                }
            }
        }
        if(!ticketList.isEmpty()&&StringUtils.isNotBlank(query.getShipName())){
            ticketList  = ticketList.stream().filter(o->StringUtils.isNotBlank(o.getShipvoyageLabel())&&o.getShipvoyageLabel().split("_")[0].contains(query.getShipName())).collect(Collectors.toList());
        }
        if(!ticketList.isEmpty()&&StringUtils.isNotBlank(query.getVoyage())){
            ticketList  = ticketList.stream().filter(o->StringUtils.isNotBlank(o.getShipvoyageLabel())&&o.getShipvoyageLabel().split("_")[1].contains(query.getVoyage())).collect(Collectors.toList());
        }

        return ticketList.stream().sorted(Comparator.comparing(o->o.getTrustNo()+o.getShipvoyageLabel())).collect(Collectors.toList());
    }

    /**
     * 签票详情查询为机械签票做数据源 按票货分配 没加位置
     * @param query
     * @return
     */
    @Override
    public List<TPrdWorkTicketDetailDTO> listDetailForAllot(TPrdTicketSeconAllotQuery query) {
        if(!FOR_MACHINE.equals(query.getAllotType())&&!FOR_LABOR.equals(query.getAllotType())){
            throw new BusinessRuntimeException("分配参数错误");
        }

        List<TPrdWorkTicketDetailDTO> result = new ArrayList<>();
        //查询签票是否已经分配
        result = ticketSecondAllotMapper.getTicketDetailList(query);
        if(!result.isEmpty()){
            if(FOR_MACHINE.equals(query.getAllotType())){

                //查询派机信息
                List<TPrdWorkTicketEquipmentPO> equipmentInfoByTickets =
                        ticketSecondAllotMapper.getEquipmentInfoByTicket(
                                result.stream().map(TPrdWorkTicketDetailDTO::getId).collect(Collectors.toList())
                        );
                Map<Long, List<TPrdWorkTicketEquipmentPO>> tmpMachine = equipmentInfoByTickets.stream().collect(Collectors.groupingBy(TPrdWorkTicketEquipmentPO::getWorkTicketDetailId));
                //回写派派机信息
                result.forEach(o->{
                    if(tmpMachine.get(o.getId())!=null && (!tmpMachine.get(o.getId()).isEmpty())){
                        List<TPrdWorkTicketEquipmentPO> tPrdWorkTicketEquipmentPOS = tmpMachine.get(o.getId());
                        o.setEquipments(tPrdWorkTicketEquipmentPOS);
                        o.setEquipmentTypeCode(tPrdWorkTicketEquipmentPOS.stream().map(TPrdWorkTicketEquipmentPO::getEquipmentTypeCode).distinct().collect(Collectors.joining(",")));
                        o.setEquipmentTypeName(tPrdWorkTicketEquipmentPOS.stream().map(TPrdWorkTicketEquipmentPO::getEquipmentTypeName).distinct().collect(Collectors.joining(",")));
                        o.setEquipmentNo(tPrdWorkTicketEquipmentPOS.stream().map(TPrdWorkTicketEquipmentPO::getEquipmentNo).distinct().collect(Collectors.joining(",")));
                        o.setEquipmentId(tPrdWorkTicketEquipmentPOS.stream().map(tmp->{return tmp.getEquipmentId().toString();}).distinct().collect(Collectors.joining(",")));
                    }
                });
            }else if(FOR_LABOR.equals(query.getAllotType())){
                TPrdTicketSeconAllotQuery tmpQuery = new TPrdTicketSeconAllotQuery();
                tmpQuery.setDispatchType(DISP_FOR_LABOR);
                tmpQuery.setWorkPlanId(query.getWorkPlanId());
                List<TPrdDispatchSecondaryDTO> tmpLaborSecDispInfo = ticketSecondAllotMapper.getSecondDisptchInfo(tmpQuery);
                if(tmpLaborSecDispInfo.isEmpty()){
                    throw new BusinessRuntimeException("未进行二次配工，请先配工在进行分配");
                }
                //装卸队派工部门必填，根据部门进行分配作业量，下面这段赋值部门信息的代码主要是为了方便和机械的分配进行对比
                if(!tmpLaborSecDispInfo.isEmpty()){
                    tmpLaborSecDispInfo.forEach(o->{
                        if(o.getDeptParentId()==null){
                            throw new BusinessRuntimeException("装卸队二次配工中缺少部门信息");
                        }
                        o.setTmpDeptId(o.getDeptId());
                        o.setTmpDeptName(o.getDeptName());
                        o.setDeptId(o.getDeptParentId());
                        o.setDeptName(o.getDeptParentName());
                    });
                    Map<Long, List<TPrdDispatchSecondaryDTO>> laborMap = tmpLaborSecDispInfo.stream()
                            .collect(Collectors.groupingBy(TPrdDispatchSecondaryDTO::getDeptId));
                    result.forEach(o->{
                        List<TPrdDispatchSecondaryDTO> tPrdDispatchSecondaryDTOS = laborMap.get(o.getDeptId());
                        if(tPrdDispatchSecondaryDTOS!=null&&!tPrdDispatchSecondaryDTOS.isEmpty()){
                            o.setLaborNumber(tPrdDispatchSecondaryDTOS.stream().map(tmpSec->new BigDecimal(Optional.ofNullable(tmpSec.getNumberCount()).orElse(new Long(0)))).reduce(BigDecimal.ZERO,BigDecimal::add));
                        }
                    });
                }

            }
            //返回签票数据
            return result;
        }

        //未分配 获取当前签票下的除固机队外的数据
        TPrdTicketSeconAllotQuery tmpQuery = new TPrdTicketSeconAllotQuery();
        tmpQuery.setWorkPlanId(query.getWorkPlanId());
        tmpQuery.setId(query.getId());
        tmpQuery.setAllotType("1");
        tmpQuery.setNoGj("1");//二次分配固机队不参与
        result = ticketSecondAllotMapper.getTicketDetailList(tmpQuery);
        List<TPrdWorkTicketDetailDTO> resultWithoutTicket = new ArrayList<>();
        // 一票货一分配  每种机械类型都是独享总量
        if(result.isEmpty()){
            throw new BusinessRuntimeException("没有签票数据");
        }

            //当前要分配的作业票
//            TPrdWorkTicketDetailDTO curTickt = SerializationUtils.clone(tPrdWorkTicketDetailDTO);
            if(FOR_MACHINE.equals(query.getAllotType())){

                //获取二次配工中的机械分配情况
                //获取单类型分配量
                tmpQuery.setDispatchType(DISP_FOR_MACHINE);
                tmpQuery.setWorkPlanId(query.getWorkPlanId());
                List<TPrdDispatchSecondaryDTO> tmpSecDispInfo = ticketSecondAllotMapper.getSecondDisptchInfo(tmpQuery);
                //为数组增量做准备
                Map<String, List<TPrdDispatchSecondaryDTO>> tmpMachineMap = tmpSecDispInfo.stream().collect(Collectors.groupingBy(o->o.getDeptId().toString()+"_"+o.getEquipmentTypeId().toString()+"_"+o.getSubProcessCode()));
                //为计算机械单价做准备
                Map<String, List<TPrdDispatchSecondaryDTO>> machineTypeMap = tmpSecDispInfo.stream().collect(Collectors.groupingBy(o->o.getEquipmentTypeId()+"_"+o.getSubProcessCode()));
                HashMap<String, BigDecimal> singleMachine = new HashMap<>();

                for (TPrdWorkTicketDetailDTO curTickt : result) {
                    curTickt.setId(null);
                    for (String tmpId : machineTypeMap.keySet()) {
                        List<TPrdDispatchSecondaryDTO> tPrdDispatchSecondaryDTOS = machineTypeMap.get(tmpId);
                        if (!tPrdDispatchSecondaryDTOS.isEmpty()) {
                            singleMachine.put(tmpId,
                                    Optional.ofNullable(curTickt.getTon()).orElse(BigDecimal.ZERO).divide(Optional.ofNullable(BigDecimal.valueOf(tPrdDispatchSecondaryDTOS.size())).orElse(BigDecimal.ZERO), 2, BigDecimal.ROUND_HALF_UP));
                        }
                    }

                    //获取机械基本信息 拼接回显机械编号用
                    for (String tmpKey : tmpMachineMap.keySet()) {
                        List<TPrdDispatchSecondaryDTO> v = tmpMachineMap.get(tmpKey);
                        if (v.isEmpty()) {
                            //正常情况
                        } else {
                            TPrdWorkTicketDetailDTO tmpResDto = SerializationUtils.clone(curTickt);
                            //回写前端派机信息 供后端新增使用
                            List<TPrdWorkTicketEquipmentPO> tPrdWorkTicketEquipmentPOS = new ArrayList<>();
                            for (TPrdDispatchSecondaryDTO tmpForDto : v) {
                                TPrdWorkTicketEquipmentPO tPrdWorkTicketEquipmentPO = new TPrdWorkTicketEquipmentPO();
                                tPrdWorkTicketEquipmentPO.setEquipmentId(tmpForDto.getEquipmentId());
                                tPrdWorkTicketEquipmentPO.setEquipmentNo(tmpForDto.getEquipmentNo());
                                tPrdWorkTicketEquipmentPO.setEquipmentTypeCode(tmpForDto.getEquipmentTypeCode());
                                tPrdWorkTicketEquipmentPO.setEquipmentTypeName(tmpForDto.getEquipmentTypeName());
                                tPrdWorkTicketEquipmentPOS.add(tPrdWorkTicketEquipmentPO);
                            }
                            //回写前端派机信息
                            tmpResDto.setEquipmentNo(tPrdWorkTicketEquipmentPOS.stream().map(TPrdWorkTicketEquipmentPO::getEquipmentNo).distinct().collect(Collectors.joining(",")));
                            tmpResDto.setEquipmentId(tPrdWorkTicketEquipmentPOS.stream().map(o -> o.getEquipmentId().toString()).distinct().collect(Collectors.joining(",")));
                            tmpResDto.setEquipmentTypeCode(tPrdWorkTicketEquipmentPOS.stream().map(TPrdWorkTicketEquipmentPO::getEquipmentTypeCode).distinct().collect(Collectors.joining(",")));
                            tmpResDto.setEquipmentTypeName(tPrdWorkTicketEquipmentPOS.stream().map(TPrdWorkTicketEquipmentPO::getEquipmentTypeName).distinct().collect(Collectors.joining(",")));
                            tmpResDto.setEquipments(tPrdWorkTicketEquipmentPOS);
                            tmpResDto.setDeptId(v.get(0).getDeptId());
                            tmpResDto.setDeptName(v.get(0).getDeptName());
                            tmpResDto.setProcessDetailCode(v.get(0).getSubProcessCode());
                            tmpResDto.setProcessDetailName(v.get(0).getSubProcessName());
                            //分配量
                            tmpResDto.setTon(BigDecimal.valueOf(v.size()).multiply(Optional.ofNullable(singleMachine.get(v.get(0).getEquipmentTypeId()+"_"+v.get(0).getSubProcessCode())).orElse(BigDecimal.ZERO)).setScale(2,BigDecimal.ROUND_HALF_UP));

                            resultWithoutTicket.add(tmpResDto);
                        }
                    }
                }
            }else if(FOR_LABOR.equals(query.getAllotType())){
                tmpQuery.setDispatchType(DISP_FOR_LABOR);
                tmpQuery.setWorkPlanId(query.getWorkPlanId());
                List<TPrdDispatchSecondaryDTO> tmpLaborSecDispInfo = ticketSecondAllotMapper.getSecondDisptchInfo(tmpQuery);
                if (tmpLaborSecDispInfo.isEmpty()){
                    throw new BusinessRuntimeException("没有人员配工信息");
                }
                tmpLaborSecDispInfo.forEach(o->{
                    if(o.getDeptParentId()==null){
                        throw new BusinessRuntimeException("装卸队二次配工中缺少部门信息");
                    }
                    o.setTmpDeptId(o.getDeptId());
                    o.setTmpDeptName(o.getDeptName());
                    o.setDeptId(o.getDeptParentId());
                    o.setDeptName(o.getDeptParentName());
                });
                if(tmpLaborSecDispInfo.isEmpty()){

                }else {
                    // 结算单人作业单价
                    //拆分每个部分派工多少人，为集合增量做准备，为计算吨数做准备
                    Map<String, List<TPrdDispatchSecondaryDTO>> laborMap = tmpLaborSecDispInfo.stream()
                            .collect(Collectors.groupingBy(o->o.getDeptId()+"_"+o.getSubProcessCode()));

                    //分配签票
                    for (TPrdWorkTicketDetailDTO curTickt : result) {
                        curTickt.setId(null);
                        //计算单价
                        BigDecimal signleLabor = Optional.ofNullable(curTickt.getTon()).orElse(BigDecimal.ZERO).divide( tmpLaborSecDispInfo.stream().map(p->BigDecimal.valueOf(Optional.ofNullable(p.getNumberCount()).orElseThrow(()->new BusinessRuntimeException("没有分配人数")))).collect(Collectors.toList()).stream().reduce(BigDecimal.ZERO,BigDecimal::add));

                        for (String tmpId : laborMap.keySet()) {
                        List<TPrdDispatchSecondaryDTO> tmpLabor = laborMap.get(tmpId);

                        if(tmpLabor.isEmpty()){

                        }else{
                            TPrdWorkTicketDetailDTO tmpResDto = SerializationUtils.clone(curTickt);
                            tmpResDto.setDeptName(tmpLabor.get(0).getDeptName());
                            tmpResDto.setDeptId(tmpLabor.get(0).getDeptId());
                            tmpResDto.setProcessDetailName(tmpLabor.get(0).getSubProcessName());
                            tmpResDto.setProcessDetailCode(tmpLabor.get(0).getSubProcessCode());
                            tmpResDto.setTon(signleLabor.multiply(
                                    tmpLabor.stream().map(tmpLaborItem->Optional.ofNullable(new BigDecimal(tmpLaborItem.getNumberCount())).orElse(BigDecimal.ZERO)).reduce(BigDecimal.ZERO,BigDecimal::add)
                            ).setScale(2, BigDecimal.ROUND_HALF_UP));
                            tmpResDto.setLaborNumber(
                                    tmpLabor.stream().map(tmpLaborItem->Optional.ofNullable(new BigDecimal(tmpLaborItem.getNumberCount())).orElse(BigDecimal.ZERO)).reduce(BigDecimal.ZERO,BigDecimal::add)
                            );
                            resultWithoutTicket.add(tmpResDto);
                        }
                    }
                }
            }
        }
        return resultWithoutTicket;
    }
    /**
     * 签票详情查询为机械签票做数据源 不分票货直接分配总量
     * @param query
     * @return
     */
    @Override
    public List<TPrdWorkTicketDetailDTO> listDetailForAllotNoCargo(TPrdTicketSeconAllotQuery query) {
        if(!FOR_MACHINE.equals(query.getAllotType())&&!FOR_LABOR.equals(query.getAllotType())){
            throw new BusinessRuntimeException("分配参数错误");
        }

        List<TPrdWorkTicketDetailDTO> result = new ArrayList<>();
        //查询签票是否已经分配
        result = ticketSecondAllotMapper.getTicketDetailList(query);
        if(!result.isEmpty()){
            if(FOR_MACHINE.equals(query.getAllotType())){

                //查询派机信息
                List<TPrdWorkTicketEquipmentPO> equipmentInfoByTickets =
                        ticketSecondAllotMapper.getEquipmentInfoByTicket(
                                result.stream().map(TPrdWorkTicketDetailDTO::getId).collect(Collectors.toList())
                        );
                Map<Long, List<TPrdWorkTicketEquipmentPO>> tmpMachine = equipmentInfoByTickets.stream().collect(Collectors.groupingBy(TPrdWorkTicketEquipmentPO::getWorkTicketDetailId));
                //回写派派机信息
                result.forEach(o->{
                    if(tmpMachine.get(o.getId())!=null && (!tmpMachine.get(o.getId()).isEmpty())){
                        List<TPrdWorkTicketEquipmentPO> tPrdWorkTicketEquipmentPOS = tmpMachine.get(o.getId());
                        o.setEquipments(tPrdWorkTicketEquipmentPOS);
                        o.setEquipmentTypeCode(tPrdWorkTicketEquipmentPOS.stream().map(TPrdWorkTicketEquipmentPO::getEquipmentTypeCode).distinct().collect(Collectors.joining(",")));
                        o.setEquipmentTypeName(tPrdWorkTicketEquipmentPOS.stream().map(TPrdWorkTicketEquipmentPO::getEquipmentTypeName).distinct().collect(Collectors.joining(",")));
                        o.setEquipmentNo(tPrdWorkTicketEquipmentPOS.stream().map(TPrdWorkTicketEquipmentPO::getEquipmentNo).distinct().collect(Collectors.joining(",")));
                        o.setEquipmentId(tPrdWorkTicketEquipmentPOS.stream().map(tmp->{return tmp.getEquipmentId().toString();}).distinct().collect(Collectors.joining(",")));
                    }
                });
            }else if(FOR_LABOR.equals(query.getAllotType())){
                TPrdTicketSeconAllotQuery tmpQuery = new TPrdTicketSeconAllotQuery();
                tmpQuery.setDispatchType(DISP_FOR_LABOR);
                tmpQuery.setWorkPlanId(query.getWorkPlanId());
                List<TPrdDispatchSecondaryDTO> tmpLaborSecDispInfo = ticketSecondAllotMapper.getSecondDisptchInfo(tmpQuery);
                if(tmpLaborSecDispInfo.isEmpty()){
                    throw new BusinessRuntimeException("未进行二次配工，请先配工在进行分配");
                }
                //装卸队派工部门必填，根据部门进行分配作业量，下面这段赋值部门信息的代码主要是为了方便和机械的分配进行对比
                if(!tmpLaborSecDispInfo.isEmpty()){
                    tmpLaborSecDispInfo.forEach(o->{
                        if(o.getDeptParentId()==null){
                            throw new BusinessRuntimeException("装卸队二次配工中缺少部门信息");
                        }
                        o.setTmpDeptId(o.getDeptId());
                        o.setTmpDeptName(o.getDeptName());
                        o.setDeptId(o.getDeptParentId());
                        o.setDeptName(o.getDeptParentName());
                    });
                    Map<Long, List<TPrdDispatchSecondaryDTO>> laborMap = tmpLaborSecDispInfo.stream()
                            .collect(Collectors.groupingBy(TPrdDispatchSecondaryDTO::getDeptId));
                    result.forEach(o->{
                        List<TPrdDispatchSecondaryDTO> tPrdDispatchSecondaryDTOS = laborMap.get(o.getDeptId());
                        if(tPrdDispatchSecondaryDTOS!=null&&!tPrdDispatchSecondaryDTOS.isEmpty()){
                            o.setLaborNumber(tPrdDispatchSecondaryDTOS.stream().map(tmpSec->new BigDecimal(Optional.ofNullable(tmpSec.getNumberCount()).orElse(new Long(0)))).reduce(BigDecimal.ZERO,BigDecimal::add));
                        }
                    });
                }

            }
            //返回签票数据
            return result.stream().sorted(Comparator.comparing(o->o.getProcessDetailCode()+"_"+o.getWorkPositionCode()+"_"+o.getDeptId()+"_"+o.getEquipmentTypeCode())).collect(Collectors.toList());
        }

        //未分配 获取当前签票下的除固机队外的数据
        TPrdTicketSeconAllotQuery tmpQuery = new TPrdTicketSeconAllotQuery();
        tmpQuery.setWorkPlanId(query.getWorkPlanId());
        tmpQuery.setId(query.getId());
        tmpQuery.setAllotType("1");
        tmpQuery.setNoGj("1");//二次分配固机队不参与
        result = ticketSecondAllotMapper.getTicketDetailList(tmpQuery);
        List<TPrdWorkTicketDetailDTO> resultWithoutTicket = new ArrayList<>();
        // 一票货一分配  每种机械类型都是独享总量
        if(result.isEmpty()){
            throw new BusinessRuntimeException("没有签票数据");
        }
        BigDecimal allTon = result.stream().map(o->Optional.ofNullable(o.getTon()).orElseThrow(()->new BusinessRuntimeException("获取签票量失败"))).reduce(BigDecimal.ZERO, BigDecimal::add);
        //当前要分配的作业票
//            TPrdWorkTicketDetailDTO curTickt = SerializationUtils.clone(tPrdWorkTicketDetailDTO);
        /**
         * 特殊处理的
         * 1.计划类型是集疏港或者作业过程是直取的签票采用根据理货数据分配模式
         * 2.根据计划ID查找理货记录表
         * 3.分别取出理货记录表中的作业机械以及装卸部门，根据作业部门及作业过程、机械类型进行分组
         * 4.下舱及打高还是需要根据派工进行分配。
         */
        //获取作业计划信息
        TPrdWorkPlanPO workPlan = ticketNewMapper.getWorkPlan(query.getWorkPlanId());
        MWorkProcessDTO processInfo = ticketNewMapper.getProcessInfo(workPlan.getProcessCode());
        if(JSG_PLAN_TYPE.equals(workPlan.getPlanType()) || "1".equals(processInfo.getIsDirectAccess())){
            List<TPrdWorkTicketDetailDTO> specialList = ticketSecondAllotMapper.getTallListForAllot(workPlan.getId());
            List<TPrdWorkTicketDetailDTO> specilaResult = new ArrayList<TPrdWorkTicketDetailDTO>();
            List<TPrdWorkTicketDetailDTO> tmpResult = new ArrayList<>();
            if(FOR_MACHINE.equals(query.getAllotType())){
                //处理机械的信息，为了合并转运机械和普通的作业机械
                List<TPrdWorkTicketDetailDTO> tmpMachineList = specialList.stream().filter(o -> o.getEquipmentId() != null).collect(Collectors.toList());
                tmpMachineList.forEach(o->{
                    o.setDeptId(o.getMachineDeptId());
                    o.setDeptName(o.getMachineDeptName());
                });
                List<TPrdWorkTicketDetailDTO> tmpMachineTranList = specialList.stream().filter(o ->( o.getTransportEquipmentNo() != null) && ( o.getTransportEquipmentTypeCode() !=null)).collect(Collectors.toList());
                tmpMachineTranList.forEach(o->{
                    o.setEquipmentTypeCode(o.getTransportEquipmentTypeCode());
                    o.setEquipmentTypeName(o.getTransportEquipmentTypeName());
                    o.setEquipmentId(o.getTransportEquipmentId()==null?null:o.getTransportEquipmentId().toString());
                    o.setEquipmentNo(o.getTransportEquipmentNo());
                    o.setDeptId(o.getTransportDeptId());
                    o.setDeptName(o.getTransportDeptName());
                });
                //合并
                tmpMachineList.addAll(tmpMachineTranList);
                if(tmpMachineList.isEmpty()){

                }else {
                    tmpMachineList = tmpMachineList.stream().filter(tmpMachine -> !"固机队".equals(tmpMachine.getDeptName())).collect(Collectors.toList());
                    if(tmpMachineList.isEmpty()){

                    }else {
                        tmpMachineList = tmpMachineList.stream().filter(o -> !"999999999".equals(o.getEquipmentId())).collect(Collectors.toList());
                        if(tmpMachineList.isEmpty()){

                        }else {
                            Map<String, List<TPrdWorkTicketDetailDTO>> machineTallyMap = tmpMachineList.stream().collect(Collectors.groupingBy(o -> o.getProcessDetailCode() + "_" +o.getDeptId()+"_"+o.getEquipmentTypeCode()));

                            //回显机械信息 为后面插入作业票做数据准备
                            for (String s : machineTallyMap.keySet()) {
                                List<TPrdWorkTicketDetailDTO> workTicketDetailDTOS = machineTallyMap.get(s);
                                if(workTicketDetailDTOS.isEmpty()){
                                    continue;
                                }
                                TPrdWorkTicketDetailDTO tmpResDto = new TPrdWorkTicketDetailDTO();
                                List<TPrdWorkTicketEquipmentPO> tmpRowMachineList = new ArrayList<>();
                                workTicketDetailDTOS.forEach(curTick->{
                                    TPrdWorkTicketEquipmentPO tPrdWorkTicketEquipmentPO = new TPrdWorkTicketEquipmentPO();
                                    tPrdWorkTicketEquipmentPO.setEquipmentId(new Long(curTick.getEquipmentId()));
                                    tPrdWorkTicketEquipmentPO.setEquipmentNo(curTick.getEquipmentNo());
                                    tPrdWorkTicketEquipmentPO.setEquipmentTypeCode(curTick.getEquipmentTypeCode());
                                    tPrdWorkTicketEquipmentPO.setEquipmentTypeName(curTick.getEquipmentTypeName());
                                    tmpRowMachineList.add(tPrdWorkTicketEquipmentPO);
                                });
                                tmpResDto.setEquipments(tmpRowMachineList);
                                tmpResDto.setEquipmentNo(tmpRowMachineList.stream().map(TPrdWorkTicketEquipmentPO::getEquipmentNo).distinct().collect(Collectors.joining(",")));
                                tmpResDto.setEquipmentId(tmpRowMachineList.stream().map(o -> o.getEquipmentId().toString()).distinct().collect(Collectors.joining(",")));
                                tmpResDto.setEquipmentTypeCode(tmpRowMachineList.stream().map(TPrdWorkTicketEquipmentPO::getEquipmentTypeCode).distinct().collect(Collectors.joining(",")));
                                tmpResDto.setEquipmentTypeName(tmpRowMachineList.stream().map(TPrdWorkTicketEquipmentPO::getEquipmentTypeName).distinct().collect(Collectors.joining(",")));
                                //作业票列表展示的信息
                                tmpResDto.setDeptId(workTicketDetailDTOS.stream().findFirst().orElseThrow(()->new BusinessRuntimeException("自动分配获取理货，获取部门信息失败")).getDeptId());
                                tmpResDto.setDeptName(workTicketDetailDTOS.stream().findFirst().orElseThrow(()->new BusinessRuntimeException("自动分配获取理货，获取部门信息失败")).getDeptName());
                                tmpResDto.setWorkTicketId(result.stream().findFirst().orElseThrow(()->new BusinessRuntimeException("获取签票信息失败")).getWorkTicketId());
                                tmpResDto.setEquipmentTypeCode(workTicketDetailDTOS.stream().findFirst().orElseThrow(()->new BusinessRuntimeException("自动分配理货，获取机械类型信息失败")).getEquipmentTypeCode());
                                tmpResDto.setEquipmentTypeName(workTicketDetailDTOS.stream().findFirst().orElseThrow(()->new BusinessRuntimeException("自动分配理货，获取机械类型信息失败")).getEquipmentTypeName());
                                tmpResDto.setProcessDetailCode(workTicketDetailDTOS.get(0).getProcessDetailCode());
                                tmpResDto.setProcessDetailName(workTicketDetailDTOS.get(0).getProcessDetailName());
                                tmpResDto.setProcessCode(workPlan.getProcessCode());
                                tmpResDto.setProcessName(workPlan.getProcessName());
                                tmpResDto.setTon(workTicketDetailDTOS.stream().map(o->Optional.ofNullable(o.getTon()).orElseThrow(()->new BusinessRuntimeException("自动分配理货，获取理货重量失败"))).reduce(BigDecimal.ZERO,BigDecimal::add));
                                tmpResult.add(tmpResDto);
                            }
                        }

                    }
                    }

                //获取二次配工信息处理下仓作业过程
                tmpQuery.setDispatchType(DISP_FOR_MACHINE);
                tmpQuery.setWorkPlanId(query.getWorkPlanId());
                List<TPrdDispatchSecondaryDTO> tmpSecDispInfo = ticketSecondAllotMapper.getSecondDisptchInfo(tmpQuery);
                tmpSecDispInfo = tmpSecDispInfo.stream().filter(o -> "0".equals(o.getIsTallyCourse())).collect(Collectors.toList());
                //获取签票中的辅助作业过程
                if(!tmpSecDispInfo.isEmpty()){
                    //为数组增量做准备
                    Map<String, List<TPrdDispatchSecondaryDTO>> tmpMachineMap = tmpSecDispInfo.stream().collect(Collectors.groupingBy(o->o.getDeptId().toString()+"_"+o.getSubProcessCode()+"_"+o.getWorkPositionName()+"_"+o.getEquipmentTypeCode()));
                    //为计算机械单价做准备
                    Map<String, List<TPrdDispatchSecondaryDTO>> machineTypeMap = tmpSecDispInfo.stream().collect(Collectors.groupingBy(
                            o->o.getSubProcessCode()+"_"+o.getWorkPositionCode()));
                    HashMap<String, BigDecimal> singleMachine = new HashMap<>();
                    for (String tmpId : machineTypeMap.keySet()) {
                        List<TPrdDispatchSecondaryDTO> tPrdDispatchSecondaryDTOS = machineTypeMap.get(tmpId);
                        if (!tPrdDispatchSecondaryDTOS.isEmpty()) {
                            singleMachine.put(tmpId,
                                    allTon.divide(Optional.ofNullable(BigDecimal.valueOf(tPrdDispatchSecondaryDTOS.size())).orElse(BigDecimal.ZERO), 2, BigDecimal.ROUND_HALF_UP));
                        }
                    }
                    for (String tmpKey : tmpMachineMap.keySet()) {
                        List<TPrdDispatchSecondaryDTO> v = tmpMachineMap.get(tmpKey);
                        if (v.isEmpty()) {
                            //正常情况
                        } else {
                            TPrdWorkTicketDetailDTO tmpResDto = SerializationUtils.clone(result.get(0));
                            tmpResDto.setCargoInfoId(null);
                            //回写前端派机信息 供后端新增使用
                            List<TPrdWorkTicketEquipmentPO> tPrdWorkTicketEquipmentPOS = new ArrayList<>();
                            for (TPrdDispatchSecondaryDTO tmpForDto : v) {
                                TPrdWorkTicketEquipmentPO tPrdWorkTicketEquipmentPO = new TPrdWorkTicketEquipmentPO();
                                tPrdWorkTicketEquipmentPO.setEquipmentId(tmpForDto.getEquipmentId());
                                tPrdWorkTicketEquipmentPO.setEquipmentNo(tmpForDto.getEquipmentNo());
                                tPrdWorkTicketEquipmentPO.setEquipmentTypeCode(tmpForDto.getEquipmentTypeCode());
                                tPrdWorkTicketEquipmentPO.setEquipmentTypeName(tmpForDto.getEquipmentTypeName());
                                tPrdWorkTicketEquipmentPOS.add(tPrdWorkTicketEquipmentPO);
                            }
                            //回写前端派机信息
                            tmpResDto.setEquipmentNo(tPrdWorkTicketEquipmentPOS.stream().map(TPrdWorkTicketEquipmentPO::getEquipmentNo).distinct().collect(Collectors.joining(",")));
                            tmpResDto.setEquipmentId(tPrdWorkTicketEquipmentPOS.stream().map(o -> o.getEquipmentId().toString()).distinct().collect(Collectors.joining(",")));
                            tmpResDto.setEquipmentTypeCode(tPrdWorkTicketEquipmentPOS.stream().map(TPrdWorkTicketEquipmentPO::getEquipmentTypeCode).distinct().collect(Collectors.joining(",")));
                            tmpResDto.setEquipmentTypeName(tPrdWorkTicketEquipmentPOS.stream().map(TPrdWorkTicketEquipmentPO::getEquipmentTypeName).distinct().collect(Collectors.joining(",")));
                            tmpResDto.setEquipments(tPrdWorkTicketEquipmentPOS);
                            tmpResDto.setDeptId(v.get(0).getDeptId());
                            tmpResDto.setDeptName(v.get(0).getDeptName());
                            tmpResDto.setProcessDetailCode(v.get(0).getSubProcessCode());
                            tmpResDto.setProcessDetailName(v.get(0).getSubProcessName());
                            tmpResDto.setWorkPositionName(v.get(0).getWorkPositionName());
                            tmpResDto.setWorkPositionCode(v.get(0).getWorkPositionCode());
                            //分配量
                            tmpResDto.setTon(BigDecimal.valueOf(v.size()).multiply(
                                    Optional.ofNullable(singleMachine.get(v.get(0).getSubProcessCode()+"_"+v.get(0).getWorkPositionCode())).orElse(BigDecimal.ZERO)).setScale(2,BigDecimal.ROUND_HALF_UP));
                            tmpResult.add(tmpResDto);
                        }
                    }
                }
                specilaResult = tmpResult;
            }
            else if(FOR_LABOR.equals(query.getAllotType())){
                //获取签票中人员信息
                List<TPrdWorkTicketDetailDTO> laborTallyInfo = specialList.stream().filter(o -> o.getDeptId() != null).collect(Collectors.toList());
                //过滤固机队
                laborTallyInfo = laborTallyInfo.stream().filter(o -> !"固机队".equals(o.getDeptName())).collect(Collectors.toList());
                if(laborTallyInfo.isEmpty()){

                }else{
                    //过滤无装卸部门
                    laborTallyInfo = laborTallyInfo.stream().filter(o -> !new Long(999999999).equals(o.getDeptId())).collect(Collectors.toList());
                    if(laborTallyInfo.isEmpty()){

                    }else {

                        Map<String, List<TPrdWorkTicketDetailDTO>> resultMap = laborTallyInfo.stream().collect(Collectors.groupingBy(o -> o.getProcessDetailCode() + "_" + o.getDeptId()));
                        for (String s : resultMap.keySet()) {
                            List<TPrdWorkTicketDetailDTO> workTicketDetailDTOS = resultMap.get(s);
                            if(workTicketDetailDTOS.isEmpty()){
                                continue;
                            }
                            TPrdWorkTicketDetailDTO tmpResDto = new TPrdWorkTicketDetailDTO();
                            tmpResDto.setDeptId(workTicketDetailDTOS.get(0).getDeptId());
                            tmpResDto.setDeptName(workTicketDetailDTOS.get(0).getDeptName());
                            tmpResDto.setProcessName(workPlan.getProcessName());
                            tmpResDto.setProcessCode(workPlan.getProcessCode());
                            tmpResDto.setProcessDetailCode(workTicketDetailDTOS.get(0).getProcessDetailCode());
                            tmpResDto.setProcessDetailName(workTicketDetailDTOS.get(0).getProcessDetailName());
                            tmpResDto.setTon(workTicketDetailDTOS.stream().map(o->Optional.ofNullable(o.getTon()).orElseThrow(()->new BusinessRuntimeException("获取理货量失败"))).reduce(BigDecimal.ZERO,BigDecimal::add));
                            specilaResult.add(tmpResDto);
                        }
                    }
                }

                //判断是否存在辅助作业过程
                tmpQuery.setDispatchType(DISP_FOR_LABOR);
                tmpQuery.setWorkPlanId(query.getWorkPlanId());
                List<TPrdDispatchSecondaryDTO> tmpLaborSecDispInfo = ticketSecondAllotMapper.getSecondDisptchInfo(tmpQuery);
                tmpLaborSecDispInfo = tmpLaborSecDispInfo.stream().filter(o -> "0".equals(o.getIsTallyCourse())).collect(Collectors.toList());
                if (!tmpLaborSecDispInfo.isEmpty()) {

                    // 结算单人作业单价
                    //拆分每个部分派工多少人，为集合增量做准备，为计算吨数做准备
                    Map<String, List<TPrdDispatchSecondaryDTO>> laborMap = tmpLaborSecDispInfo.stream()
                            .collect(Collectors.groupingBy(o->o.getDeptId()+"_"+o.getSubProcessCode()+"_"+o.getWorkPositionName()));
                    Map<String, List<TPrdDispatchSecondaryDTO>> tmpSingleMap = tmpLaborSecDispInfo.stream().collect(Collectors.groupingBy(o -> o.getSubProcessCode()+"_"+o.getWorkPositionName()));

                    //分配签票
                    for (String tmpId : laborMap.keySet()) {
                        List<TPrdDispatchSecondaryDTO> tmpLabor = laborMap.get(tmpId);
                        List<TPrdDispatchSecondaryDTO> tPrdDispatchSecondaryDTOS = tmpSingleMap.get(tmpLabor.get(0).getSubProcessCode()+"_"+tmpLabor.get(0).getWorkPositionName());
                        BigDecimal signleLabor = allTon.divide(tPrdDispatchSecondaryDTOS.stream().map(o->new BigDecimal(o.getNumberCount())).reduce(BigDecimal.ZERO,BigDecimal::add), 2, BigDecimal.ROUND_HALF_UP);

                        if(tmpLabor.isEmpty()){

                        }else{

                            TPrdWorkTicketDetailDTO tmpResDto = SerializationUtils.clone(result.get(0));
                            tmpResDto.setCargoInfoId(null);

                            tmpResDto.setDeptName(tmpLabor.get(0).getDeptParentName());
                            tmpResDto.setDeptId(tmpLabor.get(0).getDeptParentId());
                            tmpResDto.setProcessDetailName(tmpLabor.get(0).getSubProcessName());
                            tmpResDto.setProcessDetailCode(tmpLabor.get(0).getSubProcessCode());
                            tmpResDto.setWorkPositionName(tmpLabor.get(0).getWorkPositionName());
                            tmpResDto.setWorkPositionCode(tmpLabor.get(0).getWorkPositionCode());
                            tmpResDto.setTon(signleLabor.multiply(
                                    tmpLabor.stream().map(tmpLaborItem->Optional.ofNullable(new BigDecimal(tmpLaborItem.getNumberCount())).orElse(BigDecimal.ZERO)).reduce(BigDecimal.ZERO,BigDecimal::add)
                            ).setScale(2, BigDecimal.ROUND_HALF_UP));
                            tmpResDto.setLaborNumber(
                                    tmpLabor.stream().map(tmpLaborItem->Optional.ofNullable(new BigDecimal(tmpLaborItem.getNumberCount())).orElse(BigDecimal.ZERO)).reduce(BigDecimal.ZERO,BigDecimal::add)
                            );
                            specilaResult.add(tmpResDto);
                        }
                    }
                }
            }

            return specilaResult.stream().sorted(Comparator.comparing(o->o.getProcessDetailCode()+"_"+o.getWorkPositionCode()+"_"+o.getDeptId()+"_"+o.getEquipmentTypeCode())).collect(Collectors.toList());
        }
        //正常的逻辑
        if(FOR_MACHINE.equals(query.getAllotType())){

                //获取二次配工中的机械分配情况
                //获取单类型分配量
                tmpQuery.setDispatchType(DISP_FOR_MACHINE);
                tmpQuery.setWorkPlanId(query.getWorkPlanId());
                List<TPrdDispatchSecondaryDTO> tmpSecDispInfo = ticketSecondAllotMapper.getSecondDisptchInfo(tmpQuery);
                //为数组增量做准备
                Map<String, List<TPrdDispatchSecondaryDTO>> tmpMachineMap = tmpSecDispInfo.stream().collect(Collectors.groupingBy(o->o.getDeptId().toString()+"_"+o.getSubProcessCode()+"_"+o.getWorkPositionName()+"_"+o.getEquipmentTypeCode()));
                //为计算机械单价做准备
                Map<String, List<TPrdDispatchSecondaryDTO>> machineTypeMap = tmpSecDispInfo.stream().collect(Collectors.groupingBy(o->o.getSubProcessCode()+"_"+o.getWorkPositionCode()));
                HashMap<String, BigDecimal> singleMachine = new HashMap<>();
                for (String tmpId : machineTypeMap.keySet()) {
                    List<TPrdDispatchSecondaryDTO> tPrdDispatchSecondaryDTOS = machineTypeMap.get(tmpId);
                    if (!tPrdDispatchSecondaryDTOS.isEmpty()) {
                        singleMachine.put(tmpId,
                                allTon.divide(Optional.ofNullable(BigDecimal.valueOf(tPrdDispatchSecondaryDTOS.size())).orElse(BigDecimal.ZERO), 2, BigDecimal.ROUND_HALF_UP));
                    }
                }
                for (String tmpKey : tmpMachineMap.keySet()) {
                    List<TPrdDispatchSecondaryDTO> v = tmpMachineMap.get(tmpKey);
                    if (v.isEmpty()) {
                        //正常情况
                    } else {
                        TPrdWorkTicketDetailDTO tmpResDto = SerializationUtils.clone(result.get(0));
                        tmpResDto.setCargoInfoId(null);
                        //回写前端派机信息 供后端新增使用
                        List<TPrdWorkTicketEquipmentPO> tPrdWorkTicketEquipmentPOS = new ArrayList<>();
                        for (TPrdDispatchSecondaryDTO tmpForDto : v) {
                            TPrdWorkTicketEquipmentPO tPrdWorkTicketEquipmentPO = new TPrdWorkTicketEquipmentPO();
                            tPrdWorkTicketEquipmentPO.setEquipmentId(tmpForDto.getEquipmentId());
                            tPrdWorkTicketEquipmentPO.setEquipmentNo(tmpForDto.getEquipmentNo());
                            tPrdWorkTicketEquipmentPO.setEquipmentTypeCode(tmpForDto.getEquipmentTypeCode());
                            tPrdWorkTicketEquipmentPO.setEquipmentTypeName(tmpForDto.getEquipmentTypeName());
                            tPrdWorkTicketEquipmentPOS.add(tPrdWorkTicketEquipmentPO);
                        }
                        //回写前端派机信息
                        tmpResDto.setEquipmentNo(tPrdWorkTicketEquipmentPOS.stream().map(TPrdWorkTicketEquipmentPO::getEquipmentNo).distinct().collect(Collectors.joining(",")));
                        tmpResDto.setEquipmentId(tPrdWorkTicketEquipmentPOS.stream().map(o -> o.getEquipmentId().toString()).distinct().collect(Collectors.joining(",")));
                        tmpResDto.setEquipmentTypeCode(tPrdWorkTicketEquipmentPOS.stream().map(TPrdWorkTicketEquipmentPO::getEquipmentTypeCode).distinct().collect(Collectors.joining(",")));
                        tmpResDto.setEquipmentTypeName(tPrdWorkTicketEquipmentPOS.stream().map(TPrdWorkTicketEquipmentPO::getEquipmentTypeName).distinct().collect(Collectors.joining(",")));
                        tmpResDto.setEquipments(tPrdWorkTicketEquipmentPOS);
                        tmpResDto.setDeptId(v.get(0).getDeptId());
                        tmpResDto.setDeptName(v.get(0).getDeptName());
                        tmpResDto.setProcessDetailCode(v.get(0).getSubProcessCode());
                        tmpResDto.setProcessDetailName(v.get(0).getSubProcessName());
                        tmpResDto.setWorkPositionName(v.get(0).getWorkPositionName());
                        tmpResDto.setWorkPositionCode(v.get(0).getWorkPositionCode());
                        //分配量
                        tmpResDto.setTon(BigDecimal.valueOf(v.size()).multiply(Optional.ofNullable(singleMachine.get(v.get(0).getSubProcessCode()+"_"+v.get(0).getWorkPositionCode())).orElse(BigDecimal.ZERO)).setScale(2,BigDecimal.ROUND_HALF_UP));

                        resultWithoutTicket.add(tmpResDto);
                    }
                }
            }else if(FOR_LABOR.equals(query.getAllotType())){
                tmpQuery.setDispatchType(DISP_FOR_LABOR);
                tmpQuery.setWorkPlanId(query.getWorkPlanId());
                List<TPrdDispatchSecondaryDTO> tmpLaborSecDispInfo = ticketSecondAllotMapper.getSecondDisptchInfo(tmpQuery);
                if (tmpLaborSecDispInfo.isEmpty()){
                    throw new BusinessRuntimeException("没有人员配工信息");
                }
                tmpLaborSecDispInfo.forEach(o->{
                    if(o.getDeptParentId()==null){
                        throw new BusinessRuntimeException("装卸队二次配工中缺少部门信息");
                    }
                    o.setTmpDeptId(o.getDeptId());
                    o.setTmpDeptName(o.getDeptName());
                    o.setDeptId(o.getDeptParentId());
                    o.setDeptName(o.getDeptParentName());
                });
                if(tmpLaborSecDispInfo.isEmpty()){

                }else {
                    // 结算单人作业单价
                    //拆分每个部分派工多少人，为集合增量做准备，为计算吨数做准备
                    Map<String, List<TPrdDispatchSecondaryDTO>> laborMap = tmpLaborSecDispInfo.stream()
                            .collect(Collectors.groupingBy(o->o.getDeptId()+"_"+o.getSubProcessCode()+"_"+o.getWorkPositionName()));
                    Map<String, List<TPrdDispatchSecondaryDTO>> tmpSingleMap = tmpLaborSecDispInfo.stream().collect(Collectors.groupingBy(o -> o.getSubProcessCode()+"_"+o.getWorkPositionName()));

                    //分配签票
                    for (String tmpId : laborMap.keySet()) {
                        List<TPrdDispatchSecondaryDTO> tmpLabor = laborMap.get(tmpId);
                        List<TPrdDispatchSecondaryDTO> tPrdDispatchSecondaryDTOS = tmpSingleMap.get(tmpLabor.get(0).getSubProcessCode()+"_"+tmpLabor.get(0).getWorkPositionName());
                        BigDecimal signleLabor = allTon.divide(tPrdDispatchSecondaryDTOS.stream().map(o->new BigDecimal(o.getNumberCount())).reduce(BigDecimal.ZERO,BigDecimal::add), 2, BigDecimal.ROUND_HALF_UP);

                        if(tmpLabor.isEmpty()){

                        }else{

                            TPrdWorkTicketDetailDTO tmpResDto = SerializationUtils.clone(result.get(0));
                            tmpResDto.setCargoInfoId(null);

                            tmpResDto.setDeptName(tmpLabor.get(0).getDeptName());
                            tmpResDto.setDeptId(tmpLabor.get(0).getDeptId());
                            tmpResDto.setProcessDetailName(tmpLabor.get(0).getSubProcessName());
                            tmpResDto.setProcessDetailCode(tmpLabor.get(0).getSubProcessCode());
                            tmpResDto.setWorkPositionName(tmpLabor.get(0).getWorkPositionName());
                            tmpResDto.setWorkPositionCode(tmpLabor.get(0).getWorkPositionCode());
                            tmpResDto.setTon(signleLabor.multiply(
                                    tmpLabor.stream().map(tmpLaborItem->Optional.ofNullable(new BigDecimal(tmpLaborItem.getNumberCount())).orElse(BigDecimal.ZERO)).reduce(BigDecimal.ZERO,BigDecimal::add)
                            ).setScale(2, BigDecimal.ROUND_HALF_UP));
                            tmpResDto.setLaborNumber(
                                    tmpLabor.stream().map(tmpLaborItem->Optional.ofNullable(new BigDecimal(tmpLaborItem.getNumberCount())).orElse(BigDecimal.ZERO)).reduce(BigDecimal.ZERO,BigDecimal::add)
                            );
                            resultWithoutTicket.add(tmpResDto);
                        }
                    }
            }
        }

        return resultWithoutTicket.stream().sorted(Comparator.comparing(o->o.getProcessDetailCode()+"_"+o.getWorkPositionCode()+"_"+o.getDeptId()+"_"+o.getEquipmentTypeCode())).collect(Collectors.toList());
    }

    /**
     * 新增作业票
     * @param workTicket
     */
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public void insertWorkTicket(TPrdWorkTicketDTO workTicket) {
        DistributedLock.newBuilder().store(redisTemplate)
                .key(DistributedLockKeyPrefixEnum.WORK_TICKET_KEY.getCode() + workTicket.getWorkPlanId())
                .build().run(() -> {
                    if(workTicket.getId()==null){
                        throw new BusinessRuntimeException("缺失作业票信息");
                    }
                    if(!FOR_MACHINE.equals(workTicket.getAllotType())&&!FOR_LABOR.equals(workTicket.getAllotType())){
                        throw new BusinessRuntimeException("分配参数错误");
                    }
                    TPrdWorkPlanPO workPlan = ticketNewMapper.getWorkPlan(workTicket.getWorkPlanId());
                    //杂项计划必填项检验
                    if (!DispatchEnum.WorkPlanTypeEnum.SUNDRY.getCode().equals(workPlan.getPlanType())) {
                        workTicket.getDetails().forEach(v1 -> {
                            if (v1.getTrustCargoInfoId() == null) {
                                throw new BusinessRuntimeException("指令票货ID不能为空");
                            }
                            if (v1.getCargoInfoId() == null) {
                                throw new BusinessRuntimeException("票货ID不能为空");
                            }
                            if (StringUtils.isEmpty(v1.getCabinNo()) && ("下舱".equals(v1.getProcessDetailName()))) {
                                throw new BusinessRuntimeException("下舱作业过程，请选择舱口");
                            }
                            if (v1.getTon() == null && (!"下舱".equals(v1.getProcessDetailName()))) {
                                throw new BusinessRuntimeException("吨数（数量）不能为空");
                            }
                            if (!"下舱".equals(v1.getProcessDetailName())) {
                                if (v1.getTon().compareTo(BigDecimal.ZERO) < 0) {
                                    throw new BusinessRuntimeException("吨数（数量）不能小于0");
                                }
                            }
                        });
                        //其他计划的必填项的检验
                    }else {
                        workTicket.getDetails().forEach(o->{
                            if(StringUtils.isBlank(o.getDeptName())){
                                throw new BusinessRuntimeException("部门列填写不完全");
                            }
                            if(StringUtils.isBlank(o.getProcessDetailCode())){
                                throw new BusinessRuntimeException("作业过程列填写不完全");
                            }
                            if(o.getTrustCargoInfoId()==null){
                                throw new BusinessRuntimeException("票货信息列信息不完全");
                            }
                        });
                    }

                    //校验同一部门是否在同一作业过程下的同一票货下的同种机械类型是否重复签票
//                    Map<String, List<TPrdWorkTicketDetailDTO>> tmpCheckMap = workTicket.getDetails().stream().collect(Collectors.groupingBy(o -> {
//                        System.out.println(o.getCargoInfoId()+"_"+o.getProcessDetailCode()+"_"+o.getDeptId()+"_"+o.getEquipmentTypeCode());
//                        return o.getCargoInfoId().toString() + o.getDeptId().toString();
//                    }));
//                    tmpCheckMap.forEach((k,v)->{
//                        if(v.size()>1){
//                            throw new BusinessRuntimeException(v.get(0).getCargoInfoName()+"_"+v.get(0).getProcessName()+"_"+v.get(0).getDeptName()+"重复签票");
//                        }
//                    });


                    TPrdTicketSeconAllotQuery query = new TPrdTicketSeconAllotQuery();
                    query.setNoGj("1");
                    query.setAllotType(workTicket.getAllotType());
                    query.setWorkPlanId(workTicket.getWorkPlanId());
                    List<TPrdWorkTicketDetailDTO> ticketDetailDTOList = ticketSecondAllotMapper.getTicketDetailList(query);
                    if (!ticketDetailDTOList.isEmpty()) {
                        throw new BusinessRuntimeException("当前计划已分配,请先撤销分配");
                    }

                    List<TPrdWorkTicketDetailDTO> workTicketDetails = workTicket.getDetails();
                    //根据作业票主表获取分配前的量 不查固机队的一票货只有一条签票子表数据
                    query = new TPrdTicketSeconAllotQuery();
                    //通过主表获取
                    query.setId(workTicket.getId());
                    //获取签票数据不要固机队
                    query.setNoGj("1");
                    //获取签票数据只要签票的
                    query.setAllotType(DO_TICKET);
                    List<TPrdWorkTicketDetailDTO> detailForCheck = ticketSecondAllotMapper.getTicketDetailList(query);
                    if(FOR_MACHINE.equals(workTicket.getAllotType())){

                        Map<Long, TPrdWorkTicketDetailDTO> detailMapForCheck = detailForCheck.stream().collect(Collectors.toMap(TPrdWorkTicketDetailDTO::getCargoInfoId, Function.identity(), (k1, k2) -> k2));
                        Map<Long, List<TPrdWorkTicketDetailDTO>> detailCarfoInfoMap = workTicketDetails.stream().collect(Collectors.groupingBy(o -> Optional.ofNullable(o.getCargoInfoId()).orElseThrow(() -> new BusinessRuntimeException("缺少票货数据"))));
                        //外层大循环次数不会很多
                        for (Long cargoInfoId : detailCarfoInfoMap.keySet()) {
                            //填写的数据
                            List<TPrdWorkTicketDetailDTO> tmpDetailList = detailCarfoInfoMap.get(cargoInfoId);
                            //用于校验的一次分配的数据
                            TPrdWorkTicketDetailDTO ticketDetailForCheck = detailMapForCheck.get(cargoInfoId);
                            if(tmpDetailList==null){
                                //一般不会走到这里
                                throw new BusinessRuntimeException("填写的签票数据为空!");
                            }else {
                                //machineType 前端传值 读浮动
                                Map<String, List<TPrdWorkTicketDetailDTO>> machineMap = tmpDetailList.stream().collect(Collectors.groupingBy(TPrdWorkTicketDetailDTO::getEquipmentTypeCode));
                                for (String tmpMachineType : machineMap.keySet()) {
                                    if(ticketDetailForCheck.getTon().subtract(
                                            Optional.ofNullable(machineMap.get(tmpMachineType)).orElse(new ArrayList<>())
                                                    .stream().map(o->Optional.ofNullable(o.getTon()).orElseThrow(()->new BusinessRuntimeException("缺少吨数"))).reduce(BigDecimal.ZERO,BigDecimal::add)
                                    ).abs().compareTo(BigDecimal.ONE)>0){
                                        throw new BusinessRuntimeException("机械类型"+tmpDetailList.get(0).getEquipmentTypeName()+"超过总作业量");
                                    }
                                }
                            }
                        }
                    }else if (FOR_LABOR.equals(workTicket.getAllotType())){
                        //前端填写的按票货分组
                        Map<Long, List<TPrdWorkTicketDetailDTO>> laborTicketList = workTicketDetails.stream().collect(Collectors.groupingBy(TPrdWorkTicketDetailDTO::getCargoInfoId));
                        for (TPrdWorkTicketDetailDTO ticketForCheck : detailForCheck) {
                            //获取当前校验的票货对应的前端填写的分配数据集合
                            List<TPrdWorkTicketDetailDTO> checkList = laborTicketList.get(ticketForCheck.getCargoInfoId());
                            if(ticketForCheck.getTon().subtract(checkList.stream().map(TPrdWorkTicketDetailDTO::getTon).reduce(BigDecimal.ZERO,BigDecimal::add) ).abs().compareTo(BigDecimal.ONE) >0){
                                throw new BusinessRuntimeException(ticketForCheck.getCargoInfoName()+"人员分配超总量");
                            }
                        }
                    }

                    //根据计划查询航次id
                    Map<String, Object> trustShipvoyage = new HashMap<>();
                    if ("1".equals(workPlan.getPlanType())) {
                        trustShipvoyage = ticketNewMapper.getShipvoyage(workTicket.getWorkPlanId());
                    } else {
                        trustShipvoyage = ticketNewMapper.getTrustShipvoyage(workPlan.getTrustId());
                    }
                    Map<String, Object> finalTrustShipvoyage = trustShipvoyage;
                    workTicketDetails.forEach(v1 -> {
                        if (finalTrustShipvoyage != null) {
                            v1.setShipvoyageId(finalTrustShipvoyage.get("id").toString());
                            v1.setShipvoyageItemId(finalTrustShipvoyage.get("itemId").toString());
                        }
                    });


                    workTicket.setCompanyId(workPlan.getCompanyId());
                    workTicket.setCompanyName(workPlan.getCompanyName());
                    workTicket.setType(workPlan.getPlanType());
                    workTicket.setProcessCode(workPlan.getProcessCode());
                    workTicket.setProcessName(workPlan.getProcessName());
                    workTicket.setWorkDate(workPlan.getWorkDate());
                    workTicket.setClassCode(workPlan.getClassCode());
                    workTicket.setClassName(workPlan.getClassName());
                    workTicket.setWorkTicketStatus(WorkTicketStatusEnum._10.getCode());
                    workTicket.setWorkTicketStatusName(WorkTicketStatusEnum._10.getName());

                    //返回作业票机械子表数据，给作业票子表赋值
                    List<TPrdWorkTicketEquipmentPO> equipments = workTicket.getDetails().stream().flatMap(v1 -> {
                        v1.setId(snowflake.nextId());
                        v1.setWorkTicketId(workTicket.getId());
                        v1.setProcessCode(workTicket.getProcessCode());
                        v1.setProcessName(workTicket.getProcessName());
                        v1.setWorkDate(workTicket.getWorkDate());
                        v1.setClassCode(workTicket.getClassCode());
                        v1.setClassName(workTicket.getClassName());
                        v1.setAllotType(workTicket.getAllotType());

                        return Optional.ofNullable(v1.getEquipments()).orElse(Collections.emptyList()).stream().peek(v2 -> {
                            v2.setId(snowflake.nextId());
                            v2.setWorkTicketDetailId(v1.getId());
                        });
                    }).collect(Collectors.toList());

                    Optional.ofNullable(workTicket.getLabors()).orElse(Collections.emptyList()).forEach(v1 -> {
                        v1.setId(snowflake.nextId());
                        v1.setWorkTicketId(workTicket.getId());
                    });

                    //默认签票类型是签票 1签票，2机械，3人工
                    workTicket.getDetails().forEach(o->{
                        o.setAllotType(workTicket.getAllotType());
                    });

                    ticketNewMapper.insertWorkTicketDetail(workTicket.getDetails());
                    if (CollectionUtils.isNotEmpty(equipments)) {
                        ticketNewMapper.insertWorkTicketEquipment(equipments);
                    }
                    //没用到
                    if (CollectionUtils.isNotEmpty(workTicket.getLabors())) {
                        ticketNewMapper.insertWorkTicketLabor(workTicket.getLabors());
                    }

                    //分配计件
                    String planType = ticketNewMapper.getWorkPlanType(workTicket.getWorkPlanId());
                    SpringUtils.getBean(TPrdWorkTicketNewServiceImpl.class).commonUpdateSalary(workTicket,workTicket.getDetails(),true,"2".equals(planType ),"二次分配");
                });
    }
    /**
     * 新增作业票
     * @param workTicket
     */
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public void insertWorkTicketNoCargo(TPrdWorkTicketDTO workTicket) {
        DistributedLock.newBuilder().store(redisTemplate)
                .key(DistributedLockKeyPrefixEnum.WORK_TICKET_KEY.getCode() + workTicket.getWorkPlanId())
                .build().run(() -> {
                    if(workTicket.getId()==null){
                        throw new BusinessRuntimeException("缺失作业票信息");
                    }
                    if(!FOR_MACHINE.equals(workTicket.getAllotType())&&!FOR_LABOR.equals(workTicket.getAllotType())){
                        throw new BusinessRuntimeException("分配参数错误");
                    }
                    TPrdWorkPlanPO workPlan = ticketNewMapper.getWorkPlan(workTicket.getWorkPlanId());

                    if(!workPlan.getPlanType().equals("4")){
                        int count = ticketNewMapper.getExByWorkDate(workPlan.getWorkDate());
                        if(count>0){
                            String date = DateUtils.formatDate(workPlan.getWorkDate(),"yyyy-MM");
                            throw new BusinessRuntimeException(date + "已存在HR审核");
                        }
                    }

                    //辅助计划
                    if(workPlan.getPlanType().equals("4")){
                        int count = ticketNewMapper.getFuZhuExByWorkDate(workPlan.getWorkDate());
                        if(count>0){
                            String date = DateUtils.formatDate(workPlan.getWorkDate(),"yyyy-MM");
                            throw new BusinessRuntimeException(date + "已存在HR审核");
                        }
                    }

                    if (!DispatchEnum.WorkPlanTypeEnum.SUNDRY.getCode().equals(workPlan.getPlanType())) {
                        workTicket.getDetails().forEach(v1 -> {
                            if (v1.getTon() == null && (!"下舱".equals(v1.getProcessDetailName()))) {
                                throw new BusinessRuntimeException("吨数（数量）不能为空");
                            }
                            if (!"下舱".equals(v1.getProcessDetailName())) {
//                                if (v1.getTon().compareTo(BigDecimal.ZERO) <= 0) {
//                                    throw new BusinessRuntimeException("吨数（数量）不能小于等于0");
//                                }
                            }
                        });
                        //其他计划的必填项的检验
                    }else {
                        workTicket.getDetails().forEach(o->{
                            if(StringUtils.isBlank(o.getDeptName())){
                                throw new BusinessRuntimeException("部门列填写不完全");
                            }
                            if(StringUtils.isBlank(o.getProcessDetailCode())){
                                throw new BusinessRuntimeException("作业过程列填写不完全");
                            }
                            if(o.getTrustCargoInfoId()==null){
                                throw new BusinessRuntimeException("票货信息列信息不完全");
                            }
                        });
                    }

                    if("1".equals(workPlan.getPlanType())||"3".equals(workPlan.getPlanType())){
                        SysParameterDTO WHOLE_IS_ADJUST = sysParameterMapper.getByKey("WHOLE_IS_ADJUST");
                        if("Y".equals(WHOLE_IS_ADJUST.getParamVal())){
                            if(FOR_LABOR.equals(workTicket.getAllotType())){
                                if("20".equals(workPlan.getIsAdjustLabor())){
                                    throw new BusinessRuntimeException("已经整船调整过，不能调整分配");
                                }
                            }
                            if(FOR_MACHINE.equals(workTicket.getAllotType())) {
                                if("20".equals(workPlan.getIsAdjust())){
                                    throw new BusinessRuntimeException("已经整船调整过，不能调整分配");
                                }

                            }
                        }
                    }

                    TPrdTicketSeconAllotQuery query = new TPrdTicketSeconAllotQuery();
                    query.setNoGj("1");
                    query.setAllotType(workTicket.getAllotType());
                    query.setWorkPlanId(workTicket.getWorkPlanId());
                    List<TPrdWorkTicketDetailDTO> ticketDetailDTOList = ticketSecondAllotMapper.getTicketDetailList(query);
                    if (!ticketDetailDTOList.isEmpty()) {
                        throw new BusinessRuntimeException("当前计划已分配,请先撤销分配");
                    }

                    List<TPrdWorkTicketDetailDTO> workTicketDetails = workTicket.getDetails();
                    //根据作业票主表获取分配前的量 不查固机队的 一票货只有一条签票子表数据
                    query = new TPrdTicketSeconAllotQuery();
                    //通过主表获取
                    query.setId(workTicket.getId());
                    //获取签票数据不要固机队
                    query.setNoGj("1");
                    //获取签票数据只要签票的
                    query.setAllotType(DO_TICKET);
                    List<TPrdWorkTicketDetailDTO> detailForCheck = ticketSecondAllotMapper.getTicketDetailList(query);
                    BigDecimal allTon = detailForCheck.stream().map(TPrdWorkTicketDetailPO::getTon).reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal bigDecimal = BigDecimal.ONE;
                    SysParameterDTO sysParameter = sysParameterMapper.getByKey("ALLOT_FUDONG_LIANG");;
                    BigDecimal forCheck = new BigDecimal(sysParameter.getParamVal());

//                    if(StringUtils.isBlank(sysParameter.getParamVal())){
//
//                    }else{
//                        try {
//                            forCheck = new BigDecimal(sysParameter.getParamVal());
//
//                        }catch (Exception e){
//                            throw new BusinessRuntimeException("分配浮动量参数转换异常");
//                        }
//                    }

                    if(FOR_MACHINE.equals(workTicket.getAllotType())){
//                        Map<String, List<TPrdWorkTicketDetailDTO>> tmpCheckMap = workTicket.getDetails().stream().collect(Collectors.groupingBy(o -> {
//                            return o.getProcessDetailCode() + o.getDeptId().toString()+"_"+o.getWorkPositionCode();
//                        }));
//                        tmpCheckMap.forEach((k,v)->{
//                            if(v.size()>1){
//                                throw new BusinessRuntimeException("重复签票！</br>作业过程："+v.get(0).getProcessName()+"</br>部门："+v.get(0).getDeptName()+"</br>作业位置"+v.get(0).getWorkPositionCode());
//                            }
//                        });


                        Map<String, List<TPrdWorkTicketDetailDTO>> detailCarfoInfoMap = workTicketDetails.stream().collect(Collectors.groupingBy(o -> o.getProcessDetailCode()+"_"+o.getWorkPositionCode()+"_"+o.getDeptId()));
                        //按照作业过程+机械类型+位置分组判断是否超量
                        for (String cargoInfoId : detailCarfoInfoMap.keySet()) {
                            List<TPrdWorkTicketDetailDTO> tmpDetailList = detailCarfoInfoMap.get(cargoInfoId);
                            if( Optional.ofNullable(tmpDetailList).orElse(new ArrayList<>())
                                    .stream().map(o->Optional.ofNullable(o.getTon()).orElseThrow(()->new BusinessRuntimeException("缺少吨数"))).reduce(BigDecimal.ZERO,BigDecimal::add)
                                            .subtract(allTon).compareTo(forCheck)>0){
                                throw new BusinessRuntimeException("机械分配超总作业量</br>作业过程："+tmpDetailList.get(0).getProcessDetailName()+"</br>机械类型:"+tmpDetailList.get(0).getEquipmentTypeName()+"</br>");
                            }
                        }
                    }else if (FOR_LABOR.equals(workTicket.getAllotType())){
//                        Map<String, List<TPrdWorkTicketDetailDTO>> tmpCheckMap = workTicket.getDetails().stream().collect(Collectors.groupingBy(o -> {
//                            return o.getProcessDetailCode() + o.getDeptId().toString()+"_"+o.getWorkPositionCode();
//                        }));
//                        tmpCheckMap.forEach((k,v)->{
//                            if(v.size()>1){
//                                throw new BusinessRuntimeException("重复签票！</br>作业过程："+v.get(0).getProcessName()+"</br>部门："+v.get(0).getDeptName()+"</br>作业位置"+v.get(0).getWorkPositionCode());
//                            }
//                        });

                        //按照作业过程+位置 分组判断是否超量
                        Map<String, List<TPrdWorkTicketDetailDTO>> collect = workTicket.getDetails().stream().collect(Collectors.groupingBy(o->o.getProcessDetailCode()+"_"+o.getWorkPositionCode()));
                        collect.forEach((k,v)->{
                            if(v.stream().map(TPrdWorkTicketDetailPO::getTon).reduce(BigDecimal.ZERO,BigDecimal::add).subtract(allTon)
                                    .compareTo(forCheck) >0){
                                throw new BusinessRuntimeException("人员分配超总作业量</br>作业过程："+v.get(0).getProcessDetailName()+"</br>位置："+v.get(0).getWorkPositionCode()+"</br>");
                            }
                        });
                    }

                    //根据计划查询航次id
                    Map<String, Object> trustShipvoyage = new HashMap<>();
                    if ("1".equals(workPlan.getPlanType())) {
                        trustShipvoyage = ticketNewMapper.getShipvoyage(workTicket.getWorkPlanId());
                    } else {
                        trustShipvoyage = ticketNewMapper.getTrustShipvoyage(workPlan.getTrustId());
                    }
                    Map<String, Object> finalTrustShipvoyage = trustShipvoyage;
                    workTicketDetails.forEach(v1 -> {
                        if (finalTrustShipvoyage != null) {
                            v1.setShipvoyageId(finalTrustShipvoyage.get("id").toString());
                            v1.setShipvoyageItemId(finalTrustShipvoyage.get("itemId").toString());
                        }
                    });


                    workTicket.setCompanyId(workPlan.getCompanyId());
                    workTicket.setCompanyName(workPlan.getCompanyName());
                    workTicket.setType(workPlan.getPlanType());
                    workTicket.setProcessCode(workPlan.getProcessCode());
                    workTicket.setProcessName(workPlan.getProcessName());
                    workTicket.setWorkDate(workPlan.getWorkDate());
                    workTicket.setClassCode(workPlan.getClassCode());
                    workTicket.setClassName(workPlan.getClassName());
                    workTicket.setWorkTicketStatus(WorkTicketStatusEnum._10.getCode());
                    workTicket.setWorkTicketStatusName(WorkTicketStatusEnum._10.getName());

                    //返回作业票机械子表数据，给作业票子表赋值
                    List<TPrdWorkTicketEquipmentPO> equipments = workTicket.getDetails().stream().flatMap(v1 -> {
                        v1.setId(snowflake.nextId());
                        v1.setWorkTicketId(workTicket.getId());
                        v1.setProcessCode(workTicket.getProcessCode());
                        v1.setProcessName(workTicket.getProcessName());
                        v1.setWorkDate(workTicket.getWorkDate());
                        v1.setClassCode(workTicket.getClassCode());
                        v1.setClassName(workTicket.getClassName());
                        v1.setAllotType(workTicket.getAllotType());

                        return Optional.ofNullable(v1.getEquipments()).orElse(Collections.emptyList()).stream().peek(v2 -> {
                            v2.setId(snowflake.nextId());
                            v2.setWorkTicketDetailId(v1.getId());
                        });
                    }).collect(Collectors.toList());

                    Optional.ofNullable(workTicket.getLabors()).orElse(Collections.emptyList()).forEach(v1 -> {
                        v1.setId(snowflake.nextId());
                        v1.setWorkTicketId(workTicket.getId());
                    });

                    //默认签票类型是签票 1签票，2机械，3人工
                    workTicket.getDetails().forEach(o->{
                        o.setAllotType(workTicket.getAllotType());
                    });

                    ticketNewMapper.insertWorkTicketDetail(workTicket.getDetails());
                    if (CollectionUtils.isNotEmpty(equipments)) {
                        ticketNewMapper.insertWorkTicketEquipment(equipments);
                    }
                    //没用到
                    if (CollectionUtils.isNotEmpty(workTicket.getLabors())) {
                        ticketNewMapper.insertWorkTicketLabor(workTicket.getLabors());
                    }

                    //分配计件
                    String planType = ticketNewMapper.getWorkPlanType(workTicket.getWorkPlanId());
                    //取改计划下任意一条签票的货物来确定是剑散，后面分配计件用
                    workTicket.getDetails().forEach(o->{
                        o.setCargoCode(detailForCheck.get(0).getCargoCode());
                        o.setCargoName(detailForCheck.get(0).getCargoName());
                    });
                    SpringUtils.getBean(TPrdWorkTicketNewServiceImpl.class).commonUpdateSalary(workTicket,workTicket.getDetails(),true,"2".equals(planType ),"二次分配");
                });
    }

    /**
     * 按理货主表和分配类型删除
     * @param ticketId
     * @param allotType
     */
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public void deleteAllot(Long ticketId, String allotType) {
        if(!FOR_MACHINE.equals(allotType)&&!FOR_LABOR.equals(allotType)){
            throw new BusinessRuntimeException("分配参数错误");
        }
        TPrdTicketSeconAllotQuery query = new TPrdTicketSeconAllotQuery();
        query.setId(ticketId);
        query.setAllotType(allotType);
        List<TPrdWorkTicketDetailDTO> ticketDetailList = ticketSecondAllotMapper.getTicketDetailList(query);
        if (ticketDetailList.isEmpty()) {
            throw new BusinessRuntimeException("没有查询到签票信息无需撤销签票");
        }

        TPrdWorkPlanPO workPlan = ticketNewMapper.getWorkPlan(Optional.ofNullable(ticketDetailList.stream().findFirst().orElseThrow(null).getWorkPlanId()).orElseThrow(()->new BusinessRuntimeException("从签票获取作业计划信息失败")));

        if(!workPlan.getPlanType().equals("4")){
            int count = ticketNewMapper.getExByWorkDate(workPlan.getWorkDate());
            if(count>0){
                String date = DateUtils.formatDate(workPlan.getWorkDate(),"yyyy-MM");
                throw new BusinessRuntimeException(date + "已存在HR审核");
            }
        }
        //辅助计划
        if(workPlan.getPlanType().equals("4")){
            int count = ticketNewMapper.getFuZhuExByWorkDate(workPlan.getWorkDate());
            if(count>0){
                String date = DateUtils.formatDate(workPlan.getWorkDate(),"yyyy-MM");
                throw new BusinessRuntimeException(date + "已存在HR审核");
            }
        }


        if("1".equals(workPlan.getPlanType())||"3".equals(workPlan.getPlanType())){
            SysParameterDTO WHOLE_IS_ADJUST = sysParameterMapper.getByKey("WHOLE_IS_ADJUST");
            if("Y".equals(WHOLE_IS_ADJUST.getParamVal())){
                if(FOR_LABOR.equals(allotType)){
                    if("20".equals(workPlan.getIsAdjustLabor())){
                        throw new BusinessRuntimeException("已经整船调整过，不能调整分配");
                    }
                }
                if(FOR_MACHINE.equals(allotType)) {
                    if("20".equals(workPlan.getIsAdjust())){
                        throw new BusinessRuntimeException("已经整船调整过，不能调整分配");
                    }

                }
            }
        }

        ticketSecondAllotMapper.deleteWorkTicketDetail(ticketId,allotType);
        List<Long> workTicketDetailIds = ticketDetailList.stream().map(TPrdWorkTicketDetailPO::getId).collect(Collectors.toList());
        ticketNewMapper.deleteWorkTicketEquipment(workTicketDetailIds);

        //分配计件
        List<TPrdSalaryPO> detailSalarys = ticketNewMapper.getSalaryByTicketDetial(ticketDetailList.stream().map(TPrdWorkTicketDetailDTO::getId).collect(Collectors.toList()));
        List<com.yy.ppm.produce.bean.po.TPrdSalaryPO> collect = detailSalarys.stream().filter(o -> !"10".equals(o.getSalaryStatusCode())).collect(Collectors.toList());
        if (!collect.isEmpty()) {
            throw new BusinessRuntimeException("计件已审核");
        }

        if(!detailSalarys.isEmpty()){
            ticketNewMapper.deleteSalary(ticketDetailList.stream().map(TPrdWorkTicketDetailDTO::getId).collect(Collectors.toList()));
        }
    }

    /**
     * 获取二次分配的操作班组信息
     * @param allotType
     * @return
     */
    @Override
    public List<SysDeptDTO> getDepts(String allotType) {
        if(!FOR_MACHINE.equals(allotType)&& !FOR_LABOR.equals(allotType)){
            throw new BusinessRuntimeException("分配参数错误");
        }
        return ticketSecondAllotMapper.getDepts(allotType);
    }
}
