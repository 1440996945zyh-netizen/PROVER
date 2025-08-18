package com.yy.ppm.produce.service.impl;

import com.google.common.collect.Maps;
import com.yy.common.page.Pages;
import com.yy.common.util.*;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.common.enums.ShipStatusEnum;
import com.yy.ppm.produce.bean.dto.TDisShipVoyageDTO;
import com.yy.ppm.produce.bean.dto.TWholeShipAdjustmenRes;
import com.yy.ppm.produce.bean.dto.TWholeShipAdjustmentQueryDTO;
import com.yy.ppm.produce.bean.dto.workTicket.TPrdWorkTicketDTO;
import com.yy.ppm.produce.bean.dto.workTicket.TPrdWorkTicketDetailDTO;
import com.yy.ppm.produce.bean.po.TPrdSalaryPO;
import com.yy.ppm.produce.bean.po.TPrdWorkTicketDetailPO;
import com.yy.ppm.produce.bean.po.TPrdWorkTicketPO;
import com.yy.ppm.produce.bean.po.TWholeShipAdjustmentExaminePO;
import com.yy.ppm.produce.mapper.TPrdTicketSecondAllotMapper;
import com.yy.ppm.produce.mapper.TPrdWorkTicketNewMapper;
import com.yy.ppm.produce.mapper.WholeShipAdjustmentMapper;
import com.yy.ppm.produce.service.TWholeShipAdjustmentService;
import com.yy.ppm.system.bean.dto.SysParameterDTO;
import com.yy.ppm.system.mapper.SysParameterMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import jakarta.annotation.Resource;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class TWholeShipAdjustmentServiceImpl implements TWholeShipAdjustmentService {

    @Autowired
    WholeShipAdjustmentMapper mapper;
    @Resource
    private SysParameterMapper sysParameterMapper;
    @Autowired
    private TPrdWorkTicketNewMapper workTicketMapper;
    @Autowired
    private PlatformTransactionManager transactionManager;

    List<TransactionStatus> transactionStatuses = Collections.synchronizedList(new ArrayList<TransactionStatus>());

    private static final String FOR_TICKET = "1";//分配类型是机械
    private static final String FOR_MACHINE = "2";//分配类型是机械
    private static final String FOR_LABOR = "3";//分配类型是人工
    @Autowired
    private SpringUtils springUtils;


    @Override
    public Pages<TDisShipVoyageDTO> getList(TWholeShipAdjustmentQueryDTO queryDTO) {

        Pages<TDisShipVoyageDTO> result = PageHelperUtils.limit(queryDTO, () -> {
            return mapper.getList(queryDTO);
        });

        return result;
    }

    @Override
    public TWholeShipAdjustmenRes getTicketListForChange(TWholeShipAdjustmentQueryDTO queryDTO) {
        if(StringUtils.isBlank(queryDTO.getAllotType())){
            throw new BusinessRuntimeException("缺少分配类型参数");
        }
        if(!FOR_MACHINE.equals(queryDTO.getAllotType())&&!FOR_LABOR.equals(queryDTO.getAllotType())){
            throw new BusinessRuntimeException("分配类型参数错误");
        }

        List<Map<Long,String>> shipInfo = mapper.getShipInfo(queryDTO);
        if(shipInfo == null){
            throw new BusinessRuntimeException("船名航次的不存在");
        }
        List<String> dynamicTypeCodeList = shipInfo.stream().map(o -> String.valueOf(o.get("dynamicTypeCode"))).collect(Collectors.toList());
        if(!dynamicTypeCodeList.stream().anyMatch(o->o.equals(ShipStatusEnum.WANGONG.getCode()))){
            throw new BusinessRuntimeException("船舶尚未完工");
        }
        if(shipInfo.get(0).get("loadUnload").equals("卸")) {
            if (StringUtils.isNotBlank(String.valueOf(shipInfo.get(0).get("isShipClear")))) {
                if ("1".equals(String.valueOf(shipInfo.get(0).get("isShipClear")))) {

                } else {
                    throw new BusinessRuntimeException("船舶尚未完工确认");
                }
            } else {
                throw new BusinessRuntimeException("船舶尚未完工确认");
            }
        }

        TWholeShipAdjustmenRes result = new TWholeShipAdjustmenRes();

        List<TPrdWorkTicketDetailDTO> ticketInfoList = new ArrayList<>();
        List<TPrdWorkTicketDetailDTO> ticketInfoFuZhuList = new ArrayList<>();
        if (FOR_MACHINE.equals(queryDTO.getAllotType())) {
            ticketInfoList  = mapper.getWorkTIcketInfoListForMachine(queryDTO);
            ticketInfoFuZhuList = mapper.getWorkTicketListWithFuZhu(queryDTO);
        }else if(FOR_LABOR.equals(queryDTO.getAllotType())){
            ticketInfoList  = mapper.getWorkTIcketInfoListForLabor(queryDTO);
            ticketInfoFuZhuList = mapper.getWorkTicketListWithFuZhu(queryDTO);

        }

        if(!ticketInfoList.isEmpty()){
            //组装作业过程主代码
            List<Long> workTicketIds = ticketInfoList.stream().map(TPrdWorkTicketDetailPO::getWorkTicketId).collect(Collectors.toList());
            List<TPrdWorkTicketDetailDTO> workTicketList = mapper.getWorkTicketList(workTicketIds);

            Map<String, List<TPrdWorkTicketDetailDTO>> workTicketProcessDetailMap =
                    workTicketList.stream().collect(Collectors.groupingBy(TPrdWorkTicketDetailPO::getProcessDetailCode));
            HashMap<String, BigDecimal> stringBigDecimalHashMap = new HashMap<>();

            for (String s : workTicketProcessDetailMap.keySet()) {
                List<TPrdWorkTicketDetailDTO> workTicketDetailDTOS = workTicketProcessDetailMap.get(s);
                stringBigDecimalHashMap.put(s,workTicketDetailDTOS.stream().map(o->Optional.ofNullable(o.getTon()).orElseThrow(()->new BusinessRuntimeException("获取理货确认重量失败"))).reduce(BigDecimal.ZERO,BigDecimal::add));
            }

            Map<Long, List<TPrdWorkTicketDetailDTO>> ticketMap = workTicketList.stream().collect(Collectors.groupingBy(TPrdWorkTicketDetailPO::getWorkTicketId));
            HashMap<Long, BigDecimal> ticketMapDecimal = new HashMap<>();
            ticketMap.forEach((k1,v1)->{
                ticketMapDecimal.put(k1,v1.stream().map(o->Optional.ofNullable(o.getTon()).orElse(BigDecimal.ZERO) ).reduce(BigDecimal.ZERO,BigDecimal::add));
            });


            if(FOR_MACHINE.equals(queryDTO.getAllotType())){
            Map<String, List<TPrdWorkTicketDetailDTO>> topTicketMap =
                    ticketInfoList.stream().collect(Collectors.groupingBy(
                            o -> o.getWorkPositionCode()+"_"+o.getProcessDetailCode() + "_" + o.getDeptId().toString()
                            + "_" + String.valueOf(o.getEquipmentTypeName())));
            //作业过程加位置
                Map<String, List<TPrdWorkTicketDetailDTO>> collect4 = ticketInfoList.stream().collect(Collectors.groupingBy(o->
                        o.getProcessDetailCode()+ "_" + o.getWorkPositionCode() ));
                HashMap<String, TPrdWorkTicketDetailDTO> topResultMap = new HashMap<>();

            for (String s : topTicketMap.keySet()) {
                List<TPrdWorkTicketDetailDTO> workTicketDetailDTOS = topTicketMap.get(s);
                TPrdWorkTicketDetailDTO tmpDto = new TPrdWorkTicketDetailDTO();
                tmpDto.setDeptId(workTicketDetailDTOS.get(0).getDeptId());
                tmpDto.setDeptName(workTicketDetailDTOS.get(0).getDeptName());
                tmpDto.setProcessDetailName(workTicketDetailDTOS.get(0).getProcessDetailName());
                tmpDto.setProcessDetailCode(workTicketDetailDTOS.get(0).getProcessDetailCode());
                tmpDto.setProcessCode(workTicketDetailDTOS.get(0).getProcessCode());
                tmpDto.setProcessName(workTicketDetailDTOS.get(0).getProcessName());
                tmpDto.setWorkPositionName(workTicketDetailDTOS.get(0).getWorkPositionName());
                tmpDto.setWorkPositionCode(workTicketDetailDTOS.get(0).getWorkPositionCode());
                String collect = workTicketDetailDTOS.stream().filter(o -> StringUtils.isNotBlank(o.getEquipmentTypeName())).map(o -> o.getEquipmentTypeName()).collect(Collectors.joining(","));
                tmpDto.setEquipmentTypeName(new ArrayList<String>(Arrays.asList(collect.split(","))).stream().filter(StringUtils::isNotBlank).distinct().collect(Collectors.joining(",")));
                collect = workTicketDetailDTOS.stream().filter(o -> StringUtils.isNotBlank(o.getEquipmentNo())).map(o -> o.getEquipmentNo()).collect(Collectors.joining(","));
                tmpDto.setEquipmentNo(new ArrayList<String>(Arrays.asList(collect.split(","))).stream().filter(StringUtils::isNotBlank).distinct().collect(Collectors.joining(",")));
                tmpDto.setTon(workTicketDetailDTOS.stream().map(o -> o.getTon() == null ? BigDecimal.ZERO : o.getTon()).reduce(BigDecimal.ZERO, BigDecimal::add));
                //计算签票量
                ArrayList<BigDecimal> bigDecimals = new ArrayList<>();
                List<TPrdWorkTicketDetailDTO> workTicketDetailDTOS1 = collect4.get(topTicketMap.get(s).stream().findFirst().orElseThrow(null)
                        .getProcessDetailCode() + "_" + topTicketMap.get(s).stream().findFirst().orElseThrow(null).getWorkPositionCode());
                List<Long> collect1 = ticketMapDecimal.keySet().stream().filter(o ->
                        workTicketDetailDTOS1.stream().map(TPrdWorkTicketDetailPO::getWorkTicketId)
                                .filter(Objects::nonNull).collect(Collectors.toList()).stream().anyMatch(v1 -> v1.equals(o))
                ).collect(Collectors.toList());
                for (Long k1 : collect1) {
                    bigDecimals.add(ticketMapDecimal.get(k1));
                }
                tmpDto.setTmpTopTon(bigDecimals.stream().reduce(BigDecimal.ZERO,BigDecimal::add));
                topResultMap.put(s,tmpDto);
            }
            result.setTopCollect(new ArrayList<>(topResultMap.values()));

            List<TPrdWorkTicketDetailDTO> tmpBoottmResult = new ArrayList<>();
            Map<String, List<TPrdWorkTicketDetailDTO>> collect1 = ticketInfoList.stream().collect(Collectors.groupingBy(o -> o.getProcessDetailCode()));
            collect1.keySet().stream().sorted(Comparator.comparing(o->o)).forEach(k1->
            {
                Map<String, List<TPrdWorkTicketDetailDTO>> collect2 = collect1.get(k1).stream().collect(Collectors.groupingBy(o -> String.valueOf(o.getWorkPositionCode())));
                for (String k2 : collect2.keySet().stream().sorted().collect(Collectors.toList())) {
                    Map<String, List<TPrdWorkTicketDetailDTO>> collect3 = collect2.get(k2).stream().collect(Collectors.groupingBy(o -> o.getDeptId() + "_" + o.getEquipmentTypeName()));
                    collect3.keySet().stream().sorted().collect(Collectors.toList()).forEach(k3->{
                        tmpBoottmResult.addAll(collect3.get(k3).stream().sorted(Comparator.comparing(o->o.getWorkDate().toString()+"_"+o.getClassCode(),Comparator.reverseOrder())).collect(Collectors.toList()));
                        TPrdWorkTicketDetailDTO tPrdWorkTicketDetailDTO = new TPrdWorkTicketDetailDTO();
                        tPrdWorkTicketDetailDTO.setTon(collect3.get(k3).stream().map(o->Optional.of(o.getTon()).orElseThrow(()-> new BusinessRuntimeException("没有获取到吨数"))).reduce(BigDecimal.ZERO,BigDecimal::add));
                        tPrdWorkTicketDetailDTO.setGroupId(new Long("2"));
                        tPrdWorkTicketDetailDTO.setGroupKey(
                                collect3.get(k3).stream().findFirst().orElse(new TPrdWorkTicketDetailDTO()).getProcessDetailCode() + "_"
                                        + String.valueOf(collect3.get(k3).stream().findFirst().orElse(new TPrdWorkTicketDetailDTO()).getWorkPositionCode()) + "_"
                                        +collect3.get(k3).stream().findFirst().orElse(new TPrdWorkTicketDetailDTO()).getDeptId()+ "_"
                                        + collect3.get(k3).stream().findFirst().orElse(new TPrdWorkTicketDetailDTO()).getEquipmentTypeName()
                        );
                        tmpBoottmResult.add(tPrdWorkTicketDetailDTO);
                    });
                    TPrdWorkTicketDetailDTO tPrdWorkTicketDetailDTO = new TPrdWorkTicketDetailDTO();

                    tPrdWorkTicketDetailDTO.setTon(collect2.get(k2).stream().map(o->Optional.ofNullable(o.getTon()).orElse(BigDecimal.ZERO)).reduce(BigDecimal.ZERO,BigDecimal::add));
                    tPrdWorkTicketDetailDTO.setGroupId(new Long("3"));
                    tPrdWorkTicketDetailDTO.setGroupKey(
                            collect2.get(k2).stream().findFirst().orElse(new TPrdWorkTicketDetailDTO()).getProcessDetailCode() + "_"
                                    +String.valueOf(collect2.get(k2).stream().findFirst().orElse(new TPrdWorkTicketDetailDTO()).getWorkPositionCode())
                    );
                    tmpBoottmResult.add(tPrdWorkTicketDetailDTO);
                }
                TPrdWorkTicketDetailDTO tPrdWorkTicketDetailDTO = new TPrdWorkTicketDetailDTO();

                ticketMapDecimal.keySet().stream().filter(item->
                        collect1.get(k1).stream().map(TPrdWorkTicketDetailPO::getWorkTicketId
                        ).filter(Objects::nonNull).collect(Collectors.toList()).stream().anyMatch(item1-> item1.equals(item))
                ).forEach(item->
                        tPrdWorkTicketDetailDTO.setTon(Optional.ofNullable(tPrdWorkTicketDetailDTO.getTon()).orElse(BigDecimal.ZERO).add(ticketMapDecimal.get(item))));

                tPrdWorkTicketDetailDTO.setGroupId(new Long("4"));
                tPrdWorkTicketDetailDTO.setGroupKey(
                        collect1.get(k1).stream().findFirst().orElse(new TPrdWorkTicketDetailDTO()).getProcessDetailCode()
                );
                tmpBoottmResult.add(tPrdWorkTicketDetailDTO);
            });
            result.setBottomList(tmpBoottmResult);
            if(ticketInfoFuZhuList!=null && !ticketInfoFuZhuList.isEmpty()) {
                Map<String, List<TPrdWorkTicketDetailDTO>> collect = ticketInfoFuZhuList.stream().collect(
                        Collectors.groupingBy(o -> o.getProcessDetailName() + o.getWorkPositionCode()));
                collect.keySet().forEach(k3 -> {
                    if (result.getTopCollect() != null) {
                        TPrdWorkTicketDetailDTO tmpDto = new TPrdWorkTicketDetailDTO();
                        tmpDto.setDeptId(collect.get(k3).get(0).getDeptId());
                        tmpDto.setDeptName(collect.get(k3).get(0).getDeptName());
                        tmpDto.setProcessDetailName(collect.get(k3).get(0).getProcessDetailName());
                        tmpDto.setProcessDetailCode(collect.get(k3).get(0).getProcessDetailCode());
                        tmpDto.setProcessCode(collect.get(k3).get(0).getProcessCode());
                        tmpDto.setProcessName(collect.get(k3).get(0).getProcessName());
                        tmpDto.setWorkPositionName(collect.get(k3).get(0).getWorkPositionName());
                        tmpDto.setWorkPositionCode(collect.get(k3).get(0).getWorkPositionCode());
                        String collect01 = collect.get(k3).stream().filter(o -> StringUtils.isNotBlank(o.getEquipmentTypeName())).map(o -> o.getEquipmentTypeName()).collect(Collectors.joining(","));
                        tmpDto.setEquipmentTypeName(new ArrayList<String>(Arrays.asList(collect01.split(","))).stream().filter(StringUtils::isNotBlank).distinct().collect(Collectors.joining(",")));
                        collect01 = collect.get(k3).stream().filter(o -> StringUtils.isNotBlank(o.getEquipmentNo())).map(o -> o.getEquipmentNo()).collect(Collectors.joining(","));
                        tmpDto.setEquipmentNo(new ArrayList<String>(Arrays.asList(collect01.split(","))).stream().filter(StringUtils::isNotBlank).distinct().collect(Collectors.joining(",")));
                        tmpDto.setTon(collect.get(k3).stream().map(o -> o.getTon() == null ? BigDecimal.ZERO : o.getTon()).reduce(BigDecimal.ZERO, BigDecimal::add));
                        tmpDto.setTmpTopTon(collect.get(k3).stream().map(o -> o.getTon() == null ? BigDecimal.ZERO : o.getTon()).reduce(BigDecimal.ZERO, BigDecimal::add));
                        result.getTopCollect().add(tmpDto);
                    }
                });
                Map<String, List<TPrdWorkTicketDetailDTO>> collect3 = ticketInfoFuZhuList.stream().collect(Collectors.groupingBy(o -> o.getProcessDetailCode()));

                for (String s : collect3.keySet().stream().sorted().collect(Collectors.toList())) {
                    List<TPrdWorkTicketDetailDTO> workTicketDetailDTOS = collect3.get(s);
                    if(workTicketDetailDTOS!=null && !workTicketDetailDTOS.isEmpty()) {
                        Map<String, List<TPrdWorkTicketDetailDTO>> collect2 =
                                workTicketDetailDTOS.stream().collect(Collectors.groupingBy(tmp -> String.valueOf(tmp.getWorkPositionCode())));
                        collect2.keySet().stream().sorted().forEach(k3 -> {
                            List<TPrdWorkTicketDetailDTO> workTicketDetailDTOS1 = collect2.get(k3);
                            if(workTicketDetailDTOS1!=null && !workTicketDetailDTOS1.isEmpty()) {
                                Map<String, List<TPrdWorkTicketDetailDTO>> collect5 = workTicketDetailDTOS1.stream().collect(Collectors.groupingBy(tmp -> tmp.getDeptId() + "_" + tmp.getEquipmentTypeId()));
                                if(collect5!=null && !collect5.isEmpty()) {
                                    collect5.keySet().stream().sorted().forEach(k4 -> {
                                        List<TPrdWorkTicketDetailDTO> collect6 = collect5.get(k4).stream().collect(Collectors.toList());
                                        result.getBottomList().addAll(collect6.stream().sorted(Comparator.comparing(tmpK->tmpK.getDeptId().toString())).collect(Collectors.toList()));
                                        TPrdWorkTicketDetailDTO tPrdWorkTicketDetailDTO = new TPrdWorkTicketDetailDTO();
                                        tPrdWorkTicketDetailDTO.setGroupId(new Long("2"));
                                        tPrdWorkTicketDetailDTO.setGroupKey(collect6.get(0).getProcessDetailCode()+"_"+String.valueOf(collect6.get(0).getWorkPositionCode())
                                                +"_"+collect6.get(0).getDeptId().toString()+"_"+String.valueOf(collect6.get(0).getEquipmentTypeName()));
                                        tPrdWorkTicketDetailDTO.setTon(collect6.stream().map(o->Optional.ofNullable(o.getTon()).orElseThrow(()->new BusinessRuntimeException("辅助签票缺失作业量"))).reduce(BigDecimal.ZERO,BigDecimal::add));

                                        result.getBottomList().add(tPrdWorkTicketDetailDTO);
                                    });
                                };
                                TPrdWorkTicketDetailDTO tPrdWorkTicketDetailDTO = new TPrdWorkTicketDetailDTO();
                                tPrdWorkTicketDetailDTO.setGroupId(new Long("3"));
                                tPrdWorkTicketDetailDTO.setGroupKey(workTicketDetailDTOS1.get(0).getProcessDetailCode()+"_"+String.valueOf(workTicketDetailDTOS1.get(0).getWorkPositionCode()));
                                tPrdWorkTicketDetailDTO.setTon(workTicketDetailDTOS1.stream().map(o->Optional.ofNullable(o.getTon()).orElseThrow(()->new BusinessRuntimeException("辅助签票缺失作业量"))).reduce(BigDecimal.ZERO,BigDecimal::add));

                                result.getBottomList().add(tPrdWorkTicketDetailDTO);
                            }
                        });
                        TPrdWorkTicketDetailDTO tPrdWorkTicketDetailDTO = new TPrdWorkTicketDetailDTO();
                        tPrdWorkTicketDetailDTO.setGroupId(new Long("4"));
                        tPrdWorkTicketDetailDTO.setGroupKey(workTicketDetailDTOS.get(0).getProcessDetailCode());
                        tPrdWorkTicketDetailDTO.setTon(workTicketDetailDTOS.stream().map(o->Optional.ofNullable(o.getTon()).orElseThrow(()->new BusinessRuntimeException("辅助签票缺失作业量"))).reduce(BigDecimal.ZERO,BigDecimal::add));
                        result.getBottomList().add(tPrdWorkTicketDetailDTO);
                    }
                }
            }

            //回填签票子表
        }else if(FOR_LABOR.equals(queryDTO.getAllotType())){
                Map<String, List<TPrdWorkTicketDetailDTO>> topTicketMap =
                        ticketInfoList.stream().collect(Collectors.groupingBy(
                                o -> o.getProcessDetailCode() + "_" + o.getDeptId().toString() + "_" + o.getWorkPositionCode()));
                Map<String, List<TPrdWorkTicketDetailDTO>> collect4 = ticketInfoList.stream().collect(Collectors.groupingBy(
                        o -> o.getProcessDetailCode() + "_" + o.getWorkPositionCode()));
                HashMap<String, TPrdWorkTicketDetailDTO> topResultMap = new HashMap<>();
                for (String s : topTicketMap.keySet()) {
                    List<TPrdWorkTicketDetailDTO> workTicketDetailDTOS = topTicketMap.get(s);
                    TPrdWorkTicketDetailDTO tmpDto = new TPrdWorkTicketDetailDTO();
                    tmpDto.setDeptId(workTicketDetailDTOS.get(0).getDeptId());
                    tmpDto.setDeptName(workTicketDetailDTOS.get(0).getDeptName());
                    tmpDto.setProcessDetailName(workTicketDetailDTOS.get(0).getProcessDetailName());
                    tmpDto.setProcessDetailCode(workTicketDetailDTOS.get(0).getProcessDetailCode());
                    tmpDto.setProcessCode(workTicketDetailDTOS.get(0).getProcessCode());
                    tmpDto.setProcessName(workTicketDetailDTOS.get(0).getProcessName());
                    tmpDto.setWorkPositionName(workTicketDetailDTOS.get(0).getWorkPositionName());
                    tmpDto.setWorkPositionCode(workTicketDetailDTOS.get(0).getWorkPositionCode());
                    tmpDto.setTon(workTicketDetailDTOS.stream().map(o -> o.getTon() == null ? BigDecimal.ZERO : o.getTon())
                            .reduce(BigDecimal.ZERO, BigDecimal::add));
                    //统计签票总量
                    List<TPrdWorkTicketDetailDTO> workTicketDetailDTOS1 = collect4.get(topTicketMap.get(s).stream().findFirst().orElseThrow(null)
                            .getProcessDetailCode() + "_" + topTicketMap.get(s).stream().findFirst().orElseThrow(null).getWorkPositionCode());

                    List<Long> collect = ticketMapDecimal.keySet().stream().filter(o ->
                            workTicketDetailDTOS1.stream().map(TPrdWorkTicketDetailPO::getWorkTicketId).filter(Objects::nonNull).collect(Collectors.toList()).stream().anyMatch(v1 -> v1.equals(o))
                    ).collect(Collectors.toList());
                    ArrayList<BigDecimal> bigDecimals = new ArrayList<>();
                    for (Long k1 : collect) {
                        bigDecimals.add(ticketMapDecimal.get(k1));
                    }
                    tmpDto.setTmpTopTon(bigDecimals.stream().reduce(BigDecimal.ZERO,BigDecimal::add));
                    topResultMap.put(s,tmpDto);
                }
                result.setTopCollect(new ArrayList<>(topResultMap.values()));
            //回填底部集合子表

            List<TPrdWorkTicketDetailDTO> tmpBoottmResult = new ArrayList<>();
                Map<String, List<TPrdWorkTicketDetailDTO>> collect1 = ticketInfoList.stream().collect(Collectors.groupingBy(TPrdWorkTicketDetailPO::getProcessDetailCode));
                collect1.keySet().stream().sorted(Comparator.comparing(o->o)).forEach(k1->
                {
                    Map<String, List<TPrdWorkTicketDetailDTO>> collect2 = collect1.get(k1).stream().collect(Collectors.groupingBy(tmpItem->Optional.ofNullable(tmpItem.getWorkPositionCode()).orElse("null")));
                    for (String k2 : collect2.keySet().stream().sorted().collect(Collectors.toList())) {
                        Map<String, List<TPrdWorkTicketDetailDTO>> collect3 = collect2.get(k2).stream().collect(Collectors.groupingBy(o -> o.getDeptId().toString() ));
                        collect3.keySet().stream().sorted().collect(Collectors.toList()).forEach(k3->{
                            tmpBoottmResult.addAll(collect3.get(k3).stream().sorted(Comparator.comparing(o->o.getWorkDate().toString()+"_"+o.getClassCode(),Comparator.reverseOrder())).collect(Collectors.toList()));
                            TPrdWorkTicketDetailDTO tPrdWorkTicketDetailDTO = new TPrdWorkTicketDetailDTO();
                            tPrdWorkTicketDetailDTO.setTon(collect3.get(k3).stream().map(o->Optional.of(o.getTon()).orElseThrow(()-> new BusinessRuntimeException("没有获取到吨数"))).reduce(BigDecimal.ZERO,BigDecimal::add));
                            tPrdWorkTicketDetailDTO.setGroupId(new Long("2"));
                            tPrdWorkTicketDetailDTO.setGroupKey(
                                    collect3.get(k3).stream().findFirst().orElse(new TPrdWorkTicketDetailDTO()).getProcessDetailCode() + "_"
                                            + String.valueOf(collect3.get(k3).stream().findFirst().orElse(new TPrdWorkTicketDetailDTO()).getWorkPositionCode()) + "_"
                                            + collect3.get(k3).stream().findFirst().orElse(new TPrdWorkTicketDetailDTO()).getDeptId()
                            );
                            tmpBoottmResult.add(tPrdWorkTicketDetailDTO);
                        });
                        TPrdWorkTicketDetailDTO tPrdWorkTicketDetailDTO = new TPrdWorkTicketDetailDTO();

                        tPrdWorkTicketDetailDTO.setTon(collect2.get(k2).stream().map(o->Optional.ofNullable(o.getTon()).orElse(BigDecimal.ZERO)).reduce(BigDecimal.ZERO,BigDecimal::add));
                        tPrdWorkTicketDetailDTO.setGroupId(new Long("3"));
                        tPrdWorkTicketDetailDTO.setGroupKey(
                                collect2.get(k2).stream().findFirst().orElse(new TPrdWorkTicketDetailDTO()).getProcessDetailCode() + "_"
                                        +String.valueOf(collect2.get(k2).stream().findFirst().orElse(new TPrdWorkTicketDetailDTO()).getWorkPositionCode())
                        );
                        tmpBoottmResult.add(tPrdWorkTicketDetailDTO);
                    }
                    TPrdWorkTicketDetailDTO tPrdWorkTicketDetailDTO = new TPrdWorkTicketDetailDTO();
                    //统计签票总量
                    ticketMapDecimal.keySet().stream().filter(item->
                        collect1.get(k1).stream().map(TPrdWorkTicketDetailPO::getWorkTicketId
                        ).filter(Objects::nonNull).collect(Collectors.toList()).stream().anyMatch(item1-> item1.equals(item))
                    ).forEach(item->
                            tPrdWorkTicketDetailDTO.setTon(Optional.ofNullable(tPrdWorkTicketDetailDTO.getTon()).orElse(BigDecimal.ZERO).add(ticketMapDecimal.get(item))));
                    tPrdWorkTicketDetailDTO.setGroupId(new Long("4"));
                    tPrdWorkTicketDetailDTO.setGroupKey(
                            collect1.get(k1).stream().findFirst().orElse(new TPrdWorkTicketDetailDTO()).getProcessDetailCode()
                    );
                    tmpBoottmResult.add(tPrdWorkTicketDetailDTO);
                });
                result.setBottomList(tmpBoottmResult);

                Map<String, List<TPrdWorkTicketDetailDTO>> collect5 = ticketInfoFuZhuList.stream().collect(
                        Collectors.groupingBy(o -> o.getProcessDetailCode() + "_" + String.valueOf(o.getWorkPositionCode() )+ "_" +String.valueOf(Optional.ofNullable(o.getDeptId()).orElseThrow(()->new BusinessRuntimeException("辅助签票缺少签票部门")))));
                collect5.keySet().stream().sorted().forEach(tmpK->{
                    TPrdWorkTicketDetailDTO tPrdWorkTicketDetailDTO = new TPrdWorkTicketDetailDTO();
                    tPrdWorkTicketDetailDTO.setProcessDetailCode(collect5.get(tmpK).get(0).getProcessDetailCode());
                    tPrdWorkTicketDetailDTO.setProcessDetailName(collect5.get(tmpK).get(0).getProcessDetailName());
                    tPrdWorkTicketDetailDTO.setDeptName(collect5.get(tmpK).get(0).getDeptName());
                    tPrdWorkTicketDetailDTO.setTon(collect5.get(tmpK).stream().map(o->Optional.ofNullable(o.getTon()).orElse(BigDecimal.ZERO)).reduce(BigDecimal.ZERO,BigDecimal::add));
                    tPrdWorkTicketDetailDTO.setTmpTopTon(collect5.get(tmpK).stream().map(o->Optional.ofNullable(o.getTon()).orElse(BigDecimal.ZERO)).reduce(BigDecimal.ZERO,BigDecimal::add));

                    result.getTopCollect().add(tPrdWorkTicketDetailDTO);

                });

                Map<String, List<TPrdWorkTicketDetailDTO>> collect =
                        ticketInfoFuZhuList.stream().collect(Collectors.groupingBy(o -> o.getProcessDetailCode()));
                for (String s : collect.keySet().stream().sorted().collect(Collectors.toList())) {
                    List<TPrdWorkTicketDetailDTO> workTicketDetailDTOS = collect.get(s);
                    if (workTicketDetailDTOS!=null&&!workTicketDetailDTOS.isEmpty()){
                        Map<String, List<TPrdWorkTicketDetailDTO>> collect2 = workTicketDetailDTOS.stream().collect(Collectors.groupingBy(tmpk -> String.valueOf(tmpk.getWorkPositionCode())));
                        for (String K3 : collect2.keySet().stream().sorted().collect(Collectors.toList())) {
                            if(collect2.get(K3)!=null&&!collect2.get(K3).isEmpty()){
                                List<TPrdWorkTicketDetailDTO> workTicketDetailDTOS1 = collect2.get(K3);
                                Map<Long, List<TPrdWorkTicketDetailDTO>> collect3 = workTicketDetailDTOS1.stream().collect(Collectors.groupingBy(tmpK4 ->Optional.ofNullable(tmpK4.getDeptId()).orElseThrow(()->new BusinessRuntimeException("辅助签票缺少签票部门")) ));
                                collect3.keySet().stream().sorted().forEach(tmpk4->{
                                    List<TPrdWorkTicketDetailDTO> workTicketDetailDTOS2 = collect3.get(tmpk4);
                                    if(workTicketDetailDTOS2!=null&&!workTicketDetailDTOS2.isEmpty()){
                                        result.getBottomList().addAll(workTicketDetailDTOS2.stream().sorted(Comparator.comparing(tmpk5->tmpk5.getWorkDate()+"_"+tmpk5.getClassCode())).collect(Collectors.toList()));
                                        TPrdWorkTicketDetailDTO tPrdWorkTicketDetailDTO = new TPrdWorkTicketDetailDTO();
                                        tPrdWorkTicketDetailDTO.setGroupId(new Long("2"));
                                        tPrdWorkTicketDetailDTO.setGroupKey(workTicketDetailDTOS2.get(0).getProcessDetailCode()
                                                +"_"+String.valueOf(workTicketDetailDTOS2.get(0).getWorkPositionCode())
                                                +"_"+workTicketDetailDTOS2.get(0).getDeptId());
                                        tPrdWorkTicketDetailDTO.setTon(workTicketDetailDTOS2.stream().map(o->o.getTon()).reduce(BigDecimal.ZERO,BigDecimal::add));
                                        result.getBottomList().add(tPrdWorkTicketDetailDTO);
                                    }
                                });

                                TPrdWorkTicketDetailDTO tPrdWorkTicketDetailDTO = new TPrdWorkTicketDetailDTO();
                                tPrdWorkTicketDetailDTO.setGroupId(new Long("3"));
                                tPrdWorkTicketDetailDTO.setTon(workTicketDetailDTOS1.stream().map(o->o.getTon()).reduce(BigDecimal.ZERO,BigDecimal::add));
                                tPrdWorkTicketDetailDTO.setGroupKey(workTicketDetailDTOS1.get(0).getProcessDetailCode()+"_"+String.valueOf(workTicketDetailDTOS1.get(0).getWorkPositionCode()));
                                result.getBottomList().add(tPrdWorkTicketDetailDTO);
                            }
                        }
                        TPrdWorkTicketDetailDTO tPrdWorkTicketDetailDTO = new TPrdWorkTicketDetailDTO();
                        tPrdWorkTicketDetailDTO.setGroupId(new Long("4"));
                        tPrdWorkTicketDetailDTO.setGroupKey(workTicketDetailDTOS.get(0).getProcessDetailCode());
                        tPrdWorkTicketDetailDTO.setTon(workTicketDetailDTOS.stream().map(o->Optional.ofNullable(o.getTon()).orElseThrow(()->new BusinessRuntimeException("辅助签票缺少签票作业量"))).reduce(BigDecimal.ZERO,BigDecimal::add));
                        result.getBottomList().add(tPrdWorkTicketDetailDTO);
                    }
                }
            }
        }
        if(result.getTopCollect()!=null && !result.getTopCollect().isEmpty()){
            result.setTopCollect(result.getTopCollect().stream().sorted(Comparator.comparing(o->
                    o.getProcessDetailCode()+"_"+o.getWorkPositionCode()
            )).collect(Collectors.toList()));
        }
        return result;
    }

    @Override
    public TWholeShipAdjustmenRes getTicketListForChange1128(TWholeShipAdjustmentQueryDTO queryDTO) {
        if(StringUtils.isBlank(queryDTO.getAllotType())){
            throw new BusinessRuntimeException("缺少分配类型参数");
        }
        if(!FOR_MACHINE.equals(queryDTO.getAllotType())&&!FOR_LABOR.equals(queryDTO.getAllotType())){
            throw new BusinessRuntimeException("分配类型参数错误");
        }

        List<Map<Long,String>> shipInfo = mapper.getShipInfo(queryDTO);
        if(shipInfo == null){
            throw new BusinessRuntimeException("船名航次的不存在");
        }
        List<String> dynamicTypeCodeList = shipInfo.stream().map(o -> String.valueOf(o.get("dynamicTypeCode"))).collect(Collectors.toList());
        if(!dynamicTypeCodeList.stream().anyMatch(o->o.equals(ShipStatusEnum.WANGONG.getCode()))){
            throw new BusinessRuntimeException("船舶尚未完工");
        }
        if(shipInfo.get(0).get("loadUnload").equals("卸")){
            if(StringUtils.isNotBlank(String.valueOf(shipInfo.get(0).get("isShipClear")))){
                if ("1".equals(String.valueOf(shipInfo.get(0).get("isShipClear")))){

                }else{
                    throw new BusinessRuntimeException("船舶尚未完工确认");
                }
            }else {
                throw new BusinessRuntimeException("船舶尚未完工确认");
            }
        }

        TWholeShipAdjustmenRes result = new TWholeShipAdjustmenRes();
        result.setTopCollect(new ArrayList<>());

        List<TPrdWorkTicketDetailDTO> ticketInfoList = new ArrayList<>();
        List<TPrdWorkTicketDetailDTO> ticketInfoFuZhuList = new ArrayList<>();
        if (FOR_MACHINE.equals(queryDTO.getAllotType())) {
            ticketInfoList  = mapper.getWorkTIcketInfoListForMachine(queryDTO);
            ticketInfoFuZhuList = mapper.getWorkTicketListWithFuZhu(queryDTO);



        }else if(FOR_LABOR.equals(queryDTO.getAllotType())){
            ticketInfoList  = mapper.getWorkTIcketInfoListForLabor(queryDTO);
            ticketInfoFuZhuList = mapper.getWorkTicketListWithFuZhu(queryDTO);

        }
        if(CollectionUtils.isEmpty(ticketInfoList)&&CollectionUtils.isEmpty(ticketInfoFuZhuList)){
            return null;
        }
        List<Date> collectDateCheck = new ArrayList<>();
        Date date = null;
        if(!CollectionUtils.isEmpty(ticketInfoList)){
            collectDateCheck = ticketInfoList.stream().map(TPrdWorkTicketDetailPO::getWorkDateCheck).distinct().collect(Collectors.toList());
            if(!CollectionUtils.isEmpty(ticketInfoFuZhuList)){
                collectDateCheck.addAll(ticketInfoFuZhuList.stream().map(TPrdWorkTicketDetailPO::getWorkDateCheck).distinct().collect(Collectors.toList()));
            }
        }else{
            if(!CollectionUtils.isEmpty(ticketInfoFuZhuList)){
                collectDateCheck.addAll(ticketInfoFuZhuList.stream().map(TPrdWorkTicketDetailPO::getWorkDateCheck).distinct().collect(Collectors.toList()));
            }
        }
        List<Map<Date,String>> canEditDate = mapper.getHrExTicketList(collectDateCheck);
        Map<String, String> canEditDateMap = new HashMap<>();
        for (Map<Date, String> dateStringMap : canEditDate) {
            canEditDateMap.put(dateStringMap.get("workDate"),dateStringMap.get("flag"));
        }
        if(!CollectionUtils.isEmpty(ticketInfoFuZhuList)){
            if(canEditDate!=null && canEditDate.size()>0){
                for (TPrdWorkTicketDetailDTO tPrdWorkTicketDetailDTO : ticketInfoFuZhuList) {
                    if(canEditDateMap.get(tPrdWorkTicketDetailDTO.getWorkDateCheck())!=null &&
                            "N".equals(canEditDateMap.get(tPrdWorkTicketDetailDTO.getWorkDateCheck()))){
                        tPrdWorkTicketDetailDTO.setCanEdit(false);
                    }else{
                        tPrdWorkTicketDetailDTO.setCanEdit(true);
                    }
                }
            }else{
                for (TPrdWorkTicketDetailDTO tPrdWorkTicketDetailDTO : ticketInfoFuZhuList) {
                    tPrdWorkTicketDetailDTO.setCanEdit(true);
                }
            }

        }

        if(!ticketInfoList.isEmpty()){
            if(canEditDate!=null && canEditDate.size()>0){
                for (TPrdWorkTicketDetailDTO tPrdWorkTicketDetailDTO : ticketInfoList) {
                    if(canEditDateMap.get(tPrdWorkTicketDetailDTO.getWorkDateCheck())!=null &&
                "N".equals(canEditDateMap.get(tPrdWorkTicketDetailDTO.getWorkDateCheck()))){
                        tPrdWorkTicketDetailDTO.setCanEdit(false);
                    }else{
                        tPrdWorkTicketDetailDTO.setCanEdit(true);
                    }
                }
            }else{
                for (TPrdWorkTicketDetailDTO tPrdWorkTicketDetailDTO : ticketInfoList) {
                    tPrdWorkTicketDetailDTO.setCanEdit(true);
                }
            }


            //组装作业过程主代码
            List<Long> workTicketIds = ticketInfoList.stream().map(TPrdWorkTicketDetailPO::getWorkTicketId).collect(Collectors.toList());
            List<TPrdWorkTicketDetailDTO> workTicketList = mapper.getWorkTicketList(workTicketIds);

            Map<String, List<TPrdWorkTicketDetailDTO>> workTicketProcessDetailMap =
                    workTicketList.stream().collect(Collectors.groupingBy(TPrdWorkTicketDetailPO::getProcessDetailCode));
            HashMap<String, BigDecimal> stringBigDecimalHashMap = new HashMap<>();

            for (String s : workTicketProcessDetailMap.keySet()) {
                List<TPrdWorkTicketDetailDTO> workTicketDetailDTOS = workTicketProcessDetailMap.get(s);
                stringBigDecimalHashMap.put(s,workTicketDetailDTOS.stream().map(o->Optional.ofNullable(o.getTon()).orElseThrow(()->new BusinessRuntimeException("获取理货确认重量失败"))).reduce(BigDecimal.ZERO,BigDecimal::add));
            }

            Map<Long, List<TPrdWorkTicketDetailDTO>> ticketMap = workTicketList.stream().collect(Collectors.groupingBy(TPrdWorkTicketDetailPO::getWorkTicketId));
            HashMap<Long, BigDecimal> ticketMapDecimal = new HashMap<>();
            ticketMap.forEach((k1,v1)->{
                ticketMapDecimal.put(k1,v1.stream().map(o->Optional.ofNullable(o.getTon()).orElse(BigDecimal.ZERO) ).reduce(BigDecimal.ZERO,BigDecimal::add));
            });


            if(FOR_MACHINE.equals(queryDTO.getAllotType())){
                Map<Long, List<TPrdWorkTicketDetailDTO>> trust_map = ticketInfoList.stream().collect(Collectors.groupingBy(o -> o.getTrustId()));
                List<TPrdWorkTicketDetailDTO> tmpBoottmResult = new ArrayList<>();

                for (Long trust_id : trust_map.keySet()) {
                    List<TPrdWorkTicketDetailDTO> workTicketDetailDTOS_trust = trust_map.get(trust_id);
                    Map<String, List<TPrdWorkTicketDetailDTO>> topTicketMap =
                            workTicketDetailDTOS_trust.stream().collect(Collectors.groupingBy(
                                    o -> o.getWorkPositionCode()+"_"+o.getProcessDetailCode() + "_" + o.getDeptId().toString()
                                            + "_" + String.valueOf(o.getEquipmentTypeName())));
                    //作业过程加位置
                    Map<String, List<TPrdWorkTicketDetailDTO>> collect4 =
                            workTicketDetailDTOS_trust.stream().collect(
                                    Collectors.groupingBy(o->o.getProcessDetailCode()+ "_" + o.getWorkPositionCode() ));
                    HashMap<String, TPrdWorkTicketDetailDTO> topResultMap = new HashMap<>();
                    for (String s : topTicketMap.keySet()) {
                        List<TPrdWorkTicketDetailDTO> workTicketDetailDTOS = topTicketMap.get(s);
                        TPrdWorkTicketDetailDTO tmpDto = new TPrdWorkTicketDetailDTO();
                        tmpDto.setDeptId(workTicketDetailDTOS.get(0).getDeptId());
                        tmpDto.setDeptName(workTicketDetailDTOS.get(0).getDeptName());
                        tmpDto.setProcessDetailName(workTicketDetailDTOS.get(0).getProcessDetailName());
                        tmpDto.setProcessDetailCode(workTicketDetailDTOS.get(0).getProcessDetailCode());
                        tmpDto.setProcessCode(workTicketDetailDTOS.get(0).getProcessCode());
                        tmpDto.setProcessName(workTicketDetailDTOS.get(0).getProcessName());
                        tmpDto.setWorkPositionName(workTicketDetailDTOS.get(0).getWorkPositionName());
                        tmpDto.setWorkPositionCode(workTicketDetailDTOS.get(0).getWorkPositionCode());
                        tmpDto.setTrustNo(workTicketDetailDTOS.get(0).getTrustNo());
                        String collect = workTicketDetailDTOS.stream().filter(o -> StringUtils.isNotBlank(o.getEquipmentTypeName())).map(o -> o.getEquipmentTypeName()).collect(Collectors.joining(","));
                        tmpDto.setEquipmentTypeName(new ArrayList<String>(Arrays.asList(collect.split(","))).stream().filter(StringUtils::isNotBlank).distinct().collect(Collectors.joining(",")));
                        collect = workTicketDetailDTOS.stream().filter(o -> StringUtils.isNotBlank(o.getEquipmentNo())).map(o -> o.getEquipmentNo()).collect(Collectors.joining(","));
                        tmpDto.setEquipmentNo(new ArrayList<String>(Arrays.asList(collect.split(","))).stream().filter(StringUtils::isNotBlank).distinct().collect(Collectors.joining(",")));
                        tmpDto.setTon(workTicketDetailDTOS.stream().map(o -> o.getTon() == null ? BigDecimal.ZERO : o.getTon()).reduce(BigDecimal.ZERO, BigDecimal::add));
                        //计算签票量
                        ArrayList<BigDecimal> bigDecimals = new ArrayList<>();
                        List<TPrdWorkTicketDetailDTO> workTicketDetailDTOS1 = collect4.get(topTicketMap.get(s).stream().findFirst().orElseThrow(null)
                                .getProcessDetailCode() +
                                "_" + topTicketMap.get(s).stream().findFirst().orElseThrow(null).getWorkPositionCode());
                        List<Long> collect1 = ticketMapDecimal.keySet().stream().filter(o ->
                                workTicketDetailDTOS1.stream().map(TPrdWorkTicketDetailPO::getWorkTicketId)
                                        .filter(Objects::nonNull).collect(Collectors.toList()).stream().anyMatch(v1 -> v1.equals(o))
                        ).collect(Collectors.toList());
                        for (Long k1 : collect1) {
                            bigDecimals.add(ticketMapDecimal.get(k1));
                        }
                        tmpDto.setTmpTopTon(bigDecimals.stream().reduce(BigDecimal.ZERO,BigDecimal::add));
                        topResultMap.put(s,tmpDto);
                        result.getTopCollect().add(tmpDto);
                    }
//                    result.setTopCollect(new ArrayList<>(topResultMap.values()));


                    List<TPrdWorkTicketDetailDTO> workTicketDetailDTOS = trust_map.get(trust_id);
                    Map<String, List<TPrdWorkTicketDetailDTO>> collect1 = workTicketDetailDTOS.stream().collect(Collectors.groupingBy(o -> o.getProcessDetailCode()));
                    collect1.keySet().stream().sorted(Comparator.comparing(o->o)).forEach(k1->{
                        Map<String, List<TPrdWorkTicketDetailDTO>> collect2 = collect1.get(k1).stream().collect(Collectors.groupingBy(o -> String.valueOf(o.getWorkPositionCode())));
                        for (String k2 : collect2.keySet().stream().sorted().collect(Collectors.toList())) {
                            Map<String, List<TPrdWorkTicketDetailDTO>> collect3 = collect2.get(k2).stream().collect(Collectors.groupingBy(o -> o.getDeptId() + "_" + o.getEquipmentTypeName()));
                            collect3.keySet().stream().sorted().collect(Collectors.toList()).forEach(k3->{
                                tmpBoottmResult.addAll(collect3.get(k3).stream().sorted(Comparator.comparing(o->o.getWorkDate().toString()+"_"+o.getClassCode(),Comparator.reverseOrder())).collect(Collectors.toList()));
                                TPrdWorkTicketDetailDTO tPrdWorkTicketDetailDTO = new TPrdWorkTicketDetailDTO();
                                tPrdWorkTicketDetailDTO.setTon(collect3.get(k3).stream().map(o->Optional.of(o.getTon()).orElseThrow(()-> new BusinessRuntimeException("没有获取到吨数"))).reduce(BigDecimal.ZERO,BigDecimal::add));
                                tPrdWorkTicketDetailDTO.setGroupId(new Long("2"));
                                tPrdWorkTicketDetailDTO.setGroupKey(
                                        collect3.get(k3).stream().findFirst().orElse(new TPrdWorkTicketDetailDTO()).getProcessDetailCode() + "_"
                                                + String.valueOf(collect3.get(k3).stream().findFirst().orElse(new TPrdWorkTicketDetailDTO()).getWorkPositionCode()) + "_"
                                                +collect3.get(k3).stream().findFirst().orElse(new TPrdWorkTicketDetailDTO()).getDeptId()+ "_"
                                                + collect3.get(k3).stream().findFirst().orElse(new TPrdWorkTicketDetailDTO()).getEquipmentTypeName()
                                                + "_" + collect3.get(k3).stream().findFirst().orElse(new TPrdWorkTicketDetailDTO()).getTrustNo()
                                );
                                tPrdWorkTicketDetailDTO.setCanEdit(collect3.get(k3).get(0).getCanEdit());
                                tmpBoottmResult.add(tPrdWorkTicketDetailDTO);
                            });
                            TPrdWorkTicketDetailDTO tPrdWorkTicketDetailDTO = new TPrdWorkTicketDetailDTO();

                            tPrdWorkTicketDetailDTO.setTon(collect2.get(k2).stream().map(o->Optional.ofNullable(o.getTon()).orElse(BigDecimal.ZERO)).reduce(BigDecimal.ZERO,BigDecimal::add));
                            tPrdWorkTicketDetailDTO.setGroupId(new Long("3"));
                            tPrdWorkTicketDetailDTO.setCanEdit(workTicketDetailDTOS_trust.get(0).getCanEdit());

                            tPrdWorkTicketDetailDTO.setGroupKey(
                                    collect2.get(k2).stream().findFirst().orElse(new TPrdWorkTicketDetailDTO()).getProcessDetailCode() + "_"
                                            +String.valueOf(collect2.get(k2).stream().findFirst().orElse(new TPrdWorkTicketDetailDTO()).getWorkPositionCode())
                                            + "_"
                                            +String.valueOf(collect2.get(k2).stream().findFirst().orElse(new TPrdWorkTicketDetailDTO()).getTrustNo())
                            );
                            tmpBoottmResult.add(tPrdWorkTicketDetailDTO);
                        }
                        TPrdWorkTicketDetailDTO tPrdWorkTicketDetailDTO = new TPrdWorkTicketDetailDTO();

                        ticketMapDecimal.keySet().stream().filter(item->
                                collect1.get(k1).stream().map(TPrdWorkTicketDetailPO::getWorkTicketId
                                ).filter(Objects::nonNull).collect(Collectors.toList()).stream().anyMatch(item1-> item1.equals(item))
                        ).forEach(item->
                                tPrdWorkTicketDetailDTO.setTon(Optional.ofNullable(tPrdWorkTicketDetailDTO.getTon()).orElse(BigDecimal.ZERO).add(ticketMapDecimal.get(item))));

                        tPrdWorkTicketDetailDTO.setGroupId(new Long("4"));
                        tPrdWorkTicketDetailDTO.setCanEdit(workTicketDetailDTOS_trust.get(0).getCanEdit());

                        tPrdWorkTicketDetailDTO.setGroupKey(
                                collect1.get(k1).stream().findFirst().orElse(new TPrdWorkTicketDetailDTO()).getTrustNo()
                        );
                        tmpBoottmResult.add(tPrdWorkTicketDetailDTO);
                    });

                    TPrdWorkTicketDetailDTO tPrdWorkTicketDetailDTO = new TPrdWorkTicketDetailDTO();
                    tPrdWorkTicketDetailDTO.setTon(
                            workTicketList.stream().filter(o->workTicketDetailDTOS.stream().filter(v->v.getWorkTicketId()!=null)
                            .anyMatch(v->v.getWorkTicketId().equals(o.getWorkTicketId()))).map(tmpV->tmpV.getTon()).reduce(BigDecimal.ZERO,BigDecimal::add));
                    tPrdWorkTicketDetailDTO.setGroupId(new Long("5"));
                    tPrdWorkTicketDetailDTO.setCanEdit(workTicketDetailDTOS_trust.get(0).getCanEdit());

                    tPrdWorkTicketDetailDTO.setGroupKey(
                            workTicketDetailDTOS.stream().findAny().orElse(new TPrdWorkTicketDetailDTO()).getTrustNo()
                    );
//                    tPrdWorkTicketDetailDTO.setCanEdit(workTicketDetailDTOS_trust.get(0).gettrust());
                    tmpBoottmResult.add(tPrdWorkTicketDetailDTO);
                }
                result.setBottomList(tmpBoottmResult);

                if(ticketInfoFuZhuList!=null && !ticketInfoFuZhuList.isEmpty()) {
                Map<String, List<TPrdWorkTicketDetailDTO>> collect = ticketInfoFuZhuList.stream().collect(
                        Collectors.groupingBy(o -> o.getProcessDetailName() + o.getWorkPositionCode()));
                collect.keySet().forEach(k3 -> {
                    if (result.getTopCollect() != null) {
                        TPrdWorkTicketDetailDTO tmpDto = new TPrdWorkTicketDetailDTO();
                        tmpDto.setDeptId(collect.get(k3).get(0).getDeptId());
                        tmpDto.setDeptName(collect.get(k3).get(0).getDeptName());
                        tmpDto.setProcessDetailName(collect.get(k3).get(0).getProcessDetailName());
                        tmpDto.setProcessDetailCode(collect.get(k3).get(0).getProcessDetailCode());
                        tmpDto.setProcessCode(collect.get(k3).get(0).getProcessCode());
                        tmpDto.setProcessName(collect.get(k3).get(0).getProcessName());
                        tmpDto.setWorkPositionName(collect.get(k3).get(0).getWorkPositionName());
                        tmpDto.setWorkPositionCode(collect.get(k3).get(0).getWorkPositionCode());
                        String collect01 = collect.get(k3).stream().filter(o -> StringUtils.isNotBlank(o.getEquipmentTypeName())).map(o -> o.getEquipmentTypeName()).collect(Collectors.joining(","));
                        tmpDto.setEquipmentTypeName(new ArrayList<String>(Arrays.asList(collect01.split(","))).stream().filter(StringUtils::isNotBlank).distinct().collect(Collectors.joining(",")));
                        collect01 = collect.get(k3).stream().filter(o -> StringUtils.isNotBlank(o.getEquipmentNo())).map(o -> o.getEquipmentNo()).collect(Collectors.joining(","));
                        tmpDto.setEquipmentNo(new ArrayList<String>(Arrays.asList(collect01.split(","))).stream().filter(StringUtils::isNotBlank).distinct().collect(Collectors.joining(",")));
                        tmpDto.setTon(collect.get(k3).stream().map(o -> o.getTon() == null ? BigDecimal.ZERO : o.getTon()).reduce(BigDecimal.ZERO, BigDecimal::add));
                        tmpDto.setTmpTopTon(collect.get(k3).stream().map(o -> o.getTon() == null ? BigDecimal.ZERO : o.getTon()).reduce(BigDecimal.ZERO, BigDecimal::add));
                        result.getTopCollect().add(tmpDto);
                    }
                });
                Map<String, List<TPrdWorkTicketDetailDTO>> collect3 = ticketInfoFuZhuList.stream().collect(Collectors.groupingBy(o -> o.getProcessDetailCode()));

                for (String s : collect3.keySet().stream().sorted().collect(Collectors.toList())) {
                    List<TPrdWorkTicketDetailDTO> workTicketDetailDTOS = collect3.get(s);
                    if(workTicketDetailDTOS!=null && !workTicketDetailDTOS.isEmpty()) {
                        Map<String, List<TPrdWorkTicketDetailDTO>> collect2 =
                                workTicketDetailDTOS.stream().collect(Collectors.groupingBy(tmp -> String.valueOf(tmp.getWorkPositionCode())));
                        collect2.keySet().stream().sorted().forEach(k3 -> {
                            List<TPrdWorkTicketDetailDTO> workTicketDetailDTOS1 = collect2.get(k3);
                            if(workTicketDetailDTOS1!=null && !workTicketDetailDTOS1.isEmpty()) {
                                Map<String, List<TPrdWorkTicketDetailDTO>> collect5 = workTicketDetailDTOS1.stream().collect(Collectors.groupingBy(tmp -> tmp.getDeptId() + "_" + tmp.getEquipmentTypeId()));
                                if(collect5!=null && !collect5.isEmpty()) {
                                    collect5.keySet().stream().sorted().forEach(k4 -> {
                                        List<TPrdWorkTicketDetailDTO> collect6 = collect5.get(k4).stream().collect(Collectors.toList());
                                        result.getBottomList().addAll(collect6.stream().sorted(Comparator.comparing(tmpK->tmpK.getDeptId().toString())).collect(Collectors.toList()));
                                        TPrdWorkTicketDetailDTO tPrdWorkTicketDetailDTO = new TPrdWorkTicketDetailDTO();
                                        tPrdWorkTicketDetailDTO.setGroupId(new Long("2"));
                                        tPrdWorkTicketDetailDTO.setGroupKey(collect6.get(0).getProcessDetailCode()+"_"+String.valueOf(collect6.get(0).getWorkPositionCode())
                                                +"_"+collect6.get(0).getDeptId().toString()+"_"+String.valueOf(collect6.get(0).getEquipmentTypeName()));
                                        tPrdWorkTicketDetailDTO.setTon(collect6.stream().map(o->Optional.ofNullable(o.getTon()).orElseThrow(()->new BusinessRuntimeException("辅助签票缺失作业量"))).reduce(BigDecimal.ZERO,BigDecimal::add));

                                        tPrdWorkTicketDetailDTO.setCanEdit(collect6.get(0).getCanEdit());

                                        result.getBottomList().add(tPrdWorkTicketDetailDTO);
                                    });
                                };
                                TPrdWorkTicketDetailDTO tPrdWorkTicketDetailDTO = new TPrdWorkTicketDetailDTO();
                                tPrdWorkTicketDetailDTO.setGroupId(new Long("3"));
                                tPrdWorkTicketDetailDTO.setCanEdit(workTicketDetailDTOS1.get(0).getCanEdit());

                                tPrdWorkTicketDetailDTO.setGroupKey(workTicketDetailDTOS1.get(0).getProcessDetailCode()+"_"+String.valueOf(workTicketDetailDTOS1.get(0).getWorkPositionCode()));
                                tPrdWorkTicketDetailDTO.setTon(workTicketDetailDTOS1.stream().map(o->Optional.ofNullable(o.getTon()).orElseThrow(()->new BusinessRuntimeException("辅助签票缺失作业量"))).reduce(BigDecimal.ZERO,BigDecimal::add));

                                result.getBottomList().add(tPrdWorkTicketDetailDTO);
                            }
                        });
                        TPrdWorkTicketDetailDTO tPrdWorkTicketDetailDTO = new TPrdWorkTicketDetailDTO();
                        tPrdWorkTicketDetailDTO.setGroupId(new Long("4"));
                        tPrdWorkTicketDetailDTO.setCanEdit(workTicketDetailDTOS.get(0).getCanEdit());

                        tPrdWorkTicketDetailDTO.setGroupKey(workTicketDetailDTOS.get(0).getProcessDetailCode());
                        tPrdWorkTicketDetailDTO.setTon(workTicketDetailDTOS.stream().map(o->Optional.ofNullable(o.getTon()).orElseThrow(()->new BusinessRuntimeException("辅助签票缺失作业量"))).reduce(BigDecimal.ZERO,BigDecimal::add));
                        result.getBottomList().add(tPrdWorkTicketDetailDTO);
                    }
                }
            }

            //回填签票子表
        }else if(FOR_LABOR.equals(queryDTO.getAllotType())){

                Map<Long, List<TPrdWorkTicketDetailDTO>> collect6 = ticketInfoList.stream().collect(Collectors.groupingBy(o -> o.getTrustId()));
                List<TPrdWorkTicketDetailDTO> tmpBoottmResult = new ArrayList<>();

                for (Long l : collect6.keySet()) {
                    List<TPrdWorkTicketDetailDTO> workTicketDetailDTOS_trust = collect6.get(l);

                    Map<String, List<TPrdWorkTicketDetailDTO>> topTicketMap =
                            workTicketDetailDTOS_trust.stream().collect(Collectors.groupingBy(
                                    o -> o.getProcessDetailCode()
                                            + "_" + o.getDeptId().toString()
                                            + "_" + o.getWorkPositionCode()));
                    Map<String, List<TPrdWorkTicketDetailDTO>> collect4 = workTicketDetailDTOS_trust.stream().collect(Collectors.groupingBy(
                            o -> o.getProcessDetailCode() + "_" + o.getWorkPositionCode()));
                    HashMap<String, TPrdWorkTicketDetailDTO> topResultMap = new HashMap<>();
                    for (String s : topTicketMap.keySet()) {
                        List<TPrdWorkTicketDetailDTO> workTicketDetailDTOS = topTicketMap.get(s);
                        TPrdWorkTicketDetailDTO tmpDto = new TPrdWorkTicketDetailDTO();
                        tmpDto.setDeptId(workTicketDetailDTOS.get(0).getDeptId());
                        tmpDto.setDeptName(workTicketDetailDTOS.get(0).getDeptName());
                        tmpDto.setProcessDetailName(workTicketDetailDTOS.get(0).getProcessDetailName());
                        tmpDto.setProcessDetailCode(workTicketDetailDTOS.get(0).getProcessDetailCode());
                        tmpDto.setProcessCode(workTicketDetailDTOS.get(0).getProcessCode());
                        tmpDto.setProcessName(workTicketDetailDTOS.get(0).getProcessName());
                        tmpDto.setWorkPositionName(workTicketDetailDTOS.get(0).getWorkPositionName());
                        tmpDto.setWorkPositionCode(workTicketDetailDTOS.get(0).getWorkPositionCode());
                        tmpDto.setTrustNo(workTicketDetailDTOS.get(0).getTrustNo());

                        tmpDto.setTon(workTicketDetailDTOS.stream().map(o -> o.getTon() == null ? BigDecimal.ZERO : o.getTon())
                                .reduce(BigDecimal.ZERO, BigDecimal::add));
                        //统计签票总量
                        List<TPrdWorkTicketDetailDTO> workTicketDetailDTOS1 = collect4.get(topTicketMap.get(s).stream().findFirst().orElseThrow(null)
                                .getProcessDetailCode() + "_" + topTicketMap.get(s).stream().findFirst().orElseThrow(null).getWorkPositionCode());

                        List<Long> collect = ticketMapDecimal.keySet().stream().filter(o ->
                                workTicketDetailDTOS1.stream().map(TPrdWorkTicketDetailPO::getWorkTicketId).filter(Objects::nonNull).collect(Collectors.toList()).stream().anyMatch(v1 -> v1.equals(o))
                        ).collect(Collectors.toList());
                        ArrayList<BigDecimal> bigDecimals = new ArrayList<>();
                        for (Long k1 : collect) {
                            bigDecimals.add(ticketMapDecimal.get(k1));
                        }
                        tmpDto.setTmpTopTon(bigDecimals.stream().reduce(BigDecimal.ZERO,BigDecimal::add));
                        topResultMap.put(s,tmpDto);
                        result.getTopCollect().add(tmpDto);
                    }
//                    result.setTopCollect(new ArrayList<>(topResultMap.values()));
                    //回填底部集合子表

                    Map<String, List<TPrdWorkTicketDetailDTO>> collect1 =
                            workTicketDetailDTOS_trust.stream().collect(Collectors.groupingBy(TPrdWorkTicketDetailPO::getProcessDetailCode));
                    collect1.keySet().stream().sorted(Comparator.comparing(o->o)).forEach(k1->
                    {
                        Map<String, List<TPrdWorkTicketDetailDTO>> collect2 = collect1.get(k1).stream().collect(Collectors.groupingBy(tmpItem->Optional.ofNullable(tmpItem.getWorkPositionCode()).orElse("null")));
                        for (String k2 : collect2.keySet().stream().sorted().collect(Collectors.toList())) {
                            Map<String, List<TPrdWorkTicketDetailDTO>> collect3 = collect2.get(k2).stream().collect(Collectors.groupingBy(o -> o.getDeptId().toString() ));
                            collect3.keySet().stream().sorted().collect(Collectors.toList()).forEach(k3->{
                                tmpBoottmResult.addAll(collect3.get(k3).stream().sorted(Comparator.comparing(o->o.getWorkDate().toString()+"_"+o.getClassCode(),Comparator.reverseOrder())).collect(Collectors.toList()));
                                TPrdWorkTicketDetailDTO tPrdWorkTicketDetailDTO = new TPrdWorkTicketDetailDTO();
                                tPrdWorkTicketDetailDTO.setTon(collect3.get(k3).stream().map(o->Optional.of(o.getTon()).orElseThrow(()-> new BusinessRuntimeException("没有获取到吨数"))).reduce(BigDecimal.ZERO,BigDecimal::add));
                                tPrdWorkTicketDetailDTO.setGroupId(new Long("2"));
                                tPrdWorkTicketDetailDTO.setGroupKey(
                                        collect3.get(k3).stream().findFirst().orElse(new TPrdWorkTicketDetailDTO()).getProcessDetailCode() + "_"
                                                + String.valueOf(collect3.get(k3).stream().findFirst().orElse(new TPrdWorkTicketDetailDTO()).getWorkPositionCode())
                                                + "_"
                                                + collect3.get(k3).stream().findFirst().orElse(new TPrdWorkTicketDetailDTO()).getDeptId()
                                                + "_"
                                                + collect3.get(k3).stream().findFirst().orElse(new TPrdWorkTicketDetailDTO()).getTrustNo()
                                );
                                tPrdWorkTicketDetailDTO.setCanEdit(workTicketDetailDTOS_trust.get(0).getCanEdit());
                                tmpBoottmResult.add(tPrdWorkTicketDetailDTO);
                            });
                            TPrdWorkTicketDetailDTO tPrdWorkTicketDetailDTO = new TPrdWorkTicketDetailDTO();

                            tPrdWorkTicketDetailDTO.setTon(collect2.get(k2).stream().map(o->Optional.ofNullable(o.getTon()).orElse(BigDecimal.ZERO)).reduce(BigDecimal.ZERO,BigDecimal::add));
                            tPrdWorkTicketDetailDTO.setGroupId(new Long("3"));
                            tPrdWorkTicketDetailDTO.setGroupKey(
                                    collect2.get(k2).stream().findFirst().orElse(new TPrdWorkTicketDetailDTO()).getProcessDetailCode() + "_"
                                            +String.valueOf(collect2.get(k2).stream().findFirst().orElse(new TPrdWorkTicketDetailDTO()).getWorkPositionCode())
                                            + "_"
                                            +String.valueOf(collect2.get(k2).stream().findFirst().orElse(new TPrdWorkTicketDetailDTO()).getTrustNo())
                            );
                            tPrdWorkTicketDetailDTO.setCanEdit(workTicketDetailDTOS_trust.get(0).getCanEdit());

                            tmpBoottmResult.add(tPrdWorkTicketDetailDTO);
                        }
                        TPrdWorkTicketDetailDTO tPrdWorkTicketDetailDTO = new TPrdWorkTicketDetailDTO();
                        //统计签票总量
                        ticketMapDecimal.keySet().stream().filter(item->
                                collect1.get(k1).stream().map(TPrdWorkTicketDetailPO::getWorkTicketId
                                ).filter(Objects::nonNull).collect(Collectors.toList()).stream().anyMatch(item1-> item1.equals(item))
                        ).forEach(item->
                                tPrdWorkTicketDetailDTO.setTon(Optional.ofNullable(tPrdWorkTicketDetailDTO.getTon()).orElse(BigDecimal.ZERO).add(ticketMapDecimal.get(item))));
                        tPrdWorkTicketDetailDTO.setGroupId(new Long("4"));
                        tPrdWorkTicketDetailDTO.setCanEdit(workTicketDetailDTOS_trust.get(0).getCanEdit());

                        tPrdWorkTicketDetailDTO.setGroupKey(
                                collect1.get(k1).stream().findFirst().orElse(new TPrdWorkTicketDetailDTO()).getProcessDetailCode()
                                        +"_"+collect1.get(k1).stream().findFirst().orElse(new TPrdWorkTicketDetailDTO()).getTrustNo()
                        );
                        tmpBoottmResult.add(tPrdWorkTicketDetailDTO);
                    });
                    TPrdWorkTicketDetailDTO tPrdWorkTicketDetailDTO = new TPrdWorkTicketDetailDTO();
                    //统计签票总量
                    workTicketList.stream().filter(o->workTicketDetailDTOS_trust.stream().anyMatch(v->v.getWorkTicketId().equals(o.getWorkTicketId()))).forEach(item->
                            tPrdWorkTicketDetailDTO.setTon(
                                    Optional.ofNullable(tPrdWorkTicketDetailDTO.getTon()).orElse(BigDecimal.ZERO).add(item.getTon())
                            )
                    );
                    tPrdWorkTicketDetailDTO.setGroupId(new Long("5"));
                    tPrdWorkTicketDetailDTO.setGroupKey(
                            workTicketDetailDTOS_trust.stream().findAny().orElse(new TPrdWorkTicketDetailDTO()).getTrustNo()
                    );
                    tPrdWorkTicketDetailDTO.setCanEdit(workTicketDetailDTOS_trust.get(0).getCanEdit());
                    tmpBoottmResult.add(tPrdWorkTicketDetailDTO);

                }
                result.setBottomList(tmpBoottmResult);
                Map<String, List<TPrdWorkTicketDetailDTO>> collect5 = ticketInfoFuZhuList.stream().collect(
                        Collectors.groupingBy(o -> o.getProcessDetailCode()
                                + "_" + String.valueOf(o.getWorkPositionCode() )
                                + "_" +String.valueOf(Optional.ofNullable(o.getDeptId()
                        ).orElseThrow(()->new BusinessRuntimeException("辅助签票缺少签票部门")))));
                collect5.keySet().stream().sorted().forEach(tmpK->{
                    TPrdWorkTicketDetailDTO tPrdWorkTicketDetailDTO = new TPrdWorkTicketDetailDTO();
                    tPrdWorkTicketDetailDTO.setProcessDetailCode(collect5.get(tmpK).get(0).getProcessDetailCode());
                    tPrdWorkTicketDetailDTO.setProcessDetailName(collect5.get(tmpK).get(0).getProcessDetailName());
                    tPrdWorkTicketDetailDTO.setDeptName(collect5.get(tmpK).get(0).getDeptName());
                    tPrdWorkTicketDetailDTO.setTon(collect5.get(tmpK).stream().map(o->Optional.ofNullable(o.getTon()).orElse(BigDecimal.ZERO)).reduce(BigDecimal.ZERO,BigDecimal::add));
                    tPrdWorkTicketDetailDTO.setTmpTopTon(collect5.get(tmpK).stream().map(o->Optional.ofNullable(o.getTon()).orElse(BigDecimal.ZERO)).reduce(BigDecimal.ZERO,BigDecimal::add));

                    result.getTopCollect().add(tPrdWorkTicketDetailDTO);

                });



                Map<String, List<TPrdWorkTicketDetailDTO>> collect =
                        ticketInfoFuZhuList.stream().collect(Collectors.groupingBy(o -> o.getProcessDetailCode()));
                for (String s : collect.keySet().stream().sorted().collect(Collectors.toList())) {
                    List<TPrdWorkTicketDetailDTO> workTicketDetailDTOS = collect.get(s);
                    if (workTicketDetailDTOS!=null&&!workTicketDetailDTOS.isEmpty()){
                        Map<String, List<TPrdWorkTicketDetailDTO>> collect2 = workTicketDetailDTOS.stream().collect(Collectors.groupingBy(tmpk -> String.valueOf(tmpk.getWorkPositionCode())));
                        for (String K3 : collect2.keySet().stream().sorted().collect(Collectors.toList())) {
                            if(collect2.get(K3)!=null&&!collect2.get(K3).isEmpty()){
                                List<TPrdWorkTicketDetailDTO> workTicketDetailDTOS1 = collect2.get(K3);
                                Map<Long, List<TPrdWorkTicketDetailDTO>> collect3 = workTicketDetailDTOS1.stream().collect(Collectors.groupingBy(tmpK4 ->Optional.ofNullable(tmpK4.getDeptId()).orElseThrow(()->new BusinessRuntimeException("辅助签票缺少签票部门")) ));
                                collect3.keySet().stream().sorted().forEach(tmpk4->{
                                    List<TPrdWorkTicketDetailDTO> workTicketDetailDTOS2 = collect3.get(tmpk4);
                                    if(workTicketDetailDTOS2!=null&&!workTicketDetailDTOS2.isEmpty()){
                                        result.getBottomList().addAll(workTicketDetailDTOS2.stream().sorted(Comparator.comparing(tmpk5->tmpk5.getWorkDate()+"_"+tmpk5.getClassCode())).collect(Collectors.toList()));
                                        TPrdWorkTicketDetailDTO tPrdWorkTicketDetailDTO = new TPrdWorkTicketDetailDTO();
                                        tPrdWorkTicketDetailDTO.setGroupId(new Long("2"));
                                        tPrdWorkTicketDetailDTO.setGroupKey(workTicketDetailDTOS2.get(0).getProcessDetailCode()
                                                +"_"+String.valueOf(workTicketDetailDTOS2.get(0).getWorkPositionCode())
                                                +"_"+workTicketDetailDTOS2.get(0).getDeptId());
                                        tPrdWorkTicketDetailDTO.setTon(workTicketDetailDTOS2.stream().map(o->o.getTon()).reduce(BigDecimal.ZERO,BigDecimal::add));
                                        tPrdWorkTicketDetailDTO.setCanEdit(workTicketDetailDTOS2.get(0).getCanEdit());

                                        result.getBottomList().add(tPrdWorkTicketDetailDTO);
                                    }
                                });

                                TPrdWorkTicketDetailDTO tPrdWorkTicketDetailDTO = new TPrdWorkTicketDetailDTO();
                                tPrdWorkTicketDetailDTO.setGroupId(new Long("3"));
                                tPrdWorkTicketDetailDTO.setCanEdit(workTicketDetailDTOS1.get(0).getCanEdit());

                                tPrdWorkTicketDetailDTO.setTon(workTicketDetailDTOS1.stream().map(o->o.getTon()).reduce(BigDecimal.ZERO,BigDecimal::add));
                                tPrdWorkTicketDetailDTO.setGroupKey(workTicketDetailDTOS1.get(0).getProcessDetailCode()+"_"+String.valueOf(workTicketDetailDTOS1.get(0).getWorkPositionCode()));
                                result.getBottomList().add(tPrdWorkTicketDetailDTO);
                            }
                        }
                        TPrdWorkTicketDetailDTO tPrdWorkTicketDetailDTO = new TPrdWorkTicketDetailDTO();
                        tPrdWorkTicketDetailDTO.setGroupId(new Long("4"));
                        tPrdWorkTicketDetailDTO.setCanEdit(workTicketDetailDTOS.get(0).getCanEdit());
                        tPrdWorkTicketDetailDTO.setGroupKey(workTicketDetailDTOS.get(0).getProcessDetailCode());
                        tPrdWorkTicketDetailDTO.setTon(workTicketDetailDTOS.stream().map(o->Optional.ofNullable(o.getTon()).orElseThrow(()->new BusinessRuntimeException("辅助签票缺少签票作业量"))).reduce(BigDecimal.ZERO,BigDecimal::add));
                        result.getBottomList().add(tPrdWorkTicketDetailDTO);
                    }
                }
            }
        }
        if(result.getTopCollect()!=null && !result.getTopCollect().isEmpty()){
            result.setTopCollect(result.getTopCollect().stream().sorted(Comparator.comparing(o->
                    o.getTrustNo()+"_"+o.getProcessDetailCode()+"_"+o.getWorkPositionCode()
            )).collect(Collectors.toList()));
        }
        return result;
    }

    /**
     * 整船调整 更新
     * @param shipvoyageItemId
     * @param reqList
     */
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public void updateTickeyDetail(Long shipvoyageItemId ,List<TPrdWorkTicketDetailDTO> reqList) {
        TWholeShipAdjustmentQueryDTO queryDTO = new TWholeShipAdjustmentQueryDTO();
        queryDTO.setShipVoyageItemId(shipvoyageItemId);
        List<Map<Long,String>> shipInfo = mapper.getShipInfo(queryDTO);
        if(shipInfo == null){
            throw new BusinessRuntimeException("船名航次的不存在");
        }
        List<String> dynamicTypeCodeList = shipInfo.stream().map(o -> String.valueOf(o.get("dynamicTypeCode"))).collect(Collectors.toList());
        if(!dynamicTypeCodeList.stream().anyMatch(o->o.equals(ShipStatusEnum.WANGONG.getCode()))){
            throw new BusinessRuntimeException("船舶尚未完工");
        }
        //过滤前端数据中的合计小计行
        reqList = reqList.stream().filter(o -> o.getId() != null && o.getCanEdit()).collect(Collectors.toList());
        reqList.forEach(detailDTO->{
            if(detailDTO.getTon() == null){
                throw new BusinessRuntimeException("吨数不能为空");
            }
            if(detailDTO.getWorkTicketId()==null){
                throw new BusinessRuntimeException("缺少签票主表ID");
            }
            if(detailDTO.getWorkPlanId()==null){
                throw new BusinessRuntimeException("缺少计划id");
            }
//            if(detailDTO.getCompanyId()==null){
//                throw new BusinessRuntimeException("缺少作业公司信息");
//            }
//            if(detailDTO.getCompanyName()==null){
//                throw new BusinessRuntimeException("缺少作业公司信息");
//            }
        });

        if (reqList.isEmpty()){
            throw new BusinessRuntimeException("没有要更新的数据");
        }
        //检验计件是否已经被审核
        List<TPrdSalaryPO> detailSalarys = workTicketMapper.getSalaryByTicketDetial(reqList.stream().map(TPrdWorkTicketDetailDTO::getId).collect(Collectors.toList()));
        List<com.yy.ppm.produce.bean.po.TPrdSalaryPO> salaryCheckList = detailSalarys.stream().filter(o -> !"10".equals(o.getSalaryStatusCode())).collect(Collectors.toList());
        if (!salaryCheckList.isEmpty()) {
            throw new BusinessRuntimeException("计件已审核，请先撤销计件。");
        }

        SysParameterDTO sysParameter = sysParameterMapper.getByKey("ALLOT_FUDONG_LIANG");
        SysParameterDTO ship_adjust_check = sysParameterMapper.getByKey("ship_adjust_check");
        List<TPrdWorkTicketDetailDTO> fuZhuReqList = reqList.stream().filter(o->FOR_TICKET.equals(o.getAllotType())).collect(Collectors.toList());
        List<TPrdWorkTicketDetailDTO> workTicketListFuZhu = new ArrayList<>();
        Map<Long, List<TPrdWorkTicketDetailDTO>> tikcetMapFuzhu = new HashMap<>();
        if(!fuZhuReqList.isEmpty()){
            workTicketListFuZhu = mapper.getWorkTicketListForFuZhu(fuZhuReqList.stream().map(o -> Optional.ofNullable(o.getWorkTicketId()).orElseThrow(() -> new BusinessRuntimeException("缺少签票主信息"))).collect(Collectors.toList()));
            workTicketListFuZhu = workTicketListFuZhu.stream().filter(o->fuZhuReqList.stream().map(TPrdWorkTicketDetailPO::getId).filter(Objects::nonNull).anyMatch(tmp->tmp.equals(o.getTicketDetailId()))).collect(Collectors.toList());
            tikcetMapFuzhu = workTicketListFuZhu.stream().collect(Collectors.groupingBy(TPrdWorkTicketDetailPO::getWorkTicketId));

            if("Y".equals(ship_adjust_check.getParamVal())){
                List<TPrdWorkTicketDetailDTO> workTicketList = mapper.getWorkTicketListForFuZhu(fuZhuReqList.stream().map(o -> Optional.ofNullable(o.getWorkTicketId()).orElseThrow(() -> new BusinessRuntimeException("缺少签票主信息"))).collect(Collectors.toList()));
                workTicketList = workTicketList.stream().filter(
                        o->fuZhuReqList.stream().map(TPrdWorkTicketDetailPO::getId)
                                .filter(Objects::nonNull).anyMatch(tmp->tmp.equals(o.getTicketDetailId()))
                    ).collect(Collectors.toList()
                );
                Map<String, List<TPrdWorkTicketDetailDTO>> collect1 = workTicketList.stream().collect(Collectors.groupingBy(o -> o.getProcessDetailCode() + "_" + String.valueOf(o.getWorkPositionCode())));
                Map<String, List<TPrdWorkTicketDetailDTO>> collect = fuZhuReqList.stream().collect(Collectors.groupingBy(o -> o.getProcessDetailCode() + "_" + String.valueOf(o.getWorkPositionCode())));
                collect.forEach((k,v)->{
                    if(v!=null&&v.size()>0){
                        System.out.println(v.stream().map(o -> Optional.ofNullable(o.getTon()).orElseThrow(() -> new BusinessRuntimeException("重量获取失败")))
                                .reduce(BigDecimal.ZERO, BigDecimal::add));
                        if(v.stream().map(o->Optional.ofNullable(o.getTon()).orElseThrow(()->new BusinessRuntimeException("重量获取失败")))
                                .reduce(BigDecimal.ZERO,BigDecimal::add)
                                .subtract(collect1.get(k).stream().map(
                                        o->Optional.ofNullable(o.getTon()).orElseThrow(()->new BusinessRuntimeException("重量获取失败2")))
                                        .reduce(BigDecimal.ZERO,BigDecimal::add)).compareTo(new BigDecimal(sysParameter.getParamVal()))>0){
                            throw new BusinessRuntimeException("辅助计划 超量! </br>"+v.get(0).getProcessDetailName()+":"+String.valueOf(v.get(0).getWorkPositionCode()));
                        }
                    }
                });
            }
            mapper.updateTicketDetailBatch(fuZhuReqList);
        }
        //校验超量

        List<TPrdWorkTicketDetailDTO> workTicketList = mapper.getWorkTicketList(reqList.stream().map(o -> Optional.ofNullable(o.getWorkTicketId()).orElseThrow(() -> new BusinessRuntimeException("缺少签票主信息"))).collect(Collectors.toList()));
        Map<Long, List<TPrdWorkTicketDetailDTO>> tikcetMap = workTicketList.stream().collect(Collectors.groupingBy(TPrdWorkTicketDetailPO::getId));
        Map<String, List<TPrdWorkTicketDetailDTO>> collect5 = reqList.stream().collect(Collectors.groupingBy(o -> o.getProcessDetailCode() + "_" + String.valueOf(o.getWorkPositionCode())));

        if("Y".equals(ship_adjust_check.getParamVal())){
            collect5.keySet().forEach(col5->{
                List<TPrdWorkTicketDetailDTO> workTicketDetailDTOS = collect5.get(col5);
                if(workTicketDetailDTOS==null||workTicketDetailDTOS.isEmpty()){

                }else{
                    Map<String, List<TPrdWorkTicketDetailDTO>> collect =
                            workTicketDetailDTOS.stream().collect(Collectors.groupingBy(o -> String.valueOf(o.getWorkPositionCode())));
                    for (String s : collect.keySet()) {
                        if(
                                collect.get(s).stream().map(v->v.getTon())
                                        .reduce(BigDecimal.ZERO,BigDecimal::add)
                                        .subtract(workTicketList.stream().filter(v2->collect.get(s).stream()
                                                .map(v->v.getWorkTicketId()).collect(Collectors.toList())
                                                .stream().anyMatch(v->v.equals(v2.getWorkTicketId()))).map(v->v.getTon()).reduce(BigDecimal.ZERO,BigDecimal::add)
                                ).compareTo(new BigDecimal(sysParameter.getParamVal()))>0
                        ){
                            throw new BusinessRuntimeException("超量! </br>"+collect.get(s).stream().findFirst().orElseThrow(null).getProcessDetailName());
                        }
                    }
                }
            });

        }


        //更新船舶状态为整船调整
        if(FOR_MACHINE.equals(reqList.stream().findFirst().orElseThrow(null).getAllotType())){
            mapper.updateShipStatus(shipvoyageItemId,"20");

        }else if (FOR_LABOR.equals(reqList.stream().findFirst().orElseThrow(null).getAllotType())){
            mapper.updateShipStatusForLabor(shipvoyageItemId,"20");
        }else {
            throw new BusinessRuntimeException("调整类型参数错误！");
        }

        mapper.updateTicketDetailBatch(reqList);


        reqList.addAll(fuZhuReqList);
        Map<Long, List<TPrdWorkTicketDetailDTO>> collect = reqList.stream().collect(Collectors.groupingBy(o -> o.getWorkPlanId()));
        for (Long l : collect.keySet()) {
            List<TPrdWorkTicketDetailDTO> workTicketDetailDTOS = collect.get(l);
            if(workTicketDetailDTOS.isEmpty()){

            }else{
                Map<Long, List<TPrdWorkTicketDetailDTO>> collect1 = workTicketDetailDTOS.stream().collect(Collectors.groupingBy(o -> o.getWorkTicketId()));
                List<Long> collect2 = new ArrayList<>(collect1.keySet());
                List<TPrdWorkTicketDetailDTO> tmpWoekTicket= new ArrayList<>();
                for (Long aLong : tikcetMap.keySet()) {
                    if(collect2.stream().anyMatch(o->o.equals(aLong))){
                        tmpWoekTicket.addAll( tikcetMap.get(aLong));
                    }
                }
                Map<Object, List<TPrdWorkTicketDetailDTO>> collect3 = tmpWoekTicket.stream().collect(Collectors.groupingBy(o -> o.getWorkTicketId()));

                for (Long aLong : collect1.keySet()) {
                    List<TPrdWorkTicketDetailDTO> workTicketDetailDTOS1 = collect1.get(aLong);
                    for (TPrdWorkTicketDetailDTO tPrdWorkTicketDetailDTO : workTicketDetailDTOS1) {
                        tPrdWorkTicketDetailDTO.setCargoCode(collect3.get(aLong).stream().findFirst().orElseThrow(()-> new BusinessRuntimeException("获取签票信息失败")).getCargoCode());
                        tPrdWorkTicketDetailDTO.setCargoName(collect3.get(aLong).stream().findFirst().orElseThrow(()-> new BusinessRuntimeException("获取签票信息失败")).getCargoName());
                    }
                    TPrdWorkTicketDTO tPrdWorkTicketDTO = new TPrdWorkTicketDTO();
                    tPrdWorkTicketDTO.setId(workTicketDetailDTOS1.get(0).getWorkTicketId());
                    tPrdWorkTicketDTO.setWorkPlanId(workTicketDetailDTOS1.get(0).getWorkPlanId());
                    tPrdWorkTicketDTO.setWorkDate(workTicketDetailDTOS1.get(0).getWorkDate());
                    tPrdWorkTicketDTO.setClassCode(workTicketDetailDTOS1.get(0).getClassCode());
                    tPrdWorkTicketDTO.setClassName(workTicketDetailDTOS1.get(0).getClassName());
                    tPrdWorkTicketDTO.setCompanyId(workTicketDetailDTOS1.get(0).getCompanyId());
                    tPrdWorkTicketDTO.setCompanyName(workTicketDetailDTOS1.get(0).getCompanyName());
                    //调用计件公共方法
                    SpringUtils.getBean(TPrdWorkTicketNewServiceImpl.class).commonUpdateSalary(tPrdWorkTicketDTO,workTicketDetailDTOS1,true,false,"整船调整");
                }
            }
        }
    }

    /**
     * 整船调整 更新
     * @param shipvoyageItemId
     * @param reqList
     */
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public void updateTickeyDetail1128(Long shipvoyageItemId ,List<TPrdWorkTicketDetailDTO> reqList) {
        TWholeShipAdjustmentQueryDTO queryDTO = new TWholeShipAdjustmentQueryDTO();
        queryDTO.setShipVoyageItemId(shipvoyageItemId);
        List<Map<Long,String>> shipInfo = mapper.getShipInfo(queryDTO);
        if(shipInfo == null){
            throw new BusinessRuntimeException("船名航次的不存在");
        }
        List<String> dynamicTypeCodeList = shipInfo.stream().map(o -> String.valueOf(o.get("dynamicTypeCode"))).collect(Collectors.toList());
        if(!dynamicTypeCodeList.stream().anyMatch(o->o.equals(ShipStatusEnum.WANGONG.getCode()))){
            throw new BusinessRuntimeException("船舶尚未完工");
        }
        if(shipInfo.get(0).get("loadUnload").equals("卸")) {
            if (StringUtils.isNotBlank(String.valueOf(shipInfo.get(0).get("isShipClear")))) {
                if ("1".equals(String.valueOf(shipInfo.get(0).get("isShipClear")))) {

                } else {
                    throw new BusinessRuntimeException("船舶尚未完工确认");
                }
            } else {
                throw new BusinessRuntimeException("船舶尚未完工确认");
            }
        }
        //过滤前端数据中的合计小计行
        reqList = reqList.stream().filter(o -> o.getId() != null).collect(Collectors.toList());
        reqList.forEach(detailDTO->{
            if(detailDTO.getTon() == null){
                throw new BusinessRuntimeException("吨数不能为空");
            }
            if(detailDTO.getWorkTicketId()==null){
                throw new BusinessRuntimeException("缺少签票主表ID");
            }
            if(detailDTO.getWorkPlanId()==null){
                throw new BusinessRuntimeException("缺少计划id");
            }
//            if(detailDTO.getCompanyId()==null){
//                throw new BusinessRuntimeException("缺少作业公司信息");
//            }
//            if(detailDTO.getCompanyName()==null){
//                throw new BusinessRuntimeException("缺少作业公司信息");
//            }
        });

//        reqList.stream().forEach(o-> System.out.println(o.getCanEdit()));
        //checklist 只校验当月没有hr审核的数据是否存在审核的计件  因为集疏运直取签票的特殊性，流机队等会出现在整船调整中，从而影响计件
        /**
         * 整船调整对于跨月份的船的处理：
         * 每条签票数据上有canEdit标志位（Boolean），canEdit标志位标记这个数据所在的月份是否存在已经hr审核的数据
         * canEdit为true时  该数据参与是否超量的校验，参与数据更新，参与计件分配
         * canEdit为False时 该数据参与是否超量的校验，不参与数据更新，不参与计件分配
         */
        List<TPrdWorkTicketDetailDTO> checkList = reqList.stream().filter(o -> o.getCanEdit()).collect(Collectors.toList());
        if (reqList.isEmpty()){
            throw new BusinessRuntimeException("没有要更新的数据");
        }
        //检验计件是否已经被审核
        List<TPrdSalaryPO> detailSalarys = workTicketMapper.getSalaryByTicketDetial(checkList.stream().map(TPrdWorkTicketDetailDTO::getId).collect(Collectors.toList()));
        List<com.yy.ppm.produce.bean.po.TPrdSalaryPO> salaryCheckList = detailSalarys.stream().filter(o -> !"10".equals(o.getSalaryStatusCode())).collect(Collectors.toList());
        if (!salaryCheckList.isEmpty()) {
            throw new BusinessRuntimeException("计件已审核，请先撤销计件。");
        }

        SysParameterDTO sysParameter = sysParameterMapper.getByKey("ALLOT_FUDONG_LIANG");
        SysParameterDTO ship_adjust_check = sysParameterMapper.getByKey("ship_adjust_check");
        List<TPrdWorkTicketDetailDTO> fuZhuReqList = reqList.stream().filter(o->FOR_TICKET.equals(o.getAllotType())).collect(Collectors.toList());

        List<TPrdWorkTicketDetailDTO> workTicketListFuZhu = new ArrayList<>();
        Map<Long, List<TPrdWorkTicketDetailDTO>> tikcetMapFuzhu = new HashMap<>();
        if(!fuZhuReqList.isEmpty()){
            workTicketListFuZhu = mapper.getWorkTicketListForFuZhu(fuZhuReqList.stream().map(o -> Optional.ofNullable(o.getWorkTicketId()).orElseThrow(() -> new BusinessRuntimeException("缺少签票主信息"))).collect(Collectors.toList()));
            List<TPrdWorkTicketDetailDTO> finalFuZhuReqList = fuZhuReqList;
            workTicketListFuZhu = workTicketListFuZhu.stream().filter(o-> finalFuZhuReqList.stream().map(TPrdWorkTicketDetailPO::getId).filter(Objects::nonNull).anyMatch(tmp->tmp.equals(o.getTicketDetailId()))).collect(Collectors.toList());
            tikcetMapFuzhu = workTicketListFuZhu.stream().collect(Collectors.groupingBy(TPrdWorkTicketDetailPO::getWorkTicketId));

            if("Y".equals(ship_adjust_check.getParamVal())){
                List<TPrdWorkTicketDetailDTO> workTicketList = mapper.getWorkTicketListForFuZhu(fuZhuReqList.stream().map(o -> Optional.ofNullable(o.getWorkTicketId()).orElseThrow(() -> new BusinessRuntimeException("缺少签票主信息"))).collect(Collectors.toList()));
                workTicketList = workTicketList.stream().filter(
                        o->finalFuZhuReqList.stream().map(TPrdWorkTicketDetailPO::getId)
                                .filter(Objects::nonNull).anyMatch(tmp->tmp.equals(o.getTicketDetailId()))
                ).collect(Collectors.toList()
                );
                Map<String, List<TPrdWorkTicketDetailDTO>> collect1 = workTicketList.stream().collect(Collectors.groupingBy(o -> o.getProcessDetailCode() + "_" + String.valueOf(o.getWorkPositionCode())));
                Map<String, List<TPrdWorkTicketDetailDTO>> collect = fuZhuReqList.stream().collect(Collectors.groupingBy(o -> o.getProcessDetailCode() + "_" + String.valueOf(o.getWorkPositionCode())));
                collect.forEach((k,v)->{
                    if(v!=null&&v.size()>0){
                        System.out.println(v.stream().map(o -> Optional.ofNullable(o.getTon()).orElseThrow(() -> new BusinessRuntimeException("重量获取失败")))
                                .reduce(BigDecimal.ZERO, BigDecimal::add));
                        if(v.stream().map(o->Optional.ofNullable(o.getTon()).orElseThrow(()->new BusinessRuntimeException("重量获取失败")))
                                .reduce(BigDecimal.ZERO,BigDecimal::add)
                                .subtract(collect1.get(k).stream().map(
                                                o->Optional.ofNullable(o.getTon()).orElseThrow(()->new BusinessRuntimeException("重量获取失败2")))
                                        .reduce(BigDecimal.ZERO,BigDecimal::add)).compareTo(new BigDecimal(sysParameter.getParamVal()))>0){
                            throw new BusinessRuntimeException("辅助计划 超量! </br>"+v.get(0).getProcessDetailName()+":"+String.valueOf(v.get(0).getWorkPositionCode()));
                        }
                    }
                });
            }
            mapper.updateTicketDetailBatch(fuZhuReqList);
        }
        //校验超量

        List<TPrdWorkTicketDetailDTO> workTicketList = mapper.getWorkTicketList(reqList.stream().map(o -> Optional.ofNullable(o.getWorkTicketId()).orElseThrow(() -> new BusinessRuntimeException("缺少签票主信息"))).collect(Collectors.toList()));
        Map<Long, List<TPrdWorkTicketDetailDTO>> tikcetMap = workTicketList.stream().collect(Collectors.groupingBy(TPrdWorkTicketDetailPO::getId));
        Map<String, List<TPrdWorkTicketDetailDTO>> collect5 =
                reqList.stream().filter(o->!FOR_TICKET.equals(o.getAllotType())).collect(Collectors.groupingBy(o -> o.getProcessDetailCode() + "_" + String.valueOf(o.getWorkPositionCode())));

        if("Y".equals(ship_adjust_check.getParamVal())){
            collect5.keySet().forEach(col5->{
                List<TPrdWorkTicketDetailDTO> workTicketDetailDTOS = collect5.get(col5);
                if(workTicketDetailDTOS==null||workTicketDetailDTOS.isEmpty()){

                }else{
                    Map<String, List<TPrdWorkTicketDetailDTO>> collect1 = workTicketDetailDTOS.stream().collect(Collectors.groupingBy(o -> o.getTrustNo()));
                    collect1.keySet().stream().forEach(o->{
                        List<TPrdWorkTicketDetailDTO> workTicketDetailDTOSTrust = collect1.get(o);
                        Map<String, List<TPrdWorkTicketDetailDTO>> collect =
                                workTicketDetailDTOSTrust.stream().collect(Collectors.groupingBy(vv -> String.valueOf(vv.getWorkPositionCode())));
                        for (String s : collect.keySet()) {
                            if(
                                    collect.get(s).stream().map(v->v.getTon()).reduce(BigDecimal.ZERO,BigDecimal::add).subtract(workTicketList.stream().filter(v2->collect.get(s).stream().map(v->v.getWorkTicketId()).collect(Collectors.toList()).stream().anyMatch(v->v.equals(v2.getWorkTicketId()))).map(v->v.getTon()).reduce(BigDecimal.ZERO,BigDecimal::add)
                                    ).compareTo(new BigDecimal(sysParameter.getParamVal()))>0
                            ){
                                throw new BusinessRuntimeException("超量! </br>"+
                                        collect.get(s).stream().findFirst().orElseThrow(null).getTrustNo()+"_"+
                                        collect.get(s).stream().findFirst().orElseThrow(null).getProcessDetailName());
                            }
                        }
                    });
                }
            });

        }


        //更新船舶状态为整船调整
        if(FOR_MACHINE.equals(reqList.stream().findFirst().orElseThrow(null).getAllotType())){
            mapper.updateShipStatus(shipvoyageItemId,"20");

        }else if (FOR_LABOR.equals(reqList.stream().findFirst().orElseThrow(null).getAllotType())){
            mapper.updateShipStatusForLabor(shipvoyageItemId,"20");
        }else {
            throw new BusinessRuntimeException("调整类型参数错误！");
        }
        List<TPrdWorkTicketDetailDTO> updateList = reqList.stream().filter(o -> o.getCanEdit()).collect(Collectors.toList());
        mapper.updateTicketDetailBatch(updateList);


        //正常的签票
        reqList= reqList.stream().filter(o->!FOR_TICKET.equals(o.getAllotType())&&o.getCanEdit()).collect(Collectors.toList());
        Map<Long, List<TPrdWorkTicketDetailDTO>> collect = new HashMap<>();
        if(!CollectionUtils.isEmpty(reqList)){
            collect = reqList.stream().collect(Collectors.groupingBy(o -> o.getWorkPlanId()));
            for (Long l : collect.keySet()) {
                List<TPrdWorkTicketDetailDTO> workTicketDetailDTOS = collect.get(l);
                if(workTicketDetailDTOS.isEmpty()){

                }else{
                    Map<Long, List<TPrdWorkTicketDetailDTO>> collect1 = workTicketDetailDTOS.stream().collect(Collectors.groupingBy(o -> o.getWorkTicketId()));
                    List<Long> collect2 = new ArrayList<>(collect1.keySet());
                    List<TPrdWorkTicketDetailDTO> tmpWoekTicket= new ArrayList<>();
                    for (Long aLong : tikcetMap.keySet()) {
                        if(collect2.stream().anyMatch(o->o.equals(aLong))){
                            tmpWoekTicket.addAll( tikcetMap.get(aLong));
                        }
                    }
                    Map<Object, List<TPrdWorkTicketDetailDTO>> collect3 = tmpWoekTicket.stream().collect(Collectors.groupingBy(o -> o.getWorkTicketId()));

                    for (Long aLong : collect1.keySet()) {
                        List<TPrdWorkTicketDetailDTO> workTicketDetailDTOS1 = collect1.get(aLong);
                        for (TPrdWorkTicketDetailDTO tPrdWorkTicketDetailDTO : workTicketDetailDTOS1) {
                            tPrdWorkTicketDetailDTO.setCargoCode(collect3.get(aLong).stream().findFirst().orElseThrow(()-> new BusinessRuntimeException("获取签票信息失败")).getCargoCode());
                            tPrdWorkTicketDetailDTO.setCargoName(collect3.get(aLong).stream().findFirst().orElseThrow(()-> new BusinessRuntimeException("获取签票信息失败")).getCargoName());
                        }
                        TPrdWorkTicketDTO tPrdWorkTicketDTO = new TPrdWorkTicketDTO();
                        tPrdWorkTicketDTO.setId(workTicketDetailDTOS1.get(0).getWorkTicketId());
                        tPrdWorkTicketDTO.setWorkPlanId(workTicketDetailDTOS1.get(0).getWorkPlanId());
                        tPrdWorkTicketDTO.setWorkDate(workTicketDetailDTOS1.get(0).getWorkDate());
                        tPrdWorkTicketDTO.setClassCode(workTicketDetailDTOS1.get(0).getClassCode());
                        tPrdWorkTicketDTO.setClassName(workTicketDetailDTOS1.get(0).getClassName());
                        tPrdWorkTicketDTO.setCompanyId(workTicketDetailDTOS1.get(0).getCompanyId());
                        tPrdWorkTicketDTO.setCompanyName(workTicketDetailDTOS1.get(0).getCompanyName());
                        //调用计件公共方法
                        SpringUtils.getBean(TPrdWorkTicketNewServiceImpl.class).commonUpdateSalary(tPrdWorkTicketDTO,workTicketDetailDTOS1,true,false,"整船调整");
                    }
                }
            }
        }


        //辅助计划签票

        if(!fuZhuReqList.isEmpty()){

            fuZhuReqList = fuZhuReqList.stream().filter(o -> o.getCanEdit()).collect(Collectors.toList());
            if(!CollectionUtils.isEmpty(fuZhuReqList)){
            collect = fuZhuReqList.stream().filter(o->o.getCanEdit()).collect(Collectors.groupingBy(o -> o.getWorkPlanId()));
            for (Long l : collect.keySet()) {
                List<TPrdWorkTicketDetailDTO> workTicketDetailDTOS = collect.get(l);
                if(workTicketDetailDTOS.isEmpty()){

                }else{
                    Map<Long, List<TPrdWorkTicketDetailDTO>> collect1 =
                            workTicketDetailDTOS.stream().collect(Collectors.groupingBy(o -> o.getWorkTicketId()));

                    for (Long aLong : collect1.keySet()) {
                        List<TPrdWorkTicketDetailDTO> workTicketDetailDTOS1 = collect1.get(aLong);
                        for (TPrdWorkTicketDetailDTO tPrdWorkTicketDetailDTO : workTicketDetailDTOS1) {
                            tPrdWorkTicketDetailDTO.setCargoCode(
                                    workTicketListFuZhu.stream().filter(o->o.getWorkTicketId().equals(aLong)).findFirst().orElseThrow(()-> new BusinessRuntimeException("获取签票信息失败")
                                    ).getCargoCode()
                            );
                            tPrdWorkTicketDetailDTO.setCargoName(
                                    workTicketListFuZhu.stream().filter(o->o.getWorkTicketId().equals(aLong)).findFirst().orElseThrow(()-> new BusinessRuntimeException("获取签票信息失败")
                                    ).getCargoName()
                            );
                        }
                        TPrdWorkTicketDTO tPrdWorkTicketDTO = new TPrdWorkTicketDTO();
                        tPrdWorkTicketDTO.setId(workTicketDetailDTOS1.get(0).getWorkTicketId());
                        tPrdWorkTicketDTO.setWorkPlanId(workTicketDetailDTOS1.get(0).getWorkPlanId());
                        tPrdWorkTicketDTO.setWorkDate(workTicketDetailDTOS1.get(0).getWorkDate());
                        tPrdWorkTicketDTO.setClassCode(workTicketDetailDTOS1.get(0).getClassCode());
                        tPrdWorkTicketDTO.setClassName(workTicketDetailDTOS1.get(0).getClassName());
                        tPrdWorkTicketDTO.setCompanyId(workTicketDetailDTOS1.get(0).getCompanyId());
                        tPrdWorkTicketDTO.setCompanyName(workTicketDetailDTOS1.get(0).getCompanyName());
                        //调用计件公共方法
                        SpringUtils.getBean(TPrdWorkTicketNewServiceImpl.class).commonUpdateSalary(tPrdWorkTicketDTO,workTicketDetailDTOS1,true,false,"整船调整");
                    }
                }
            }

            }
        }
    }

    @Override
    public void updateShipAdjustStatus(Long shipvoyageItemId, String allotType) {
        TWholeShipAdjustmentQueryDTO queryDTO = new TWholeShipAdjustmentQueryDTO();
        queryDTO.setShipVoyageItemId(shipvoyageItemId);
        List<Map<Long,String>> shipInfo = mapper.getShipInfo(queryDTO);
        if(shipInfo == null){
            throw new BusinessRuntimeException("船名航次的不存在");
        }
        List<String> dynamicTypeCodeList = shipInfo.stream().map(o -> String.valueOf(o.get("dynamicTypeCode"))).collect(Collectors.toList());
        if(!dynamicTypeCodeList.stream().anyMatch(o->o.equals(ShipStatusEnum.WANGONG.getCode()))){
            throw new BusinessRuntimeException("船舶尚未完工");
        }
        if(shipInfo.get(0).get("loadUnload").equals("卸")) {
            if (StringUtils.isNotBlank(String.valueOf(shipInfo.get(0).get("isShipClear")))) {
                if ("1".equals(String.valueOf(shipInfo.get(0).get("isShipClear")))) {

                } else {
                    throw new BusinessRuntimeException("船舶尚未完工确认");
                }
            } else {
                throw new BusinessRuntimeException("船舶尚未完工确认");
            }
        }

        if(FOR_MACHINE.equals(allotType)){
            mapper.updateShipStatus(shipvoyageItemId,"10");
        }else if(FOR_LABOR.equals(allotType)){
            mapper.updateShipStatusForLabor(shipvoyageItemId,"10");
        }else{
            throw new BusinessRuntimeException("分配参数类型错误！");
        }
    }

    @Override
    public Map<String, Object> getShipPLanTicketStatus(TWholeShipAdjustmentQueryDTO queryDTO) {

        HashMap<String, Object> result = Maps.newHashMap();

        List<Map<String,Object>> mapperResult= mapper.getShipPLanTicketStatus(queryDTO);
        if(CollectionUtils.isEmpty(mapperResult)){
            result.put("tmpFlag",Boolean.FALSE);
        }else{
            result.put("tmpFlag",Boolean.TRUE);
        }

        result.put("resultMsg",mapperResult.stream().map(o->String.valueOf(o.get("errorMsg"))+"</br>").collect(Collectors.joining()));

        return result;
    }

    @Override
    public void personClearConfirm(TWholeShipAdjustmentExaminePO po) {
        TWholeShipAdjustmentExaminePO detailPo =mapper.getDeatilById(po.getShipvoyageItemId());
        if(detailPo != null){
            if(detailPo.getShipMacExamineBy()!=null){
                po.setIsShipClear("1");
            }else {
                po.setIsShipClear("0");
            }
        }else{
            throw new BusinessRuntimeException("确实必要更新数据");
        }
        mapper.personClearConfirm(po);
    }

    @Override
    public void macClearConfirm(TWholeShipAdjustmentExaminePO po) {
        TWholeShipAdjustmentExaminePO detailPo =mapper.getDeatilById(po.getShipvoyageItemId());
        if(detailPo != null){
            if(detailPo.getShipPersonExamineBy()!=null){
                po.setIsShipClear("1");
            }else {
                po.setIsShipClear("0");
            }
        }else{
            throw new BusinessRuntimeException("确实必要更新数据");
        }
        mapper.macClearConfirm(po);
    }

    @Override
    public void updateShipAdjustClearPersonStatus(Long shipvoyageItemId) {
        mapper.updateShipAdjustClearPersonStatus(shipvoyageItemId);
    }

    @Override
    public void updateShipAdjustClearMacStatus(Long shipvoyageItemId) {
        mapper.updateShipAdjustClearMacStatus(shipvoyageItemId);
    }
}
